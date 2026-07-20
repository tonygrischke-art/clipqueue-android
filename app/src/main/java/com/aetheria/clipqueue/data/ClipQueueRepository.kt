package com.aetheria.clipqueue.data

import android.content.Context
import javax.inject.Inject

class ClipQueueRepository @Inject constructor(
    private val context: Context
) {
    private val database = ClipQueueDatabase.getDatabase(context)

    suspend fun insert(item: ClipboardItem) {
        database.clipQueueDao().insert(item)
    }

    suspend fun getAll(): List<ClipboardItem> {
        return database.clipQueueDao().getAll()
    }

    suspend fun delete(id: Long) {
        val item = database.clipQueueDao().getAll().firstOrNull { it.id == id }
        if (item != null) {
            database.clipQueueDao().delete(id)
            database.clipQueueDao().decrementPositionsAfter(item.position)
        }
    }

    suspend fun clearAll() {
        database.clipQueueDao().clearAll()
    }
}