package com.aetheria.clipqueue.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "clipboard_items")
data class ClipboardItem(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val content: String,
    val type: ItemType,
    val timestamp: Long = System.currentTimeMillis(),
    val position: Int
)