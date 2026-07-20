package com.aetheria.clipqueue.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [ClipboardItem::class],
    version = 1,
    exportSchema = false
)
abstract class ClipQueueDatabase : RoomDatabase() {
    abstract fun clipQueueDao(): ClipQueueDao

    companion object {
        @Volatile
        private var INSTANCE: ClipQueueDatabase? = null

        fun getDatabase(context: Context): ClipQueueDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: buildDatabase(context).also { INSTANCE = it }
            }
        }

        private fun buildDatabase(context: Context): ClipQueueDatabase {
            return Room.databaseBuilder(
                context.applicationContext,
                ClipQueueDatabase::class.java,
                "clipqueue_database"
            ).build()
        }
    }
}