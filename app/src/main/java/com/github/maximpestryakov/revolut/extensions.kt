package com.github.maximpestryakov.revolut

import android.content.Context
import android.graphics.drawable.Drawable
import java.math.BigDecimal

fun BigDecimal.stripTrailingZerosFixed(): BigDecimal {
    return if (signum() == 0) BigDecimal.ZERO else stripTrailingZeros()
}

fun Context.getDrawable(name: String): Drawable? {
    val resourceId = resources.getIdentifier(name, "drawable", packageName)
    return if (resourceId != 0) getDrawable(resourceId) else null
}
