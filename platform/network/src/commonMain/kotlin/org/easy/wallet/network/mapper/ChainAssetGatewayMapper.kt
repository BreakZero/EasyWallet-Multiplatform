package org.easy.wallet.network.mapper

import org.easy.wallet.model.ChainId
import org.easy.wallet.model.AssetId
import org.easy.wallet.model.AssetNetwork
import org.easy.wallet.model.AssetType
import org.easy.wallet.model.SupportedAsset
import org.easy.wallet.model.Token
import org.easy.wallet.model.TokenId
import org.easy.wallet.model.TokenStandard
import org.easy.wallet.model.Address
import org.easy.wallet.model.Amount
import org.easy.wallet.model.AssetBalance
import org.easy.wallet.network.model.dto.GatewayAssetDto
import org.easy.wallet.network.model.dto.GatewayBalanceDto
import kotlin.time.Clock
import kotlin.time.ExperimentalTime
import com.ionspin.kotlin.bignum.integer.BigInteger

fun ChainId.toGatewayEvmChainIdOrNull(): Int? = when (value) {
  ChainId.EVM_MAINNET.value -> 1
  ChainId.EVM_SEPOLIA.value -> 11155111
  else -> null
}

internal fun GatewayAssetDto.toModelChainIdOrNull(): ChainId? = when {
  chain == "bitcoin" && network == "mainnet" -> ChainId.BTC_MAINNET
  chain == "ethereum" && chainId == 1 -> ChainId.EVM_MAINNET
  chain == "ethereum" && chainId == 11155111 -> ChainId.EVM_SEPOLIA
  else -> null
}

internal fun GatewayAssetDto.toTokenStandardOrNull(): TokenStandard? = when (assetType) {
  "native" -> TokenStandard.NATIVE
  "erc20" -> TokenStandard.ERC20
  else -> null
}

internal fun GatewayAssetDto.toAssetTypeOrNull(): AssetType? = when (assetType) {
  "native" -> AssetType.NATIVE
  "erc20" -> AssetType.ERC20
  else -> null
}

internal fun GatewayAssetDto.toAssetOrNull(): SupportedAsset? {
  val modelChainId = toModelChainIdOrNull() ?: return null
  val modelType = toAssetTypeOrNull() ?: return null
  val symbol = symbol ?: return null
  val name = name ?: return null
  val decimals = decimals ?: return null
  val normalizedContract = contractAddress?.lowercase()
  val id = when (modelType) {
    AssetType.NATIVE -> AssetId("${modelChainId.value}/native")
    AssetType.ERC20 -> AssetId("${modelChainId.value}/erc20:${normalizedContract ?: return null}")
  }

  return SupportedAsset(
    id = id,
    chainId = modelChainId,
    network = if (network == "testnet") AssetNetwork.TESTNET else AssetNetwork.MAINNET,
    type = modelType,
    symbol = symbol,
    name = name,
    contractAddress = normalizedContract,
    decimals = decimals,
    source = source,
    updatedAt = updatedAt
  )
}

internal fun GatewayBalanceDto.toAssetBalanceOrNull(): AssetBalance? {
  val asset = asset.toAssetOrNull() ?: return null
  val rawAmount = runCatching { BigInteger.parseString(amount.raw) }.getOrNull() ?: return null
  return AssetBalance(
    asset = asset,
    address = Address(address),
    amount = Amount(rawAmount, amount.decimals),
    priceUsd = priceUsd,
    valueUsd = valueUsd,
    source = source,
    updatedAt = updatedAt
  )
}

@OptIn(ExperimentalTime::class)
internal fun GatewayAssetDto.toTokenOrNull(sortOrder: Int, existing: Token? = null): Token? {
  val modelChainId = toModelChainIdOrNull() ?: return null
  val standard = toTokenStandardOrNull() ?: return null
  val normalizedContract = contractAddress?.lowercase()
  val symbol = symbol ?: return null
  val name = name ?: return null
  val decimals = decimals ?: return null
  val tokenId = when (standard) {
    TokenStandard.NATIVE -> TokenId("${modelChainId.value}/native")
    TokenStandard.ERC20 -> TokenId("${modelChainId.value}/erc20:${normalizedContract ?: return null}")
    else -> return null
  }
  val now = Clock.System.now().toEpochMilliseconds()

  return Token(
    tokenId = tokenId,
    chainId = modelChainId,
    standard = standard,
    contract = normalizedContract,
    symbol = symbol,
    name = name,
    decimals = decimals,
    iconUrl = existing?.iconUrl,
    enabled = existing?.enabled ?: (status == "active"),
    sortOrder = existing?.sortOrder ?: sortOrder,
    createdAt = existing?.createdAt ?: now,
    updatedAt = now
  )
}
