package org.easy.wallet.feature.wallet.password

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import org.easy.wallet.components.EasyTopAppBar
import org.easy.wallet.components.NumberGridView
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun CreatePasswordScreen() {
  val viewModel: CreatePasswordViewModel = koinViewModel()
  Scaffold(
    contentWindowInsets = WindowInsets(0),
    bottomBar = {
      NumberGridView(
        modifier = Modifier.fillMaxWidth().wrapContentHeight(),
        onDeleteClicked = viewModel::delete,
        onNumberClicked = { viewModel.enterNumber(it) }
      )
    },
    topBar = {
      EasyTopAppBar()
    }
  ) {
    Row(
      modifier = Modifier.fillMaxSize().padding(it),
      horizontalArrangement = Arrangement.spacedBy(12.dp, Alignment.CenterHorizontally),
      verticalAlignment = Alignment.CenterVertically
    ) {
      repeat(6) { index ->
        Box(
          contentAlignment = Alignment.Center,
          modifier = Modifier
            .size(24.dp)
            .border(1.dp, Color.Gray, CircleShape)
        ) {
          if (index < viewModel.password.text.length) {
            Box(
              modifier = Modifier
                .size(12.dp)
                .background(Color.Black, CircleShape)
            )
          }
        }
      }
    }
  }
}