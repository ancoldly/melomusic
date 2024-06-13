package com.example.myapplication.admin

import android.content.ContentResolver
import android.content.Context
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.widget.Toast
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
import com.example.myapplication.R
import com.example.myapplication.login.LoginViewModel
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import java.io.ByteArrayOutputStream


@Composable
fun AdminAddPodcastScreen(
    loginViewModel: LoginViewModel? = null,
    onNavToAdminHomePage: () -> Unit,
    onNavToAdminPodcastPage: () -> Unit,
) {
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
                    text = "Add podcast",
                    fontSize = 18.sp,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            }

            val context = LocalContext.current
            var artist by remember { mutableStateOf("") }
            var podcastName by remember { mutableStateOf("") }
            var audioUri by remember { mutableStateOf<Uri?>(null) }

            Spacer(modifier = Modifier.size(20.dp))

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

            Spacer(modifier = Modifier.size(10.dp))

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

            Spacer(modifier = Modifier.size(10.dp))

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
                    contentDescription = "Select song image"
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
                    if (bitmap.value != null && audioUri != null) {
                        uploadDataToFirebase(bitmap.value!!, audioUri!!, context, artist, podcastName)
                    } else {
                        Toast.makeText(context, "Please select photo and audio file\n", Toast.LENGTH_SHORT).show()
                    }
                },
            ) {
                Text(text = "Save information")
            }
        }
    }
}

fun uploadDataToFirebase(bitmap: Bitmap, audioUri: Uri, context: Context, artist: String, podcastName: String) {
    val storageRef = FirebaseStorage.getInstance().reference
    val databaseRef = FirebaseDatabase.getInstance().reference

    val imageFileName = "${System.currentTimeMillis()}.jpg"
    val imageRef = storageRef.child("images/$imageFileName")
    val baos = ByteArrayOutputStream()
    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
    val imageData = baos.toByteArray()
    val uploadImageTask = imageRef.putBytes(imageData)

    val audioFileName = "${System.currentTimeMillis()}.mp3"
    val audioRef = storageRef.child("audios/$audioFileName")
    val contentResolver: ContentResolver = context.contentResolver
    val inputStream = contentResolver.openInputStream(audioUri)
    val buffer = ByteArray(1024)
    val baosAudio = ByteArrayOutputStream()
    var len: Int
    while (inputStream!!.read(buffer).also { len = it } != -1) {
        baosAudio.write(buffer, 0, len)
    }
    val audioData = baosAudio.toByteArray()
    val uploadAudioTask = audioRef.putBytes(audioData)

    uploadImageTask.addOnSuccessListener { taskSnapshotImage ->
        imageRef.downloadUrl.addOnSuccessListener { uri ->
            val imageUrl = uri.toString()

            uploadAudioTask.addOnSuccessListener { taskSnapshotAudio ->
                audioRef.downloadUrl.addOnSuccessListener { uri ->
                    val audioUrl = uri.toString()

                    val songData = hashMapOf(
                        "artist" to artist,
                        "podcastName" to podcastName,
                        "imageUrl" to imageUrl,
                        "audioUrl" to audioUrl
                    )

                    databaseRef.child("podcasts").push()
                        .setValue(songData)
                        .addOnSuccessListener {
                            Toast.makeText(context, "Upload Success!", Toast.LENGTH_SHORT).show()
                        }
                        .addOnFailureListener {
                            Toast.makeText(context, "Upload Failed!", Toast.LENGTH_SHORT).show()
                        }
                }.addOnFailureListener {
                    Toast.makeText(context, "Audio URL retrieval failed!", Toast.LENGTH_SHORT).show()
                }
            }.addOnFailureListener {
                Toast.makeText(context, "Audio upload failed!", Toast.LENGTH_SHORT).show()
            }
        }.addOnFailureListener {
            Toast.makeText(context, "Image URL retrieval failed!", Toast.LENGTH_SHORT).show()
        }
    }.addOnFailureListener {
        Toast.makeText(context, "Image upload failed!", Toast.LENGTH_SHORT).show()
    }
}