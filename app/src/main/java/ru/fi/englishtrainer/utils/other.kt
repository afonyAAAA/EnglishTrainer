package ru.fi.englishtrainer.utils

import android.annotation.SuppressLint
import ru.fi.englishtrainer.database.firebase.AppFireBaseFirebaseRepository
import ru.fi.englishtrainer.database.room.repository.RoomRepository

@SuppressLint("StaticFieldLeak")
object Constants{
    val FIREBASE_REPOSITORY = AppFireBaseFirebaseRepository()

    lateinit var ROOM_REPOSITORY : RoomRepository
}

