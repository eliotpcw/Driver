package kz.kazpost.driver.utils

import android.content.Context
import android.widget.Toast


fun Context.toast(
    message: String?,
    long: Boolean = false
) = Toast.makeText(
    this,
    message,
    if (long) Toast.LENGTH_LONG else Toast.LENGTH_SHORT
).show()

fun Context.toast(
    message: ResourceString?,
    long: Boolean = false
) {
    toast(
        message?.format(this),
        long
    )
}