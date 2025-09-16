package org.easy.wallet.domain

import com.trustwallet.core.HDWallet
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import org.easy.wallet.data.interfaces.IChainAdapter
import org.easy.wallet.data.repository.AccountRepositoryImpl
import org.easy.wallet.data.repository.TokenRepository
import org.easy.wallet.model.Address
import org.easy.wallet.model.Amount
import org.easy.wallet.model.FungibleTokenMeta
import org.easy.wallet.model.NativeTokenMeta
import org.easy.wallet.model.TokenHolding
import org.easy.wallet.model.TokenId
import org.easy.wallet.model.zero

class FetchTokenInformationUseCase(
  private val accountRepository: AccountRepositoryImpl,
  private val tokenRepository: TokenRepository,
  private val chainAdapters: Map<String, IChainAdapter>
) {
  operator fun invoke(tokenId: TokenId): Flow<TokenHolding?> = flow {
    val account = accountRepository.activeAccount() ?: return@flow
    val token = tokenRepository.getById(tokenId = tokenId.value) ?: return@flow

    val adapter = chainAdapters[token.chainId.value] ?: return@flow

    val hdWallet = HDWallet(account.mnemonic, "")
    val address = hdWallet.address(token.chainId)

    val meta = token.contract?.let {
      FungibleTokenMeta(
        id = token.tokenId,
        chainId = token.chainId,
        standard = token.standard,
        name = token.name,
        symbol = token.symbol,
        decimals = token.decimals,
        contract = Address(it),
        logoUrl = token.iconUrl
      )
    } ?: NativeTokenMeta(
      id = token.tokenId,
      chainId = token.chainId,
      standard = token.standard,
      name = token.name,
      symbol = token.symbol,
      decimals = token.decimals,
      logoUrl = token.iconUrl
    )
    emit(value = meta.zero(address = address))

    val balance = adapter.getBalance(
      account = address,
      contract = token.contract
    )

    emit(
      value = TokenHolding(
        asset = meta,
        amount = Amount(raw = balance, decimals = meta.decimals),
        address = address
      )
    )
  }
}