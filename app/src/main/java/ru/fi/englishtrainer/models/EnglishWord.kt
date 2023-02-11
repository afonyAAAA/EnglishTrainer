package ru.fi.englishtrainer.models

data class EnglishWord(
    var word: String = "",
    var translateWord: String = "",
    val colorId : Int = 0,
    var answered : Boolean = false,
    var correctly : Boolean = false
    )
