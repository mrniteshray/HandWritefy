package xcom.niteshray.xapps.handwritefy

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import xcom.niteshray.xapps.handwritefy.ui.Screens.Home
import xcom.niteshray.xapps.handwritefy.ui.Screens.PageScreen
import xcom.niteshray.xapps.handwritefy.ui.theme.HandWritefyTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            HandWritefyTheme {
                App()
            }
        }
    }
}
@Composable
fun App() {
    var navController = rememberNavController()
    NavHost(navController = navController , startDestination = "home"){

        composable("home"){
            Home(){
               navController.navigate("page")
            }
        }
        composable("page") {
            PageScreen(navController)
        }
    }
}
