package org.easy.wallet.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImagePainter
import coil3.compose.rememberAsyncImagePainter
import easywallet.composeapp.generated.resources.Res
import easywallet.composeapp.generated.resources.compose_multiplatform
import org.jetbrains.compose.resources.painterResource

@Composable
fun DynamicAsyncImage(
  imageUrl: String?,
  contentDescription: String?,
  modifier: Modifier = Modifier,
  placeholder: Painter = painterResource(Res.drawable.compose_multiplatform),
  tint: Color? = null
) {
  var isLoading by remember { mutableStateOf(true) }
  var isError by remember { mutableStateOf(false) }
  val imageLoader = rememberAsyncImagePainter(
    model = imageUrl,
    onState = { state ->
      isLoading = state is AsyncImagePainter.State.Loading
      isError = state is AsyncImagePainter.State.Error
    }
  )
  val isLocalInspection = LocalInspectionMode.current
  Box(
    modifier = modifier,
    contentAlignment = Alignment.Center
  ) {
    if (isLoading && !isLocalInspection) {
      // Display a progress bar while loading
      CircularProgressIndicator(
        modifier = Modifier
          .align(Alignment.Center)
          .size(80.dp),
        color = MaterialTheme.colorScheme.tertiary
      )
    }
    Image(
      modifier = Modifier.fillMaxSize(),
      contentScale = ContentScale.FillBounds,
      painter = if (isError.not() && !isLocalInspection) imageLoader else placeholder,
      contentDescription = contentDescription,
      colorFilter = tint?.let { ColorFilter.tint(it) }
    )
  }
}