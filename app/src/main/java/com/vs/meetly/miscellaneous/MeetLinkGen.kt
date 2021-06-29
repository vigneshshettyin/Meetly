package com.vs.meetly.miscellaneous

object LinkPicker {
    val sampleLink = listOf<String>(
        "the-best-of-both-worlds",
        "speak-of-the-devil",
        "see-eye-to-eye",
        "once-in-a-blue-moon",
        "when-pigs-fly",
        "a-piece-of-cake",
        "let-the-cat-out-of-the-bag",
        "to-feel-under-the-weather",
        "to-cost-an-arm-and-a-leg",
        "stealing-someones-thunder",
        "the-elephant-in-the-room",
        "the-last-straw",
        "giving-someone-the-cold-shoulder",
        "getting-a-taste-of-your-own-medicine",
        "bite-the-bullet",
        "no-pain-no-gain",
        "let-someone-off-the-hook",
        "call-it-a-day",
        "a-blessing-in-disguise",
        "to-hit-the-nail-on-the-head",
        "break-a-leg",
        "you-can’t-judge-a-book-by-its-cover",
        "to-add-insult-to-injury",
        "to-cut-corners",
        "to-kill-two-birds-with-one-stone",
    )

    fun getLink(): String {
        return sampleLink.shuffled().take(1).joinToString()
    }
}