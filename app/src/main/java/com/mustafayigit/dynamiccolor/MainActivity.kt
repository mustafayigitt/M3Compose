package com.mustafayigit.dynamiccolor

import android.app.WallpaperManager
import android.content.Context
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.ImageLoader
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.mustafayigit.dynamiccolor.ui.theme.DynamicColorTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            App()
        }
    }
}


@Preview(showBackground = true, showSystemUi = true)
@Composable
fun DefaultPreview() {
    App()
}

@OptIn(ExperimentalComposeUiApi::class, androidx.compose.material3.ExperimentalMaterial3Api::class)
@Composable
fun App() {
    DynamicColorTheme {
        val context = LocalContext.current
        val keyboardController = LocalSoftwareKeyboardController.current

        var username by remember { mutableStateOf("") }

        val backgroundImages = listOf(
            "https://images.unsplash.com/photo-1648847680218-a7e7f8506299?ixlib=rb-1.2.1&ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=1049&q=80",
            "https://images.unsplash.com/photo-1648873274070-3ad22b22ed6e?ixlib=rb-1.2.1&ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=1064&q=80",
            "https://images.unsplash.com/photo-1648763336679-6c343b693b1b?ixlib=rb-1.2.1&ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=987&q=80"
        )

        var selectedImageUrl: String? by remember { mutableStateOf(null) }

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            item {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {

                    Text(
                        text = "Hello Material3",
                        style = MaterialTheme.typography.headlineMedium,
                    )

                    TextField(
                        value = username,
                        placeholder = { Text("Username") },
                        onValueChange = { username = it },
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                        keyboardActions = KeyboardActions(onDone = { keyboardController?.hide() })
                    )

                    Button(
                        onClick = {
                            keyboardController?.hide()
                            Toast.makeText(context, "Hello $username", Toast.LENGTH_SHORT).show()
                        },

                        ) {
                        Text(text = "Say Hello")
                    }
                }
            }

            item {
                LazyRow(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(horizontal = 24.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    items(backgroundImages) {
                        Card(
                            modifier = Modifier
                                .fillParentMaxWidth(1f)
                                .aspectRatio(3f / 4f)
                                .clip(shape = RoundedCornerShape(8.dp)),
                        ) {
                            Box {
                                Image(
                                    painter = rememberAsyncImagePainter(it),
                                    contentDescription = "background image",
                                    modifier = Modifier
                                        .fillParentMaxSize(1f)
                                        .clickable {
                                            selectedImageUrl =
                                                if (selectedImageUrl != it) it else null
                                        },
                                    contentScale = ContentScale.Crop
                                )
                                if (it == selectedImageUrl) {
                                    Icon(
                                        painter = painterResource(id = R.drawable.ic_done),
                                        contentDescription = "selection status",
                                        modifier = Modifier
                                            .size(32.dp)
                                            .align(Alignment.TopEnd)
                                            .offset(x = (-12).dp, y = 12.dp)
                                            .background(
                                                color = MaterialTheme.colorScheme.tertiary,
                                                shape = CircleShape
                                            )
                                            .padding(8.dp),
                                        tint = MaterialTheme.colorScheme.onTertiary
                                    )
                                }
                            }
                        }
                    }
                }
            }

            item {
                Button(
                    onClick = {
                        selectedImageUrl?.let { setWallpaper(context, it) }
                    },
                    enabled = selectedImageUrl != null,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary,
                        disabledContainerColor = MaterialTheme.colorScheme.secondary,
                        disabledContentColor = MaterialTheme.colorScheme.onSecondary
                    )
                ) {
                    Text(text = "Set selected as background")
                }
            }
        }
    }
}

fun setWallpaper(context: Context, selectedImageUrl: String) {
    val loader = ImageLoader(context)
    val downloadRequest = ImageRequest.Builder(context)
        .data(selectedImageUrl)
        .target { result ->
            val bitmap = (result as BitmapDrawable).bitmap
            val wallpaperManager = WallpaperManager.getInstance(context)
            wallpaperManager.setBitmap(bitmap)
        }
        .listener { _, result ->
            Toast.makeText(context, "SetWallpaper result: $result", Toast.LENGTH_SHORT).show()
        }
        .build()
    loader.enqueue(downloadRequest)
}
