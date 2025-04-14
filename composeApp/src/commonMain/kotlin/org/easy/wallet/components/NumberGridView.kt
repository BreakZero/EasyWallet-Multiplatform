package org.easy.wallet.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun NumberGridView(
  modifier: Modifier = Modifier,
  numbers: List<String> = listOf("1", "2", "3", "4", "5", "6", "7", "8", "9", "", "0", "⌫"),
  onNumberClicked: (String) -> Unit,
  onDeleteClicked: () -> Unit
) {
  Column(
    verticalArrangement = Arrangement.spacedBy(2.dp),
    modifier = modifier.padding(16.dp)
  ) {
    numbers.windowed(size = 3, step = 3).forEach { row ->
      Row(
        horizontalArrangement = Arrangement.spacedBy(32.dp),
        modifier = Modifier.fillMaxWidth()
      ) {
        row.forEach { label ->
          Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
              .padding(12.dp)
              .weight(1f)
              .aspectRatio(1f)
              .clip(CircleShape)
              .background(Color.LightGray)
              .clickable(enabled = label.isNotEmpty()) {
                when (label) {
                  "⌫" -> onDeleteClicked()
                  "" -> Unit
                  else -> onNumberClicked(label)
                }
              }
          ) {
            Text(
              text = label,
              fontSize = 24.sp,
              color = if (label.isNotEmpty()) Color.Black else Color.Transparent
            )
          }
        }
      }
    }
  }
}