package com.sspirit.nadiiaspaceassistant.ui.utils

import androidx.navigation.NavHostController
import com.google.gson.Gson
import com.sspirit.nadiiaspaceassistant.extensions.navigateTo

fun routesFlowStep(
    payload: String,
    nextRoutes: Array<String>,
    navController: NavHostController
) {
    if (nextRoutes.firstOrNull() != null) {
        val route = nextRoutes.first()
        val leftRouters = nextRoutes.sliceArray(1 until nextRoutes.size)

        var fullRoute = route + "/${payload}"
        if (leftRouters.isNotEmpty()) {
            val json = Gson().toJson(leftRouters)
            fullRoute += "/$json"
        }
        navController.navigate(fullRoute)
    }
}