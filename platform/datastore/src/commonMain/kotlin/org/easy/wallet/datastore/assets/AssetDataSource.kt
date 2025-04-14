package org.easy.wallet.datastore.assets

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.serialization.json.Json
import org.easy.wallet.datastore.assets.model.TokenInformation
import org.easy.wallet.datastore.assets.model.TokenList
import org.easy.wallet.datastore.assets.model.toAssets
import org.easy.wallet.model.Assets

class AssetDataSource internal constructor() {
  fun loadAssets(): Flow<List<Assets>> {
    val tokens = Json.decodeFromString<TokenList>(TOKEN_LIST).tokens
    return flow { emit(tokens.map(TokenInformation::toAssets)) }
  }
}