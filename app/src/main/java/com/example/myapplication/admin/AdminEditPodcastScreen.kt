package com.example.myapplication.admin

import android.content.ContentResolver
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.rememberImagePainter
import com.example.myapplication.R
import com.example.myapplication.login.LoginViewModel
import com.example.myapplication.viewmodel.PodcastViewModel
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.io.ByteArrayOutputStream

@Composable
fun AdminEditPodcastScreen(
    loginViewModel: LoginViewModel? = null,
    onNavToAdminPodcastPage: () -> Unit,
    podcastId: String,
) {
    val podcastViewModel: PodcastViewModel = viewModel()
    val podcasts by podcastViewModel.podcasts.collectAsState()
    val podcast = podcasts.find { it.podcastId == podcastId }

    Surface(
        color = Color(28, 28, 28)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
            ) {
                IconButton(onClick = { onNavToAdminPodcastPage.invoke() }) {
                    Icon(
                        Icons.Default.ArrowBack, contentDescription = "Home",
                        tint = Color.White
                    )
                }

                Text(
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center,
                    text = "Edit podcast",
                    fontSize = 18.sp,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.size(20.dp))

            val context = LocalContext.current
            var artist by remember { mutableStateOf("") }
            var podcastName by remember { mutableStateOf("") }
            var audioUri by remember { mutableStateOf<Uri?>(null) }

            LazyColumn(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                item {
                    Spacer(modifier = Modifier.size(10.dp))

                    Text(
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center,
                        text = "Old podcast name is: ${podcast?.podcastName}",
                        fontSize = 16.sp,
                        color = Color.White,
                    )

                    Spacer(modifier = Modifier.size(10.dp))
                }

                item {
                    OutlinedTextField(
                        value = podcastName,
                        onValueChange = { podcastName = it },
                        keyboardOptions = KeyboardOptions(
                            imeAction = ImeAction.Done,
                        ),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(50, 205, 50),
                            unfocusedBorderColor = Color.White,
                        ),
                        textStyle = TextStyle.Default.copy(fontSize = 18.sp, color = Color.White),
                        label = {
                            Text(
                                "Enter the podcast name",
                                fontSize = 18.sp,
                                color = Color.White
                            )
                        }
                    )
                }

                item {
                    Spacer(modifier = Modifier.size(10.dp))

                    Text(
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center,
                        text = "The old artist name is: ${podcast?.artist}",
                        fontSize = 16.sp,
                        color = Color.White,
                    )

                    Spacer(modifier = Modifier.size(10.dp))
                }

                item {
                    OutlinedTextField(
                        value = artist,
                        onValueChange = { artist = it },
                        keyboardOptions = KeyboardOptions(
                            imeAction = ImeAction.Done,
                        ),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(50, 205, 50),
                            unfocusedBorderColor = Color.White,
                        ),
                        textStyle = TextStyle.Default.copy(fontSize = 18.sp, color = Color.White),
                        label = {
                            Text(
                                "Enter artist name",
                                fontSize = 18.sp,
                                color = Color.White
                            )
                        }
                    )
                }

                item {
                    Spacer(modifier = Modifier.size(10.dp))
                }

                item {
                    val bitmap = remember {
                        mutableStateOf<Bitmap?>(null)
                    }

                    val launchImage = rememberLauncherForActivityResult(
                        contract = ActivityResultContracts.GetContent()
                    ) { uri ->
                        uri?.let { imageUri ->
                            val contentResolver: ContentResolver = context.contentResolver
                            bitmap.value = if (Build.VERSION.SDK_INT < 28) {
                                MediaStore.Images.Media.getBitmap(contentResolver, imageUri)
                            } else {
                                val source = ImageDecoder.createSource(contentResolver, imageUri)
                                ImageDecoder.decodeBitmap(source)
                            }
                        }
                    }

                    Text(
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center,
                        text = "Old background photos podcast",
                        fontSize = 16.sp,
                        color = Color.White,
                    )

                    podcast?.imageUrl?.let { imageUrl ->
                        Image(
                            modifier = Modifier
                                .size(150.dp)
                                .padding(10.dp)
                                .clip(RoundedCornerShape(10)),
                            painter = rememberImagePainter(imageUrl),
                            contentDescription = ""
                        )
                    }

                    val launchAudio = rememberLauncherForActivityResult(
                        contract = ActivityResultContracts.GetContent(),
                        onResult = { uri ->
                            uri?.let { audioUri = it }
                        }
                    )


                    bitmap.value?.let {
                        Image(
                            modifier = Modifier
                                .size(150.dp)
                                .padding(10.dp)
                                .clip(RoundedCornerShape(10)),
                            bitmap = it.asImageBitmap(),
                            contentDescription = ""
                        )
                    }

                    Button(
                        onClick = {
                            launchImage.launch("image/*")
                        },
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.image),
                            contentDescription = "Select podcast image"
                        )

                        Spacer(modifier = Modifier.size(10.dp))

                        Text(text = "Select podcast image")
                    }

                    audioUri?.let { uri ->
                        Text(
                            text = "$uri",
                            fontSize = 18.sp,
                            color = Color.White
                        )
                    }

                    Spacer(modifier = Modifier.size(10.dp))

                    Button(
                        onClick = {
                            launchAudio.launch("audio/*")
                        },
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.audio),
                            contentDescription = "Select audio file"
                        )

                        Spacer(modifier = Modifier.size(10.dp))

                        Text(text = "Select audio file")
                    }

                    Spacer(modifier = Modifier.size(10.dp))

                    Button(
                        onClick = {
                            if (podcastName.isBlank()) {
                                podcastName = podcast?.podcastName.orEmpty()
                            }
                            if (artist.isBlank()) {
                                artist = podcast?.artist.orEmpty()
                            }

                            if (bitmap.value != null) {
                                uploadMusicImageToFirebase(bitmap.value!!) { newImageUrl ->
                                    podcast?.imageUrl?.let { oldImageUrl ->
                                        podcastViewModel.deleteImageFromStorage(oldImageUrl)
                                    }

                                    if (audioUri != null) {
                                        uploadAudio(audioUri!!, FirebaseStorage.getInstance().reference) { newAudioUrl ->
                                            podcast?.audioUrl?.let { oldAudio ->
                                                podcastViewModel.deleteAudioFromStorage(oldAudio)
                                            }

                                            podcastViewModel.editPodcast(
                                                podcastId,
                                                artist,
                                                newAudioUrl,
                                                newImageUrl,
                                                podcastName
                                            )
                                        }
                                    } else {
                                        podcastViewModel.editPodcast(
                                            podcastId,
                                            artist,
                                            podcast?.audioUrl.orEmpty(),
                                            newImageUrl,
                                            podcastName
                                        )
                                    }
                                }
                            } else if (audioUri != null) {
                                uploadAudio(audioUri!!, FirebaseStorage.getInstance().reference) { newAudioUrl ->
                                    podcast?.audioUrl?.let { oldAudio ->
                                        podcastViewModel.deleteAudioFromStorage(oldAudio)
                                    }

                                    podcastViewModel.editPodcast(
                                        podcastId,
                                        artist,
                                        newAudioUrl,
                                        podcast?.imageUrl.orEmpty(),
                                        podcastName
                                    )
                                }
                            } else {
                                podcastViewModel.editPodcast(
                                    podcastId,
                                    artist,
                                    podcast?.audioUrl.orEmpty(),
                                    podcast?.imageUrl.orEmpty(),
                                    podcastName
                                )
                            }
                        }
                    ) {
                        Text(text = "Save information")
                    }
                }
            }
        }
    }
}

fun uploadPodcastImageToFirebase(bitmap: Bitmap, callback: (String) -> Unit) {
    val storageRef = FirebaseStorage.getInstance().reference

    val imageFileName = "${System.currentTimeMillis()}.jpg"
    val imageRef = storageRef.child("images/$imageFileName")

    val baos = ByteArrayOutputStream()
    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
    val imageData = baos.toByteArray()

    val uploadTask = imageRef.putBytes(imageData)

    uploadTask.addOnSuccessListener { taskSnapshot ->
        imageRef.downloadUrl.addOnSuccessListener { uri ->
            val imageUrl = uri.toString()
            callback(imageUrl)
        }.addOnFailureListener {
            callback("")
        }
    }.addOnFailureListener {
        callback("")
    }
}

private fun uploadAudio(audioUri: Uri, storageRef: StorageReference, callback: (String) -> Unit) {
    val audioFileName = "${System.currentTimeMillis()}.mp3"
    val audioRef = storageRef.child("audios/$audioFileName")

    val uploadAudioTask = audioRef.putFile(audioUri)
    uploadAudioTask.addOnSuccessListener { taskSnapshot ->
        audioRef.downloadUrl.addOnSuccessListener { uri ->
            val audioUrl = uri.toString()
            callback(audioUrl)
        }.addOnFailureListener {
            callback("")
        }
    }.addOnFailureListener {
        callback("")
    }
}