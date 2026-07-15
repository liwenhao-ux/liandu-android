package com.example.qingxue

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.qingxue.ui.QingXueAppScreen
import com.example.qingxue.ui.QingXueViewModel
import com.example.qingxue.ui.QingXueViewModelFactory
import com.example.qingxue.ui.theme.AppAccent
import com.example.qingxue.ui.theme.QingXueTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    private val viewModel: QingXueViewModel by viewModels {
        QingXueViewModelFactory(
            repository = (application as QingXueApp).repository,
            focusTimerStore = (application as QingXueApp).focusTimerStore,
            application = application
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val app = application as QingXueApp
            val accent by app.themePreferenceStore.accent.collectAsStateWithLifecycle(
                initialValue = AppAccent.GrayPurple
            )
            val scope = rememberCoroutineScope()

            QingXueTheme(accent = accent) {
                QingXueAppScreen(
                    viewModel = viewModel,
                    selectedAccent = accent,
                    onAccentSelected = { selected ->
                        scope.launch { app.themePreferenceStore.setAccent(selected) }
                    }
                )
            }
        }
    }
}