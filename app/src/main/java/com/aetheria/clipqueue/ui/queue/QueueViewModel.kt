package com.aetheria.clipqueue.ui.queue

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aetheria.clipqueue.data.ClipboardItem
import com.aetheria.clipqueue.data.ClipQueueRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

@HiltViewModel
class QueueViewModel @Inject constructor(
    private val repository: ClipQueueRepository
) : ViewModel() {

    fun getAllItems(): Flow<List<ClipboardItem>> = flow {
        emit(repository.getAll())
    }

    suspend fun addItem(content: String, type: ItemType) {
        val currentItems = repository.getAll()
        val newPosition = currentItems.lastOrNull()?.position ?: -1
        repository.insert(
            ClipboardItem(
                content = content,
                type = type,
                position = newPosition + 1
            )
        )
    }

    suspend fun deleteItem(id: Long) {
        repository.delete(id)
    }

    suspend fun clearAll() {
        repository.clearAll()
    }
}