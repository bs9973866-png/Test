package app.novaura.launcher

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.viewmodel.compose.viewModel
import app.novaura.launcher.ui.home.HomeScreen
import app.novaura.launcher.ui.home.HomeViewModel
import app.novaura.launcher.ui.theme.NovauraTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent { NovauraTheme { HomeScreen(viewModel<HomeViewModel>()) } }
    }
}
