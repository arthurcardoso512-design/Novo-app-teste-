package com.example.data.local

import androidx.room.TypeConverter
import com.example.data.models.SessionExercise
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory

class Converters {
  private val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
  private val listType = Types.newParameterizedType(List::class.java, SessionExercise::class.java)
  private val adapter = moshi.adapter<List<SessionExercise>>(listType)

  @TypeConverter
  fun fromSessionExerciseList(list: List<SessionExercise>?): String {
    return if (list != null) adapter.toJson(list) else "[]"
  }

  @TypeConverter
  fun toSessionExerciseList(json: String?): List<SessionExercise> {
    if (json.isNullOrEmpty()) return emptyList()
    return try {
      adapter.fromJson(json) ?: emptyList()
    } catch (e: Exception) {
      emptyList()
    }
  }
}
