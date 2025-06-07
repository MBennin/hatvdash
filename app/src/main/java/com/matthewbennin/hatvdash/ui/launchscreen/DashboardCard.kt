package com.matthewbennin.hatvdash.ui.launchscreen

import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.input.key.*
import androidx.compose.ui.unit.dp
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.material3.Text
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import com.matthewbennin.hatvdash.MdiIconManager
import com.matthewbennin.hatvdash.ui.rememberMdiIconBitmap

@Composable
fun DashboardCard(
    title: String,
    mdiIcon: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {

    val context = LocalContext.current

    var isFocused by remember { mutableStateOf(false) }

    val tintColor = MaterialTheme.colorScheme.primary.toArgb()

    val iconBitmap by rememberMdiIconBitmap(mdiIcon, tintColor)

    Surface(
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = if (isFocused) 8.dp else 2.dp,
        modifier = modifier
            .padding(8.dp)
            .fillMaxWidth()
            .height(80.dp)
            .shadow(elevation = if (isFocused) 12.dp else 4.dp, shape = RoundedCornerShape(16.dp))
            .onFocusChanged { focusState -> isFocused = focusState.isFocused }
            .clickable { onClick() }
            .focusable()
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 16.dp)
        ) {
            if (iconBitmap != null) {
                Image(
                    bitmap = iconBitmap!!.asImageBitmap(),
                    contentDescription = title,
                    modifier = Modifier.size(48.dp)
                )
            }

            Spacer(Modifier.width(16.dp))

            Text(
                text = title,
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = if (isFocused) FontWeight.Bold else FontWeight.Normal,
                style = MaterialTheme.typography.titleMedium,
                textAlign = TextAlign.Start,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}
