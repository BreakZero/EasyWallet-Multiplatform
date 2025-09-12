package org.easy.wallet.domain

import com.trustwallet.core.HDWallet
import org.easy.wallet.data.interfaces.IChainAdapter
import org.easy.wallet.data.repository.AccountRepositoryImpl
import org.easy.wallet.data.repository.TokenRepository
import org.easy.wallet.model.Address
import org.easy.wallet.model.Amount
import org.easy.wallet.model.FungibleTokenMeta
import org.easy.wallet.model.NativeTokenMeta
import org.easy.wallet.model.TokenHolding
import org.easy.wallet.model.TokenId

class FetchTokenInformationUseCase(
  private val accountRepository: AccountRepositoryImpl,
  private val tokenRepository: TokenRepository,
  private val chainAdapters: Map<String, IChainAdapter>
) {
  suspend operator fun invoke(tokenId: TokenId): TokenHolding? {
    val account = accountRepository.activeAccount() ?: return null
    val token = tokenRepository.getById(tokenId = tokenId.value) ?: return null

    val adapter = chainAdapters[token.chainId.value] ?: return null

    val hdWallet = HDWallet(account.mnemonic, "")
    val address = hdWallet.address(token)
    val balance = adapter.getBalance(
      account = address,
      contract = token.contract
    )

    val meta = token.contract?.let {
      FungibleTokenMeta(
        id = token.tokenId,
        name = token.name,
        symbol = token.symbol,
        decimals = token.decimals,
        contract = Address(it),
        logoUrl = token.iconUrl
      )
    } ?: NativeTokenMeta(
      id = token.tokenId,
      name = token.name,
      symbol = token.symbol,
      decimals = token.decimals,
      logoUrl = token.iconUrl
    )

    return TokenHolding(
      asset = meta,
      amount = Amount(raw = balance, decimals = meta.decimals),
      address = address
    )
  }
}