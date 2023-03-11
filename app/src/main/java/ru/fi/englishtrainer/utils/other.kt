package ru.fi.englishtrainer.utils

import android.annotation.SuppressLint
import android.content.Context
import ru.fi.englishtrainer.database.AppFireBaseRepository

@SuppressLint("StaticFieldLeak")
object Constants{
    val REPOSITORY = AppFireBaseRepository()
    lateinit var context : Context
}

