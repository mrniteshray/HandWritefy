package xcom.niteshray.xapps.handwritefy.ui.Screens

import android.content.Context
import android.content.Intent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import xcom.niteshray.xapps.handwritefy.R
import xcom.niteshray.xapps.handwritefy.ui.theme.gray
import android.net.Uri
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import coil.compose.rememberAsyncImagePainter
import xcom.niteshray.xapps.handwritefy.utils.GetImages

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Home(navigate: () -> Unit){
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
        floatingActionButton = {
            Icon(Icons.Default.Add , contentDescription = "Add",
                modifier = Modifier.background(Color.Red, RoundedCornerShape(120.dp)).padding(10.dp)
                    .size(40.dp)
                    .clickable{
                        navigate()
                    } ,
                tint = Color.White
            )
        } ,
        containerColor = Color.Black
    ){ innerpadding->
        var arrangement = Arrangement.Center
        val context = LocalContext.current
        val images = remember { mutableStateOf(emptyList<Uri>()) }

        val lifecycleOwner = LocalLifecycleOwner.current

        LaunchedEffect(lifecycleOwner) {
            images.value = GetImages.loadSavedImages(context)
            val observer = LifecycleEventObserver { _, event ->
                if (event == Lifecycle.Event.ON_RESUME) {
                    images.value =  GetImages.loadSavedImages(context)
                }
            }

            lifecycleOwner.lifecycle.addObserver(observer)
        }

        DisposableEffect(lifecycleOwner) {
            val observer = LifecycleEventObserver { _, event ->
                if (event == Lifecycle.Event.ON_RESUME) {
                    images.value =  GetImages.loadSavedImages(context)
                }
            }

            lifecycleOwner.lifecycle.addObserver(observer)

            onDispose {
                lifecycleOwner.lifecycle.removeObserver(observer)
            }
        }


        Column(
            modifier = Modifier.padding(innerpadding).fillMaxSize(),
            verticalArrangement = arrangement
        ){
            if (images.value.isEmpty()){
                Image(
                    painter = painterResource(R.drawable.empty)
                    ,contentDescription = "empty file",
                    modifier = Modifier.size(120.dp).align(Alignment.CenterHorizontally)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Haven't Created Assignments yet",
                    fontSize = 16.sp,
                    style = MaterialTheme.typography.titleSmall,
                    color = Color.White,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
                Spacer(modifier = Modifier.height(90.dp))
            }else{
                SavedImagesScreen(images ,context)
            }
        }
    }
}

@Composable
fun SavedImagesScreen(images: MutableState<List<Uri>> , context : Context) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(3),
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black),
        contentPadding = PaddingValues(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(images.value){
            imageUri ->
                Image(
                    painter = rememberAsyncImagePainter(imageUri),
                    contentDescription = null,
                    modifier = Modifier
                        .aspectRatio(1f)
                        .clip(RoundedCornerShape(8.dp))
                        .clickable {
                            val intent = Intent(Intent.ACTION_VIEW).apply {
                                setDataAndType(imageUri, "image/*")
                                flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
                            }
                            context.startActivity(intent)
                        },
                    contentScale = ContentScale.Crop
                )
        }
    }
}
