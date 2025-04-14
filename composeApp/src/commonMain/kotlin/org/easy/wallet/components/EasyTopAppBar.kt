package org.easy.wallet.components

import androidx.compose.foundation.layout.RowScope
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EasyTopAppBar(
  modifier: Modifier = Modifier,
  title: @Composable () -> Unit = {},
  actions: @Composable RowScope.() -> Unit = {},
  onBack: (() -> Unit)? = null
) {
  TopAppBar(
    title = title,
    navigationIcon = {
      IconButton(onClick = { onBack?.invoke() }) {
        Icon(Icons.Default.ArrowBack, contentDescription = null)
      }
    },
    actions = actions,
    modifier = modifier
  )
}