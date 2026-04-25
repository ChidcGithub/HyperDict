package com.hyperdict.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.hyperdict.app.di.ServiceLocator
import com.hyperdict.app.ui.screens.HomeScreen
import com.hyperdict.app.ui.theme.HyperDictTheme
import com.hyperdict.app.ui.viewmodel.DictionaryViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val repository = ServiceLocator.getRepository(this)

        setContent {
            HyperDictTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val viewModel: DictionaryViewModel = viewModel(
                        factory = DictionaryViewModelFactory(repository, this)
                    )
                    HomeScreen(viewModel = viewModel)
                }
            }
        }
    }
}
