package xcom.niteshray.xapps.handwritefy.ui.Screens

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.os.Environment
import android.provider.MediaStore
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.view.drawToBitmap
import androidx.navigation.NavController
import xcom.niteshray.xapps.handwritefy.R
import xcom.niteshray.xapps.handwritefy.ui.theme.gray
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PageScreen(navController: NavController){
    var context = LocalContext.current
    val composeViewRef = remember { mutableStateOf<ComposeView?>(null) }
    var fontSize by remember { mutableStateOf(12f) }
    var lineHeight by remember { mutableStateOf(20f) }

    var handtext = remember { mutableStateOf("") }
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "Handwritefy", fontSize = 26.sp , color = Color.White , fontFamily = FontFamily(
                    Font(R.font.humanhand)
                )) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = gray
                )
            )
        },
        containerColor = Color.Black
    ){ innerpadding->
        Column(
            modifier = Modifier.padding(innerpadding).verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,

        ){
            Spacer(modifier = Modifier.height(5.dp))
            Text(
                text = "Type, Print, Submit",
                fontSize = 22.sp,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily(Font(R.font.humanhand2))
            )
            Spacer(modifier = Modifier.height(5.dp))
            AndroidView(
                factory= { context ->
                    ComposeView(context).apply {
                        setContent {

                            val paperPainter: Painter = painterResource(R.drawable.samplepaper)

                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .aspectRatio(paperPainter.intrinsicSize.width / paperPainter.intrinsicSize.height)
                            ) {
                                Image(
                                    painter = paperPainter,
                                    contentDescription = "",
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Fit
                                )

                                BasicTextField(
                                    value = handtext.value,
                                    onValueChange = { handtext.value = it },
                                    textStyle = TextStyle(
                                        color = Color.Blue,
                                        fontSize = fontSize.sp,
                                        lineHeight = lineHeight.sp,
                                        fontFamily = FontFamily(Font(R.font.humanhand2))
                                    ),
                                    modifier = Modifier
                                        .padding(start = 30.dp, top = 16.dp, end = 5.dp, bottom = 10.dp)
                                        .fillMaxSize()
                                        .verticalScroll(rememberScrollState())
                                )
                            }
                        }
                        composeViewRef.value = this
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(10.dp))
            Text(text = "Font Size: ${fontSize.toInt()}", color = Color.White)
            Slider(
                value = fontSize,
                onValueChange = { fontSize = it },
                valueRange = 4f..30f,
                colors = SliderDefaults.colors(thumbColor = Color.White, activeTrackColor = Color.White)
            )

            Spacer(modifier = Modifier.height(10.dp))
            Text(text = "Line Spacing: ${lineHeight.toInt()}", color = Color.White)
            Slider(
                value = lineHeight,
                onValueChange = { lineHeight = it },
                valueRange = 8f..40f,
                colors = SliderDefaults.colors(thumbColor = Color.White, activeTrackColor = Color.White)
            )
            Spacer(modifier = Modifier.height(10.dp))
            GradientButton(text = "Save Assignment", onClick = {
                if (handtext.value.isNotEmpty()){
                    composeViewRef.value?.let {
                        var bitmap = it.drawToBitmap()
                        saveBitmapToGallery(bitmap,context)
                        navController.popBackStack()
                    }
                }else{
                    Toast.makeText(context , "Write Something First",Toast.LENGTH_LONG).show()
                }
            })

            Spacer(modifier = Modifier.height(24.dp))
        }
    }

}

fun saveBitmapToGallery(bitmap: Bitmap, context: Context) {
    val filename = "handWritten_${System.currentTimeMillis()}.png"
    val contentValues = ContentValues().apply {
        put(MediaStore.Images.Media.DISPLAY_NAME, filename)
        put(MediaStore.Images.Media.MIME_TYPE, "image/png")
        put(MediaStore.Images.Media.RELATIVE_PATH, "${Environment.DIRECTORY_PICTURES}/Handwritefy")
    }
    val uri = context.contentResolver.insert(
        MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
        contentValues
    )
    uri?.let {
        context.contentResolver.openOutputStream(it)?.use { outputStream ->
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
        }
        Toast.makeText(context, "Saved to Pictures/HandWriter", Toast.LENGTH_SHORT).show()
    }
}