package org.easy.wallet.datastore.assets

internal const val TOKEN_LIST = """
  {
    "tokens": [
        {
            "chainId": 1,
            "asset": "ethereum",
            "type": "coin",
            "address": "",
            "name": "Ethereum",
            "symbol": "ETH",
            "decimals": 18,
            "logoURI": "https://trustwallet.com/assets/images/favicon.png"
        },
        {
            "chainId": 1,
            "asset": "c60_t0x1f9840a85d5aF5bf1D1762F925BDADdC4201F984",
            "type": "ERC20",
            "address": "0x1f9840a85d5aF5bf1D1762F925BDADdC4201F984",
            "name": "Uniswap",
            "symbol": "UNI",
            "decimals": 18,
            "logoURI": "https://assets-cdn.trustwallet.com/blockchains/ethereum/assets/0x1f9840a85d5aF5bf1D1762F925BDADdC4201F984/logo.png"
         },
         {
            "chainId": 1,
            "asset": "c60_t0xdAC17F958D2ee523a2206206994597C13D831ec7",
            "type": "ERC20",
            "address": "0xdAC17F958D2ee523a2206206994597C13D831ec7",
            "name": "Tether",
            "symbol": "USDT",
            "decimals": 6,
            "logoURI": "https://assets-cdn.trustwallet.com/blockchains/ethereum/assets/0xdAC17F958D2ee523a2206206994597C13D831ec7/logo.png"
        },
        {
            "chainId": 1,
            "asset": "c60_t0x6B175474E89094C44Da98b954EedeAC495271d0F",
            "type": "ERC20",
            "address": "0x6B175474E89094C44Da98b954EedeAC495271d0F",
            "name": "Dai",
            "symbol": "DAI",
            "decimals": 18,
            "logoURI": "https://assets-cdn.trustwallet.com/blockchains/ethereum/assets/0x6B175474E89094C44Da98b954EedeAC495271d0F/logo.png"
        },
        {
            "chainId": 1,
            "asset": "c60_t0x6B3595068778DD592e39A122f4f5a5cF09C90fE2",
            "type": "ERC20",
            "address": "0x6B3595068778DD592e39A122f4f5a5cF09C90fE2",
            "name": "SushiSwap",
            "symbol": "SUSHI",
            "decimals": 18,
            "logoURI": "https://assets-cdn.trustwallet.com/blockchains/ethereum/assets/0x6B3595068778DD592e39A122f4f5a5cF09C90fE2/logo.png"
        },
        {
            "chainId": 1,
            "asset": "c60_t0x95aD61b0a150d79219dCF64E1E6Cc01f0B64C4cE",
            "type": "ERC20",
            "address": "0x95aD61b0a150d79219dCF64E1E6Cc01f0B64C4cE",
            "name": "SHIBA INU",
            "symbol": "SHIB",
            "decimals": 18,
            "logoURI": "https://assets-cdn.trustwallet.com/blockchains/ethereum/assets/0x95aD61b0a150d79219dCF64E1E6Cc01f0B64C4cE/logo.png"
        },
        {
            "chainId": 1,
            "asset": "c60_t0xC02aaA39b223FE8D0A0e5C4F27eAD9083C756Cc2",
            "type": "ERC20",
            "address": "0xC02aaA39b223FE8D0A0e5C4F27eAD9083C756Cc2",
            "name": "WETH",
            "symbol": "WETH",
            "decimals": 18,
            "logoURI": "https://assets-cdn.trustwallet.com/blockchains/ethereum/assets/0xC02aaA39b223FE8D0A0e5C4F27eAD9083C756Cc2/logo.png"
        }
    ]
  }
"""