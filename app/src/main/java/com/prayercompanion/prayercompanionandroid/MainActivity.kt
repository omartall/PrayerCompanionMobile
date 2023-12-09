package com.prayercompanion.prayercompanionandroid

import android.app.Activity
import android.os.Build
import android.os.Bundle
import android.view.WindowManager
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.ScaffoldState
import androidx.compose.material.Snackbar
import androidx.compose.material.SnackbarHost
import androidx.compose.material.Text
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.prayercompanion.prayercompanionandroid.presentation.features.qibla.QiblaScreen
import com.prayercompanion.prayercompanionandroid.presentation.features.qibla.QiblaViewModel
import com.prayercompanion.prayercompanionandroid.presentation.utils.FeedbackUtils
import com.prayercompanion.shared.domain.utils.Task
import com.prayercompanion.shared.presentation.App
import com.prayercompanion.shared.presentation.features.onboarding.sign_in.GoogleSignInSetup
import com.prayercompanion.shared.presentation.navigation.Route
import com.prayercompanion.shared.presentation.theme.PrayerCompanionAndroidTheme
import org.koin.androidx.viewmodel.ext.android.getViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : AppCompatActivity() {

    private val viewModel: MainActivityViewModel by viewModel()

    private val googleSignInClient: GoogleSignInClient by lazy {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()


        GoogleSignIn.getClient(this, gso)
    }

    private val signInWithGoogleLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        val result = it.resultCode == Activity.RESULT_OK
        val task = GoogleSignIn.getSignedInAccountFromIntent(it.data)
            .let { task ->
                Task<Pair<String?,String?>>(
                    isSuccessful = result,
                    result = task.result.idToken to null,
                    exception = task.exception
                )
            }
        GoogleSignInSetup.onResult(
            result,
            task
        )
    }

    private val feedbackUtils = FeedbackUtils(this)

    @Suppress("DEPRECATION")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.setDecorFitsSystemWindows(true)
        } else {
            window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
        }

        GoogleSignInSetup.setup(::signInWithGoogle)

        setContent {
            App()
            return@setContent
            PrayerCompanionAndroidTheme {
                val navController = rememberNavController()
                val scaffoldState = rememberScaffoldState()
                var shouldShowBottomNavigationBar by rememberSaveable {
                    mutableStateOf(false)
                }
                navController.addOnDestinationChangedListener { _, destination, _ ->
                    val route = Route.fromStringRoute(destination.route)
                    shouldShowBottomNavigationBar = route.bottomNavBar
                    viewModel.onScreenChanged(route)
                }

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    scaffoldState = scaffoldState,
                    snackbarHost = {
                        SnackbarHost(it) { data ->
                            Snackbar(
                                snackbarData = data,
                                backgroundColor = Color.LightGray,
                            )
                        }
                    },
                    bottomBar = {
                        if (shouldShowBottomNavigationBar) {
                            BottomNavigationBar(navController)
                        }
                    }
                ) { scaffold ->
                    NavHost(
                        modifier = Modifier.padding(scaffold),
                        navController = navController,
                        startDestination = Route.SplashScreen.routeName,
                    ) {
                        composable(Route.Qibla.routeName) {
                            val viewModel: QiblaViewModel = getViewModel()
                            QiblaScreen(
                                onEvent = viewModel::onEvent,
                                sensorAccuracy = viewModel.sensorAccuracy,
                                qiblaDirection = viewModel.qiblaDirection
                            )
                        }
                    }
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.onResume()
    }

    @Composable
    private fun BottomNavigationBar(navController: NavController) {
        BottomNavigation(
            modifier = Modifier.height(62.dp),
            backgroundColor = MaterialTheme.colors.primary,

            ) {
            val navBackStackEntry by navController.currentBackStackEntryAsState()
            val currentDestination = navBackStackEntry?.destination

            BottomNavItem.getOrdered().forEach { screen ->
                BottomNavigationItem(
                    icon = {
                        Icon(
                            painterResource(id = screen.icon),
                            contentDescription = null
                        )
                    },
                    label = { Text(stringResource(screen.nameId)) },
                    selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
                    onClick = {
                        navController.navigate(screen.route) {
                            // Pop up to the start destination of the graph to
                            // avoid building up a large stack of destinations
                            // on the back stack as users select items
                            popUpTo(Route.Home.routeName) {
                                saveState = true
                            }
                            // Avoid multiple copies of the same destination when
                            // re-selecting the same item
                            launchSingleTop = true
                            // Restore state when re-selecting a previously selected item
                            restoreState = true
                        }
                    },
                    selectedContentColor = MaterialTheme.colors.onPrimary,
                    unselectedContentColor = MaterialTheme.colors.secondary
                )
            }
        }
    }

    private fun signInWithGoogle() {
        signInWithGoogleLauncher.launch(
            googleSignInClient.signInIntent
        )
    }

    private suspend fun showSnackBar(scaffoldState: ScaffoldState, message: String) {
        scaffoldState
            .snackbarHostState
            .showSnackbar(message)
    }
}