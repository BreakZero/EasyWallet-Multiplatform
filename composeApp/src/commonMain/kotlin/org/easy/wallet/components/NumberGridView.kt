package org.easy.wallet.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContent
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyHorizontalGrid
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Divider
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

enum class DigitKey(val key: Char) {
  ONE('1'),
  TWO('2'),
  THREE('3'),
  FOUR('4'),
  FIVE('5'),
  SIX('6'),
  SEVEN('7'),
  EIGHT('8'),
  NINE('9'),
  EMPTY(' '),
  ZERO('0'),
  DELETE('⌫')
}

@Composable
fun NumberGridView(
  modifier: Modifier = Modifier,
  digitKeys: List<DigitKey> = DigitKey.entries,
  onNumberClicked: (Char) -> Unit,
  onDeleteClicked: () -> Unit
) {
  LazyVerticalGrid(
    modifier = modifier.background(Color.Gray),
    columns = GridCells.Fixed(3),
    contentPadding = WindowInsets.safeContent.asPaddingValues()
  ) {
    items(digitKeys, key = { it.key }) { item ->
      Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
          .height(56.dp)
          .drawBehind {
            drawLine(
              color = Color.Red,
              start = Offset(0f, size.height),
              end = Offset(size.width, size.height),
              strokeWidth = 2f
            )
          }
          .clickable(enabled = item.key != ' ') {
            when (item) {
              DigitKey.DELETE -> onDeleteClicked()
              DigitKey.EMPTY -> Unit
              else -> onNumberClicked(item.key)
            }
          }
      ) {
        Text(
          text = "${item.key}",
          fontSize = 24.sp,
          color = if (item == DigitKey.EMPTY) Color.Transparent else Color.Black
        )
      }
    }

    item(span = { GridItemSpan(maxLineSpan) }) {
      VerticalDivider(color = Color.Red)
    }
  }
}