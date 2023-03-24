package ru.fi.englishtrainer.utils

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.platform.LocalContext
import ru.fi.englishtrainer.database.firebase.AppFireBaseFirebaseRepository
import ru.fi.englishtrainer.database.room.repository.RoomRepository
import ru.fi.englishtrainer.isOnline

object Constants{
    var FIREBASE_REPOSITORY : AppFireBaseFirebaseRepository? = null
    lateinit var ROOM_REPOSITORY : RoomRepository
}

