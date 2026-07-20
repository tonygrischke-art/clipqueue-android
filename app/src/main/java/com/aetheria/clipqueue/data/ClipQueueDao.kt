package com.aetheria.clipqueue.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface ClipQueueDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: ClipboardItem)

    @Query("SELECT * FROM clipboard_items ORDER BY position ASC")
    suspend fun getAll(): List<ClipboardItem>

    @Query("DELETE FROM clipboard_items WHERE id = :id")
    suspend fun delete(id: Long)

    @Query("DELETE FROM clipboard_items")
    suspend fun clearAll()

    @Query("UPDATE clipboard_items SET position = position - 1 WHERE position > :position")
    suspend fun decrementPositionsAfter(position: Int)
}