package app.type.theme

import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.FiniteAnimationSpec
import androidx.compose.animation.core.tween
import androidx.compose.ui.unit.dp

// Tokens de movimento e dimensão — tudo que anima ou mede passa por aqui.
object Motion {
    const val HOVER = 140
    const val SELECT = 180
    const val NOTE = 200
    const val PANEL = 220
    const val SAVE = 200

    private val easing = CubicBezierEasing(0.2f, 0f, 0f, 1f)

    fun <T> spec(ms: Int): FiniteAnimationSpec<T> = tween(ms, easing = easing)
}

object Dimens {
    val radius = 6.dp
    val sidebarWidth = 230.dp
}
