package org.easy.wallet.database

import app.cash.sqldelight.db.QueryResult
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.db.SqlSchema

object InitialSchema : SqlSchema<QueryResult.Value<Unit>> by EasyWalletDatabase.Schema {
  override fun create(driver: SqlDriver): QueryResult.Value<Unit> {
    val result = EasyWalletDatabase.Schema.create(driver)
    val database = EasyWalletDatabase(driver)
    initLocalDatabase(database)
    return result
  }
}

internal fun initLocalDatabase(database: EasyWalletDatabase) {
  database.transaction {
    val q = database.tokensQueries

    // ---- Bitcoin mainnet (native) ----
    q.upsertToken(
      token_id = "btc:main/native",
      chain_id = "btc:main",
      standard = "NATIVE",
      contract = null,
      symbol = "BTC",
      name = "Bitcoin",
      decimals = 8,
      icon_url = "https://assets.coingecko.com/coins/images/1/thumb/bitcoin.png",
      enabled = 1,
      sort_order = 0,
      created_at = 0,
      updated_at = 0
    )

    // ---- Ethereum mainnet (native) ----
    q.upsertToken(
      token_id = "evm:1/native",
      chain_id = "evm:1",
      standard = "NATIVE",
      contract = null,
      symbol = "ETH",
      name = "Ethereum",
      decimals = 18,
      icon_url = "https://assets.coingecko.com/coins/images/279/thumb/ethereum.png",
      enabled = 1,
      sort_order = 0,
      created_at = 0,
      updated_at = 0
    )

    // ---- Ethereum ERC-20（lower case）----
    q.upsertToken(
      token_id = "evm:1/erc20:0xdac17f958d2ee523a2206206994597c13d831ec7",
      chain_id = "evm:1",
      standard = "ERC20",
      contract = "0xdac17f958d2ee523a2206206994597c13d831ec7",
      symbol = "USDT",
      name = "Tether USD",
      decimals = 6,
      icon_url = "https://assets.coingecko.com/coins/images/325/thumb/Tether.png",
      enabled = 1,
      sort_order = 10,
      created_at = 0,
      updated_at = 0
    )

    q.upsertToken(
      token_id = "evm:1/erc20:0xa0b86991c6218b36c1d19d4a2e9eb0ce3606eb48",
      chain_id = "evm:1",
      standard = "ERC20",
      contract = "0xa0b86991c6218b36c1d19d4a2e9eb0ce3606eb48",
      symbol = "USDC",
      name = "USD Coin",
      decimals = 6,
      icon_url = "https://assets.coingecko.com/coins/images/6319/thumb/USD_Coin_icon.png",
      enabled = 1,
      sort_order = 11,
      created_at = 0,
      updated_at = 0
    )

    q.upsertToken(
      token_id = "evm:1/erc20:0x6b175474e89094c44da98b954eedeac495271d0f",
      chain_id = "evm:1",
      standard = "ERC20",
      contract = "0x6b175474e89094c44da98b954eedeac495271d0f",
      symbol = "DAI",
      name = "Dai Stablecoin",
      decimals = 18,
      icon_url = "https://assets.coingecko.com/coins/images/9956/thumb/4943.png",
      enabled = 1,
      sort_order = 12,
      created_at = 0,
      updated_at = 0
    )

    q.upsertToken(
      token_id = "evm:1/erc20:0x2260fac5e5542a773aa44fbcfedf7c193bc2c599",
      chain_id = "evm:1",
      standard = "ERC20",
      contract = "0x2260fac5e5542a773aa44fbcfedf7c193bc2c599",
      symbol = "WBTC",
      name = "Wrapped BTC",
      decimals = 8,
      icon_url = "https://assets.coingecko.com/coins/images/7598/thumb/wrapped_bitcoin_wbtc.png",
      enabled = 1,
      sort_order = 20,
      created_at = 0,
      updated_at = 0
    )

    q.upsertToken(
      token_id = "evm:1/erc20:0x1f9840a85d5af5bf1d1762f925bdaddc4201f984",
      chain_id = "evm:1",
      standard = "ERC20",
      contract = "0x1f9840a85d5af5bf1d1762f925bdaddc4201f984",
      symbol = "UNI",
      name = "Uniswap",
      decimals = 18,
      icon_url = "https://assets.coingecko.com/coins/images/12504/thumb/uniswap-uni.png",
      enabled = 1,
      sort_order = 30,
      created_at = 0,
      updated_at = 0
    )

    q.upsertToken(
      token_id = "evm:1/erc20:0x6b3595068778dd592e39a122f4f5a5cf09c90fe2",
      chain_id = "evm:1",
      standard = "ERC20",
      contract = "0x6b3595068778dd592e39a122f4f5a5cf09c90fe2",
      symbol = "SUSHI",
      name = "Sushi",
      decimals = 18,
      icon_url = "https://assets.coingecko.com/coins/images/12271/thumb/sushiswap.png",
      enabled = 1,
      sort_order = 31,
      created_at = 0,
      updated_at = 0
    )

    q.upsertToken(
      token_id = "evm:1/erc20:0x514910771af9ca656af840dff83e8264ecf986ca",
      chain_id = "evm:1",
      standard = "ERC20",
      contract = "0x514910771af9ca656af840dff83e8264ecf986ca",
      symbol = "LINK",
      name = "Chainlink",
      decimals = 18,
      icon_url = "https://assets.coingecko.com/coins/images/877/thumb/chainlink-new-logo.png",
      enabled = 1,
      sort_order = 32,
      created_at = 0,
      updated_at = 0
    )

    // ---- Polygon (evm:137) ----
    q.upsertToken(
      token_id = "evm:137/native",
      chain_id = "evm:137",
      standard = "NATIVE",
      contract = null,
      symbol = "MATIC",
      name = "Polygon",
      decimals = 18,
      icon_url = "https://assets.coingecko.com/coins/images/4713/thumb/matic-token-icon.png",
      enabled = 1,
      sort_order = 0,
      created_at = 0,
      updated_at = 0
    )

    q.upsertToken(
      token_id = "evm:137/erc20:0x2791bca1f2de4661ed88a30c99a7a9449aa84174",
      chain_id = "evm:137",
      standard = "ERC20",
      contract = "0x2791bca1f2de4661ed88a30c99a7a9449aa84174",
      symbol = "USDC.e",
      name = "USD Coin (bridged)",
      decimals = 6,
      icon_url = "https://assets.coingecko.com/coins/images/6319/thumb/USD_Coin_icon.png",
      enabled = 1,
      sort_order = 10,
      created_at = 0,
      updated_at = 0
    )

    q.upsertToken(
      token_id = "evm:137/erc20:0xc2132d05d31c914a87c6611c10748aeb04b58e8f",
      chain_id = "evm:137",
      standard = "ERC20",
      contract = "0xc2132d05d31c914a87c6611c10748aeb04b58e8f",
      symbol = "USDT",
      name = "Tether USD",
      decimals = 6,
      icon_url = "https://assets.coingecko.com/coins/images/325/thumb/Tether.png",
      enabled = 1,
      sort_order = 11,
      created_at = 0,
      updated_at = 0
    )

    // ---- Arbitrum One (evm:42161) ----
    q.upsertToken(
      token_id = "evm:42161/native",
      chain_id = "evm:42161",
      standard = "NATIVE",
      contract = null,
      symbol = "ETH",
      name = "Ether (Arbitrum)",
      decimals = 18,
      icon_url = "https://assets.coingecko.com/coins/images/279/thumb/ethereum.png",
      enabled = 1,
      sort_order = 0,
      created_at = 0,
      updated_at = 0
    )

    q.upsertToken(
      token_id = "evm:42161/erc20:0xaf88d065e77c8cc2239327c5edb3a432268e5831",
      chain_id = "evm:42161",
      standard = "ERC20",
      contract = "0xaf88d065e77c8cc2239327c5edb3a432268e5831",
      symbol = "USDC",
      name = "USD Coin (native)",
      decimals = 6,
      icon_url = "https://assets.coingecko.com/coins/images/6319/thumb/USD_Coin_icon.png",
      enabled = 1,
      sort_order = 10,
      created_at = 0,
      updated_at = 0
    )

    q.upsertToken(
      token_id = "evm:42161/erc20:0xfd086bc7cd5c481dcc9c85ebe478a1c0b69fcbb9",
      chain_id = "evm:42161",
      standard = "ERC20",
      contract = "0xfd086bc7cd5c481dcc9c85ebe478a1c0b69fcbb9",
      symbol = "USDT",
      name = "Tether USD",
      decimals = 6,
      icon_url = "https://assets.coingecko.com/coins/images/325/thumb/Tether.png",
      enabled = 1,
      sort_order = 11,
      created_at = 0,
      updated_at = 0
    )
  }
}