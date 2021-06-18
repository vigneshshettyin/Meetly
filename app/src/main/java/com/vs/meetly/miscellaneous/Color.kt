package com.vs.meetly.miscellaneous

fun randColor():String{
    val colorList=listOf("#7209B7","#EF233C","#F7B801","#06D6A0","#F72585","#00BBF9")

    return colorList.shuffled().take(1).joinToString()
}