package com.vs.meetly.miscellaneous

object ColorPicker {
    val colors = arrayOf("#7209B7", "#EF233C", "#F7B801", "#06D6A0", "#F72585", "#00BBF9")
    var currentColorIndex = 0

    fun getColor(): String {
        currentColorIndex = (currentColorIndex + 1) % colors.size
        return colors[currentColorIndex]
    }
}