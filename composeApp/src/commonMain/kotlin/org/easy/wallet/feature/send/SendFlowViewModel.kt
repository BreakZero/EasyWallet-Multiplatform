package org.easy.wallet.feature.send

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ionspin.kotlin.bignum.decimal.BigDecimal
import com.ionspin.kotlin.bignum.integer.BigInteger
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.easy.wallet.data.transaction.TransferResult
import org.easy.wallet.domain.FetchAssetBalanceUseCase
import org.easy.wallet.domain.coinTypeForChain
import org.easy.wallet.domain.usecase.EstimateTransactionFeeUseCase
import org.easy.wallet.domain.usecase.SendTokenUseCase
import org.easy.wallet.domain.usecase.ValidateAddressUseCase
import org.easy.wallet.model.Address
import org.easy.wallet.model.AssetId

class SendFlowViewModel(
  private val fetchAssetBalanceUseCase: FetchAssetBalanceUseCase,
  private val estimateTransactionFeeUseCase: EstimateTransactionFeeUseCase,
  private val sendTokenUseCase: SendTokenUseCase,
  private val validateAddressUseCase: ValidateAddressUseCase,
  private val assetId: AssetId
) : ViewModel() {
  private val eventChannel = Channel<SendFlowEvent>()
  val event = eventChannel.receiveAsFlow()

  private val _uiState = MutableStateFlow(SendFlowState())
  val uiState: StateFlow<SendFlowState> = _uiState.asStateFlow()

  init {
    loadTokenInformation()
  }

  private fun loadTokenInformation() {
    fetchAssetBalanceUseCase(assetId)
      .onEach { assetBalance ->
        _uiState.update {
          it.copy(assetBalance = assetBalance, isLoading = false, error = null)
        }
      }.launchIn(viewModelScope)
  }

  fun handleAction(action: SendFlowAction) {
    when (action) {
      is SendFlowAction.OnRecipientChange -> onRecipientChange(action.recipient)
      is SendFlowAction.ContinueToAmount -> continueToAmount()
      is SendFlowAction.OnSendAmountChange -> onAmountChange(action.amount)
      is SendFlowAction.OnMemoChange -> _uiState.update { it.copy(memo = action.memo) }
      is SendFlowAction.UseMaxAmount -> useMaxAmount()
      is SendFlowAction.ReviewTransaction -> reviewTransaction()
      is SendFlowAction.ConfirmSend -> confirmSend()
      is SendFlowAction.DismissResult -> dismissResult()
      is SendFlowAction.GoBack -> eventChannel.trySend(SendFlowEvent.GoBack)
    }
  }

  private fun onRecipientChange(recipient: String) {
    val chainId = _uiState.value.assetBalance
      ?.asset
      ?.chainId
    val error = when {
      recipient.isBlank() -> null
      chainId != null && !validateAddressUseCase(recipient, chainId) -> AddressError.INVALID_FORMAT
      else -> null
    }
    _uiState.update { it.copy(recipientAddress = recipient, addressError = error) }
  }

  private fun continueToAmount() {
    val state = _uiState.value
    val chainId = state.assetBalance?.asset?.chainId

    val addressError = when {
      state.recipientAddress.isBlank() -> AddressError.EMPTY
      chainId != null && !validateAddressUseCase(state.recipientAddress, chainId) ->
        AddressError.INVALID_FORMAT
      state.assetBalance != null &&
        state.recipientAddress.equals(state.assetBalance.address.value, ignoreCase = true) ->
        AddressError.SAME_AS_SENDER
      else -> null
    }

    if (addressError != null) {
      _uiState.update { it.copy(addressError = addressError) }
      return
    }
    eventChannel.trySend(SendFlowEvent.NavigateToEnterAmount)
  }

  private fun onAmountChange(amount: String) {
    val error = validateAmount(amount)
    _uiState.update { it.copy(amount = amount, amountError = error) }
  }

  private fun validateAmount(amount: String): AmountError? {
    if (amount.isBlank()) return null
    val parsed = runCatching { BigDecimal.parseString(amount) }.getOrNull()
      ?: return AmountError.INVALID_FORMAT
    if (parsed <= BigDecimal.ZERO) return AmountError.ZERO
    val balance = _uiState.value.assetBalance ?: return null
    val rawAmount = toSmallestUnit(parsed, balance.asset.decimals)
    if (rawAmount > balance.amount.raw) return AmountError.EXCEEDS_BALANCE
    return null
  }

  private fun useMaxAmount() {
    val balance = _uiState.value.assetBalance ?: return
    val formatted = balance.amount.format(displayDecimals = balance.asset.decimals)
    _uiState.update { it.copy(amount = formatted, amountError = null) }
  }

  private fun reviewTransaction() {
    val state = _uiState.value
    val amountError = when {
      state.amount.isBlank() -> AmountError.EMPTY
      else -> validateAmount(state.amount)
    }
    if (amountError != null) {
      _uiState.update { it.copy(amountError = amountError) }
      return
    }
    estimateFeeAndNavigate()
  }

  private fun estimateFeeAndNavigate() {
    val state = _uiState.value
    val balance = state.assetBalance ?: return
    val senderAddress = balance.address

    viewModelScope.launch {
      _uiState.update { it.copy(isEstimatingFee = true) }
      try {
        val amount = toSmallestUnit(
          BigDecimal.parseString(state.amount),
          balance.asset.decimals
        )
        val fee = estimateTransactionFeeUseCase(
          asset = balance.asset,
          from = senderAddress,
          to = Address(state.recipientAddress),
          amount = amount
        )
        _uiState.update { it.copy(feePolicy = fee, isEstimatingFee = false) }
        eventChannel.trySend(SendFlowEvent.NavigateToReview)
      } catch (e: Exception) {
        _uiState.update { it.copy(isEstimatingFee = false) }
        eventChannel.trySend(SendFlowEvent.ShowError(e.message ?: "Fee estimation failed"))
      }
    }
  }

  private fun confirmSend() {
    val state = _uiState.value
    val balance = state.assetBalance ?: return
    val senderAddress = balance.address

    viewModelScope.launch {
      _uiState.update { it.copy(isSending = true) }
      val amount = toSmallestUnit(
        BigDecimal.parseString(state.amount),
        balance.asset.decimals
      )
      val coinType = coinTypeForChain(balance.asset.chainId)
      val result = sendTokenUseCase(
        asset = balance.asset,
        from = senderAddress,
        to = Address(state.recipientAddress),
        amount = amount,
        coinType = coinType,
        memo = state.memo.ifBlank { null }
      )
      val sendResult = when (result) {
        is TransferResult.Success -> SendResult.Success(result.txHash)
        is TransferResult.Error -> SendResult.Failure(result.message)
      }
      _uiState.update { it.copy(isSending = false, sendResult = sendResult) }
      eventChannel.trySend(SendFlowEvent.NavigateToResult)
    }
  }

  private fun dismissResult() {
    eventChannel.trySend(SendFlowEvent.NavigateToHome)
  }

  private fun toSmallestUnit(value: BigDecimal, decimals: Int): BigInteger {
    val scale = BigDecimal.TEN.pow(decimals.toLong())
    return (value * scale).toBigInteger()
  }
}
