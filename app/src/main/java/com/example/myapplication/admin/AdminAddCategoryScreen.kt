package com.example.myapplication.admin

import android.content.ContentResolver
import android.content.Context
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
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
fun AdminAddCategoryScreen(
    loginViewModel: LoginViewModel? = null,
    onNavToAdminHomePage: () -> Unit,
    onNavToAdminCategoryPage: () -> Unit,
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
                IconButton(onClick = { onNavToAdminCategoryPage.invoke() }) {
                    Icon(
                        Icons.Default.ArrowBack, contentDescription = "Home",
                        tint = Color.White
                    )
                }

                Text(
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center,
                    text = "Add music genre",
                    fontSize = 18.sp,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            }

            val context = LocalContext.current
            var categoryMusic by remember { mutableStateOf("") }

            Spacer(modifier = Modifier.size(20.dp))

            OutlinedTextField(
                value = categoryMusic,
                onValueChange = { categoryMusic = it },
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
                        "Enter a category name",
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
                    contentDescription = "Select image"
                )

                Spacer(modifier = Modifier.size(10.dp))

                Text(text = "Select photo category")
            }

            Spacer(modifier = Modifier.size(10.dp))

            Button(
                onClick = {
                    categoryMusic?.let { musicCategory ->
                        bitmap.value?.let { categoryImage ->
                            uploadCategoryToFirebase(categoryImage, context as ComponentActivity, musicCategory)
                        } ?: run {
                            Toast.makeText(context, "Please choose a photo of the category!", Toast.LENGTH_SHORT).show()
                        }
                    } ?: run {
                        Toast.makeText(context, "Please enter a category name!", Toast.LENGTH_SHORT).show()
                    }
                },
            ) {
                Text(text = "Save information")
            }
        }
    }
}

fun uploadCategoryToFirebase(bitmap: Bitmap, context: Context, categoryMusic: String) {
    val storageRef = FirebaseStorage.getInstance().reference
    val databaseRef = FirebaseDatabase.getInstance().reference

    val imageFileName = "${System.currentTimeMillis()}.jpg"
    val imageRef = storageRef.child("category_images/$imageFileName")
    val baos = ByteArrayOutputStream()
    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
    val imageData = baos.toByteArray()
    val uploadImageTask = imageRef.putBytes(imageData)

    uploadImageTask.addOnSuccessListener { taskSnapshot ->
        imageRef.downloadUrl.addOnSuccessListener { uri ->
            val imageUrl = uri.toString()

            val categoryData = hashMapOf(
                "categoryName" to categoryMusic,
                "categoryImageUrl" to imageUrl
            )

            databaseRef.child("categories").push()
                .setValue(categoryData)
                .addOnSuccessListener {
                    Toast.makeText(context, "Category upload Success!", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener {
                    Toast.makeText(context, "Category upload Failed!", Toast.LENGTH_SHORT).show()
                }
        }.addOnFailureListener {
            Toast.makeText(context, "Image URL retrieval failed!", Toast.LENGTH_SHORT).show()
        }
    }.addOnFailureListener {
        Toast.makeText(context, "Image upload failed!", Toast.LENGTH_SHORT).show()
    }
}
