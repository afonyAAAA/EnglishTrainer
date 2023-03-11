package ru.fi.englishtrainer.database

import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import com.google.firebase.firestore.AggregateSource
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.runBlocking
import ru.fi.englishtrainer.models.EnglishWord
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class AppFireBaseRepository : DatabaseRepository {

    private val db = Firebase.firestore

    override suspend fun getEnglishWord(): List<EnglishWord> {

        val countQuery = db.collection("EnglishWord").count()

            return suspendCoroutine { continuation ->
                countQuery.get(AggregateSource.SERVER).addOnCompleteListener{ taskcount ->
                    db.collection("EnglishWord")
                        .orderBy("englishWord")
                        .startAt((0..taskcount.result.count - 10).random())
                        .limit(10)
                        .get()
                        .addOnSuccessListener {
                            continuation.resume(it.toObjects(EnglishWord::class.java))
                        }.addOnFailureListener { exception ->
                            Log.d("Error", exception.toString())
                        }
                }.addOnFailureListener{ exception ->
                    Log.d("Error", exception.toString())
                }
            }
    }

    override fun addEnglishWord() {

        val listEnglish : List<EnglishWord> = (
                listOf(
                    EnglishWord("began", "началось"),
                    EnglishWord("idea", "идея"),
                    EnglishWord("cut", "резать"),
                    EnglishWord("war", "война"),
                    EnglishWord("money", "деньги"),
                    EnglishWord("against", "против"),
                    EnglishWord("center", "центр"),
                    EnglishWord("appear", "появляться"),
                    EnglishWord("serve", "обслуживать"),
                    EnglishWord("road", "дорога"),
                    EnglishWord("love", "любовь"),
                    EnglishWord("map", "карта"),
                    EnglishWord("rain", "дождь"),
                    EnglishWord("towards", "по направлению"),
                    EnglishWord("rule", "правило"),
                    EnglishWord("govern", "управлять"),
                    EnglishWord("cold", "холодный"),
                    EnglishWord("pull", "тянуть"),
                    EnglishWord("simple", "простой"),
                    EnglishWord("reason", "причина"),
                ))

        listEnglish.forEach{ item ->
            db.collection("EnglishWord").add(item)
        }



    }

}