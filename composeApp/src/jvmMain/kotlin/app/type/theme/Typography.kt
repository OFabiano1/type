package app.type.theme

import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.platform.Font
import app.type.data.FontChoice

object TypeFonts {
    val JetBrainsMono: FontFamily = FontFamily(
        Font(resource = "fonts/JetBrainsMono-Regular.ttf", weight = FontWeight.Normal),
    )

    fun of(choice: FontChoice): FontFamily = when (choice) {
        FontChoice.MONO -> JetBrainsMono
        FontChoice.SANS -> FontFamily.SansSerif
        FontChoice.SERIF -> FontFamily.Serif
    }
}
