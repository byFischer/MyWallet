package com.example.mywallet.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.mywallet.ui.screens.AddInstallmentScreen
import com.example.mywallet.ui.screens.AddSubscriptionScreen
import com.example.mywallet.ui.screens.DashboardScreen

object Routes {
    const val DASHBOARD = "dashboard"
    const val ADD_SUBSCRIPTION = "add_subscription"
    const val ADD_INSTALLMENT = "add_installment"
}

@Composable
fun AppNavigation() {
    val navController: NavHostController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Routes.DASHBOARD
    ) {
        composable(Routes.DASHBOARD) {
            DashboardScreen(
                onAddSubscriptionClick = {
                    navController.navigate(Routes.ADD_SUBSCRIPTION)
                },
                onAddInstallmentClick = {
                    navController.navigate(Routes.ADD_INSTALLMENT)
                }
            )
        }

        composable(Routes.ADD_SUBSCRIPTION) {
            AddSubscriptionScreen(
                onSubscriptionAdded = { navController.popBackStack() }
            )
        }

        composable(Routes.ADD_INSTALLMENT) {
            AddInstallmentScreen(
                onDone = { navController.popBackStack() }
            )
        }
    }
}