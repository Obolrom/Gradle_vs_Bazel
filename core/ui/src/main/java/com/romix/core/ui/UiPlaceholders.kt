package com.romix.core.ui

import java.util.ArrayList

data class UiText(val value: String)
data class UiColor(val argb: Int)
data class UiPadding(val left: Int, val top: Int, val right: Int, val bottom: Int)

data class UiButtonState(
    val text: UiText,
    val enabled: Boolean,
    val loading: Boolean
)

data class UiListItem(
    val id: Long,
    val title: String,
    val subtitle: String?,
    val selected: Boolean
)

object UiDefaults {
    val PrimaryColor = UiColor(0xFF2196F3.toInt())
    val ErrorColor = UiColor(0xFFF44336.toInt())

    fun defaultPadding(): UiPadding = UiPadding(16, 16, 16, 16)
}

fun formatTitle(name: String, index: Int): String =
    "Item #$index for $name"

fun generateItems(count: Int): List<UiListItem> {
    val result = ArrayList<UiListItem>(count)
    for (i in 0 until count) {
        result += UiListItem(
            id = i.toLong(),
            title = "Title $i",
            subtitle = if (i % 2 == 0) "Subtitle $i" else null,
            selected = i % 5 == 0
        )
    }
    return result
}

data class UiState1(val loading: Boolean, val error: String?)
data class UiState2(val loading: Boolean, val error: String?)
data class UiState3(val loading: Boolean, val error: String?)
data class UiState4(val loading: Boolean, val error: String?)
data class UiState5(val loading: Boolean, val error: String?)
data class UiState6(val loading: Boolean, val error: String?)
data class UiState7(val loading: Boolean, val error: String?)
data class UiState8(val loading: Boolean, val error: String?)
data class UiState9(val loading: Boolean, val error: String?)
data class UiState10(val loading: Boolean, val error: String?)
