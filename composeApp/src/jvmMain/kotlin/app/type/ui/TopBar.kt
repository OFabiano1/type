package app.type.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.type.data.Theme
import app.type.theme.LocalTypePalette
import app.type.theme.TypeFonts

@Composable
fun TopBar(theme: Theme, onToggleTheme: () -> Unit) {
    val palette = LocalTypePalette.current
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(36.dp)
            .background(palette.background)
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Spacer(Modifier.weight(1f))
        Text(
            text = if (theme == Theme.DARK) "tema: escuro" else "tema: claro",
            color = palette.textDim,
            fontSize = 12.sp,
            fontFamily = TypeFonts.JetBrainsMono,
            modifier = Modifier.clickable { onToggleTheme() },
        )
    }
}
