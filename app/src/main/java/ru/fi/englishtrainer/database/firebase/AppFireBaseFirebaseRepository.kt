package ru.fi.englishtrainer.database.firebase

import android.annotation.SuppressLint
import android.util.Log
import com.google.firebase.firestore.AggregateSource
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import ru.fi.englishtrainer.database.DatabaseFirebaseRepository
import ru.fi.englishtrainer.models.EnglishWord
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class AppFireBaseFirebaseRepository : DatabaseFirebaseRepository {

    private val db = Firebase.firestore

    @SuppressLint("SuspiciousIndentation")
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
}