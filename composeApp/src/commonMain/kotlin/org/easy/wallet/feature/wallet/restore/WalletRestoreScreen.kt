package org.easy.wallet.feature.wallet.restore

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MediumTopAppBar
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WalletRestoreScreen() {
  val viewModel: WalletRestoreViewModel = koinViewModel()
  Scaffold(
    modifier = Modifier.fillMaxSize(),
    topBar = {
      MediumTopAppBar(
        title = {
          Text("输入助记词(12,18,24个单词)")
        },
        navigationIcon = {
          IconButton(onClick = {}) {
            Icon(imageVector = Icons.Default.ArrowBack, contentDescription = null)
          }
        },
        actions = {
          IconButton(onClick = {}) {
            Icon(Icons.Default.Done, contentDescription = null)
          }
        }
      )
    },
    bottomBar = {
      Button(
        modifier = Modifier
          .fillMaxWidth()
          .navigationBarsPadding()
          .imePadding()
          .padding(bottom = 16.dp)
          .height(56.dp)
          .padding(horizontal = 16.dp),
        onClick = {},
        enabled = viewModel.mnemonic.text.isNotBlank()
      ) {
        Text(text = "Confirm")
      }
    }
  ) { innerPaddings ->
    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(innerPaddings)
    ) {
      BasicTextField(
        modifier = Modifier
          .fillMaxWidth()
          .defaultMinSize(minHeight = 56.dp)
          .padding(horizontal = 16.dp),
        state = viewModel.mnemonic
      )

      OutlinedButton(
        onClick = viewModel::copyFromClipboard,
        modifier = Modifier.wrapContentSize().align(Alignment.End).padding(end = 16.dp)
      ) {
        Row(
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          Icon(imageVector = Icons.Default.Call, contentDescription = null)
          Text("粘贴")
        }
      }
    }
  }
}