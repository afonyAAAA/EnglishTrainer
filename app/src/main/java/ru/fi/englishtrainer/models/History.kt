package ru.fi.englishtrainer.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import com.google.gson.reflect.TypeToken
import java.time.LocalDateTime
import java.util.*


@Entity(tableName = "history_table")
data class History(
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0,
    @SerializedName("date")
    @ColumnInfo
    val date: LocalDateTime,
    @SerializedName("percentCorrect")
    @ColumnInfo
    val percentCorrect: Int,
    @SerializedName("listResult")
    @ColumnInfo
    val listResult: List<EnglishWord>,
)


class Converters {
    @TypeConverter
    fun toListString(value: String): List<EnglishWord> {
        val listType = object :
            TypeToken<List<EnglishWord?>?>() {}.type
        return Gson().fromJson(value, listType)
    }

    @TypeConverter
    fun listResultToString(someObjects: List<EnglishWord?>?): String? {
        return Gson().toJson(someObjects)
    }


    @TypeConverter
    fun toDate(dateString: String?): LocalDateTime? {
        return if (dateString == null) {
            null
        } else {
            LocalDateTime.parse(dateString)
        }
    }

    @TypeConverter
    fun toDateString(date: LocalDateTime?): String? {
        return date?.toString()
    }
}

