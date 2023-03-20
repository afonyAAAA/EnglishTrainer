package ru.fi.englishtrainer.models

data class EnglishWord(
    var englishWord: String = "",
    var translatedWord: String = "",
    var answered : Boolean = false,
    var correctly : Boolean = false
)
