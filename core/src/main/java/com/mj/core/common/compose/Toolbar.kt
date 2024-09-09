package com.mj.core.common.compose

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import com.mj.core.theme.BargainPriceTheme
import com.mj.core.theme.Typography
import com.mj.core.theme.black

@Composable
fun Toolbar(
    modifier: Modifier,
    titleText: String = "",
    navigationImage: ImageVector,
    navigationAction: () -> Unit,
) {

    Column(modifier = modifier) {
        Box(
            contentAlignment = Alignment.Center
        ) {
            IconButton(
                modifier = Modifier.align(Alignment.CenterStart),
                onClick = navigationAction
            ) {
                Icon(
                    imageVector = navigationImage,
                    contentDescription = "",
                )
            }

            if (titleText.isNotBlank()) {
                Text(
                    modifier = Modifier.fillMaxWidth(),
                    text = titleText,
                    style = Typography.titleMedium,
                    color = black,
                    textAlign = TextAlign.Center,
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ToolbarPreview() {
    BargainPriceTheme {
        Toolbar(
            modifier = Modifier.fillMaxWidth(),
            titleText = "타이틀",
            navigationImage = Icons.AutoMirrored.Filled.ArrowBack,
            navigationAction = {}
        )
    }
}