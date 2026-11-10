package app.type.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.type.data.FontChoice
import app.type.data.Theme
import app.type.theme.LocalTypePalette
import app.type.theme.TypeFonts

@Composable
fun SettingsPanel(
    settings: EditorSettings,
    theme: Theme,
    onFont: (FontChoice) -> Unit,
    onFontSize: (Int) -> Unit,
    onLineHeight: (Int) -> Unit,
    onWidth: (Int) -> Unit,
    onToggleTheme: () -> Unit,
) {
    val p = LocalTypePalette.current
    val shape = RoundedCornerShape(10.dp)
    Column(
        modifier = Modifier
            .width(280.dp)
            .clip(shape)
            .background(p.surface)
            .border(1.dp, p.divider, shape)
            .padding(14.dp),
        verticalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(10.dp),
    ) {
        Text("ajustes", color = p.text, fontSize = 13.sp, fontFamily = TypeFonts.JetBrainsMono)
        Row(verticalAlignment = Alignment.CenterVertically) {
            Label("fonte")
            for (f in FontChoice.entries) {
                SoftButton(
                    text = f.name.lowercase(),
                    onClick = { onFont(f) },
                    color = if (f == settings.font) p.accent else p.textDim,
                )
            }
        }
        Stepper("tamanho", "${settings.fontSize}", { onFontSize(settings.fontSize - 1) }, { onFontSize(settings.fontSize + 1) })
        Stepper(
            "linha", "%.1f".format(settings.lineHeightPct / 100f),
            { onLineHeight(settings.lineHeightPct - 10) }, { onLineHeight(settings.lineHeightPct + 10) },
        )
        Stepper("largura", "${settings.width}", { onWidth(settings.width - 40) }, { onWidth(settings.width + 40) })
        Row(verticalAlignment = Alignment.CenterVertically) {
            Label("tema")
            SoftButton(if (theme == Theme.DARK) "escuro" else "claro", onToggleTheme, color = p.accent)
        }
    }
}

@Composable
private fun Label(text: String) {
    Text(
        text = text,
        color = LocalTypePalette.current.textDim,
        fontSize = 12.sp,
        fontFamily = TypeFonts.JetBrainsMono,
        modifier = Modifier.width(64.dp),
    )
}

@Composable
private fun Stepper(label: String, value: String, onMinus: () -> Unit, onPlus: () -> Unit) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Label(label)
        SoftButton("−", onMinus)
        Text(
            text = value,
            color = LocalTypePalette.current.text,
            fontSize = 12.sp,
            fontFamily = TypeFonts.JetBrainsMono,
            modifier = Modifier.width(44.dp).padding(horizontal = 4.dp),
        )
        SoftButton("+", onPlus)
    }
}
