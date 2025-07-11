package xcom.niteshray.xapps.handwritefy.ui.Screens.PageScreen

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberBottomSheetScaffoldState
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
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.platform.LocalDensity
import androidx.core.content.res.ResourcesCompat
import coil.compose.rememberAsyncImagePainter
import xcom.niteshray.xapps.handwritefy.ui.Screens.GradientButton


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PageScreen(navController: NavController) {
    val context = LocalContext.current
    val composeViewRef = remember { mutableStateOf<ComposeView?>(null) }
    var fontSize by remember { mutableStateOf(12f) }
    var lineSpacing by remember { mutableStateOf(12f) }
    val handtext = remember { mutableStateOf("  \n  Q.  Write your assignments here") }
    var selectedFontId by remember { mutableStateOf(R.font.humanhand2) }
    var expanded by remember { mutableStateOf(false) }


    val fontOptions = listOf(
        Pair("Handwritting 1", R.font.humanhand),
        Pair("Handwritting 2", R.font.humanhand2),
        Pair("Handwritting 3", R.font.handwritting3),
        Pair("Handwritting 4", R.font.handwritting4),
        Pair("Handwritting 5", R.font.handwritting5),
        Pair("Handwritting 6", R.font.handwritting6),
    )

    val uploadedPageUri = remember { mutableStateOf<Uri?>(null) }


    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let {
            uploadedPageUri.value = it
        }
    }

    val bottomSheetState = rememberBottomSheetScaffoldState()


    BottomSheetScaffold(
        scaffoldState = bottomSheetState,
        sheetPeekHeight = 100.dp,
        sheetContainerColor = Color(0xFF1C1C1C),
        sheetContent = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Customize Text",
                    fontSize = 18.sp,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
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
                Text(text = "Line Spacing: ${lineSpacing.toInt()}", color = Color.White)
                Slider(
                    value = lineSpacing,
                    onValueChange = { lineSpacing = it },
                    valueRange = 8f..40f,
                    colors = SliderDefaults.colors(thumbColor = Color.White, activeTrackColor = Color.White)
                )
                //Upload your image
                GradientButton(text = "Upload Your Page") {
                    imagePickerLauncher.launch("image/*")
                }

                Spacer(modifier = Modifier.height(4.dp))
                
                Box(modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
                    .clickable{ expanded = true }
                    .background(Color.Transparent)
                )
                {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color.Transparent)
                            .padding(12.dp)
                    ) {
                        Text(
                            text = "Selected Font: ${fontOptions.firstOrNull { it.second == selectedFontId }?.first}",
                            color = Color.White
                        )

                        Icon(Icons.Default.ArrowDropDown, contentDescription = "",
                            tint = Color.White
                            )
                    }

                    Column(modifier = Modifier.fillMaxWidth()) {
                        ExposedDropdownMenuBox(
                            expanded = expanded,
                            onExpandedChange = { expanded = !expanded }
                        ) {
                            TextField(
                                value = "${fontOptions.firstOrNull { it.second == selectedFontId }?.first}",
                                onValueChange = {},
                                readOnly = true,
                                label = { Text("Selected Font") },
                                trailingIcon = {
                                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                                },
                                colors = ExposedDropdownMenuDefaults.textFieldColors(),
                                modifier = Modifier
                                    .menuAnchor()
                                    .fillMaxWidth()
                            )
                            ExposedDropdownMenu(
                                expanded = expanded,
                                onDismissRequest = { expanded = false }
                            ) {
                                fontOptions.forEach { fontPair ->
                                    DropdownMenuItem(
                                        onClick = {
                                            selectedFontId = fontPair.second
                                            expanded = false
                                        },
                                        text = { Text(fontPair.first) }
                                    )
                                }
                            }
                        }}

                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false },
                    ) {
                        fontOptions.forEach { fontPair ->
                            DropdownMenuItem(
                                onClick = {
                                    selectedFontId = fontPair.second
                                    expanded = false
                                },
                                text = { Text(fontPair.first) }
                            )
                        }
                    }
                }
                Divider(
                    color = Color.White,
                    thickness = 1.dp,
                    modifier = Modifier.padding(10.dp,0.dp)
                )

            }
        },
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Handwritefy",
                        fontSize = 26.sp,
                        color = Color.White,
                        fontFamily = FontFamily(Font(R.font.humanhand))
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = gray)
            )
        },
        containerColor = Color.Black
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(bottom = 120.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
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
                factory = { ctx ->
                    ComposeView(ctx).apply {
                        setContent {
                            val font = FontFamily(Font(selectedFontId))
                            val painter: Painter = if (uploadedPageUri.value != null) {
                                rememberAsyncImagePainter(model = uploadedPageUri.value)
                            } else {
                                painterResource(R.drawable.samplepaper)
                            }
                            Box(
                                modifier = Modifier
                                    .graphicsLayer()
                                    .background(Color.White)
                                    .then(Modifier.fillMaxWidth())
                                    .aspectRatio(1f / 1.4142f)
                            ) {
                                Image(
                                    painter = painter,
                                    contentDescription = null,
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Fit
                                )

                                BasicTextField(
                                    value = handtext.value,
                                    onValueChange = { handtext.value = it },
                                    textStyle = TextStyle(
                                        color = Color.Blue,
                                        fontSize = fontSize.sp,
                                        lineHeight = lineSpacing.sp,
                                        fontFamily = font
                                    ),
                                    modifier = Modifier
                                        .padding(start = 30.dp,46.dp , 5.dp,10.dp)
                                        .fillMaxSize()
                                        .verticalScroll(rememberScrollState()),
                                )
                            }
                        }
                        composeViewRef.value = this
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(10.dp))
            GradientButton(text = "Save Assignment", onClick = {
                if (handtext.value.isNotEmpty()) {
                    composeViewRef.value?.let {
                        val bitmap = it.drawToBitmap()
                        saveBitmapToGallery(bitmap, context)
                        navController.popBackStack()
                    }
                } else {
                    Toast.makeText(context, "Write Something First", Toast.LENGTH_LONG).show()
                }
            })
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