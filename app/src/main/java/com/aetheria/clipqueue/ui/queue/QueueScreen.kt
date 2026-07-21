package com.aetheria.clipqueue.ui.queue

import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.aetheria.clipqueue.data.ClipboardItem
import com.aetheria.clipqueue.data.ItemType
import com.aetheria.clipqueue.ui.queue.QueueViewModel
import com.aetheria.clipqueue.R
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

@Composable
fun QueueScreen(viewModel: QueueViewModel = viewModel()) {
    val items by viewModel.getAllItems().collectAsState(initial = emptyList())
    val context = LocalContext.current
    val clipboardManager = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    val coroutineScope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Clipboard Queue",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Grab Clipboard Button
        Button(
            onClick = {
                rememberCoroutineScope().launch {
                    val clip = clipboardManager.primaryClip
                    if (clip != null && clip.getItemAt(0) != null) {
                        val text = clip.getItemAt(0).text.toString()
                        val itemType = classifyItemType(text)
                        viewModel.addItem(text, itemType)
                        Toast.makeText(context, "Added to queue", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(context, "No clipboard content", Toast.LENGTH_SHORT).show()
                    }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary
            )
        ) {
            Text("Grab Clipboard", color = MaterialTheme.colorScheme.onPrimary)
        }

        // Clear All Button
        Button(
            onClick = { rememberCoroutineScope().launch { viewModel.clearAll() } },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.error
            )
        ) {
            Text("Clear All", color = MaterialTheme.colorScheme.onError)
        }

        // Queue List
        LazyColumn {
            items(items) { item ->
                val iconRes = when (item.type) {
                    ItemType.URL -> R.drawable.ic_url
                    ItemType.EMAIL -> R.drawable.ic_email
                    ItemType.PHONE -> R.drawable.ic_phone
                    ItemType.CODE -> R.drawable.ic_code
                    ItemType.TEXT -> R.drawable.ic_text
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                        .clickable {
                            val clip = android.content.ClipData.newPlainText("", item.content)
                            clipboardManager.primaryClip = clip
                            Toast.makeText(context, "Copied to clipboard", Toast.LENGTH_SHORT).show()
                        }
                        .background(
                            color = MaterialTheme.colorScheme.surfaceVariant,
                            shape = RoundedCornerShape(8.dp)
                        ),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        Image(
                            painter = painterResource(id = iconRes),
                            contentDescription = item.type.toString(),
                            modifier = Modifier
                                .width(24.dp)
                                .height(24.dp)
                                .padding(end = 8.dp)
                        )
                        Text(
                            text = item.content,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            color = MaterialTheme.colorScheme.onSurface,
                            fontSize = 16.sp
                        )
                    }

                    Text(
                        text = "#${item.position + 1}",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 14.sp
                    )

                    // Swipe to delete (placeholder - actual swipe requires additional gesture handling)
                    Box(
                        modifier = Modifier
                            .width(48.dp)
                            .height(48.dp)
                            .align(Alignment.CenterVertically),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "✕",
                            color = MaterialTheme.colorScheme.error,
                            fontSize = 20.sp
                        )
                    }
                }
            }
        }
    }
}

fun classifyItemType(text: String): ItemType {
    val urlPattern = """(https?://[\w.-]+(?:/[\w.-]*)*)""".toRegex()
    val emailPattern = """[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}""".toRegex()
    val phonePattern = """\+?[1-9][\d\s\-()]{7,}\d""".toRegex()
    val codePattern = """^[\w\s{}()[\]<>;:,./\-+=*%&|~!@#\$^`]+\s*$""".toRegex()

    return when {
        urlPattern.containsMatchIn(text) -> ItemType.URL
        emailPattern.containsMatchIn(text) -> ItemType.EMAIL
        phonePattern.containsMatchIn(text) -> ItemType.PHONE
        codePattern.containsMatchIn(text) -> ItemType.CODE
        else -> ItemType.TEXT
    }
}