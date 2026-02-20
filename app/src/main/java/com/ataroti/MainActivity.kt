package com.ataroti

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.Surface
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.ataroti.data.db.AppDatabase
import com.ataroti.data.model.Mode
import com.ataroti.data.repo.AtarotiRepository
import com.ataroti.ui.screens.LiveCameraScreen
import com.ataroti.ui.screens.SandboxScreen
import com.ataroti.ui.screens.SessionListScreen
import com.ataroti.ui.theme.AtarotiTheme
import com.ataroti.ui.viewmodel.LiveOracleViewModel
import com.ataroti.ui.viewmodel.LiveOracleViewModelFactory
import com.ataroti.ui.viewmodel.SandboxViewModel
import com.ataroti.ui.viewmodel.SandboxViewModelFactory
import com.ataroti.ui.viewmodel.SessionListViewModel
import com.ataroti.ui.viewmodel.SessionListViewModelFactory

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val repo = remember { AtarotiRepository(AppDatabase.get(applicationContext)) }
            LaunchedEffect(Unit) { repo.ensureSeeded() }

            AtarotiTheme {
                Surface(color = Color(0xFF141414)) {
                    val navController = rememberNavController()
                    NavHost(navController = navController, startDestination = "live") {
                        composable("live") {
                            val vm: LiveOracleViewModel = viewModel(factory = LiveOracleViewModelFactory(repo))
                            LiveCameraScreen(
                                viewModel = vm,
                                onOpenSessions = { navController.navigate("sessions") },
                                onOpenSandbox = { navController.navigate("sandbox") }
                            )
                        }
                        composable("sandbox") {
                            val vm: SandboxViewModel = viewModel(factory = SandboxViewModelFactory(repo))
                            SandboxScreen(vm, onBackLive = { navController.popBackStack() })
                        }
                        composable("sessions") {
                            val vm: SessionListViewModel = viewModel(factory = SessionListViewModelFactory(repo))
                            SessionListScreen(vm, onOpenMode = {
                                when (it) {
                                    Mode.LIVE_ORACLE -> navController.navigate("live")
                                    Mode.SANDBOX -> navController.navigate("sandbox")
                                }
                            }, onBack = { navController.popBackStack() })
                        }
                    }
                }
            }
        }
    }
}
