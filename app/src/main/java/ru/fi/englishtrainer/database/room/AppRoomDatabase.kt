package ru.fi.englishtrainer.database.room

import android.content.Context
import android.os.Parcel
import android.os.Parcelable
import android.provider.ContactsContract.CommonDataKinds.Note
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import ru.fi.englishtrainer.database.room.dao.TrainerRoomDao
import ru.fi.englishtrainer.models.Converters
import ru.fi.englishtrainer.models.History

@Database(entities = [History :: class], version = 1)
@TypeConverters(Converters::class)
abstract class AppRoomDatabase : RoomDatabase() {

    abstract fun getRoomDao() : TrainerRoomDao

    companion object{
        @Volatile
        private var INSTANCE: AppRoomDatabase? = null

        fun getInstance(context: Context) : AppRoomDatabase{
            return if(INSTANCE == null){
                INSTANCE = Room.databaseBuilder(
                    context,
                    AppRoomDatabase::class.java,
                    "trainer_database"
                ).build()
                INSTANCE as AppRoomDatabase
            }else INSTANCE as AppRoomDatabase
        }
    }
}