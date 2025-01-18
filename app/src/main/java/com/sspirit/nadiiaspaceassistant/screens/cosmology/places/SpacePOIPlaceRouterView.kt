package com.sspirit.nadiiaspaceassistant.screens.cosmology.places

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.navigation.NavHostController
import com.sspirit.nadiiaspaceassistant.models.cosmology.SpacePOIPlace
import com.sspirit.nadiiaspaceassistant.models.cosmology.SpacePOIPlaceType
import com.sspirit.nadiiaspaceassistant.models.cosmology.SpacePOIPlaceType.*
import com.sspirit.nadiiaspaceassistant.ui.CenteredRegularText
import com.sspirit.nadiiaspaceassistant.ui.ScreenWrapper
import com.sspirit.nadiiaspaceassistant.utils.updaterState
import com.sspirit.nadiiaspaceassistant.viewmodels.InfoDialogViewModel

@Composable
fun SpacePOIPlaceRouterView(place: SpacePOIPlace, navigator: NavHostController) {
    val isLoading = rememberSaveable { mutableStateOf(false) }
    val updater = updaterState()

    ScreenWrapper(navigator, place.type.title, isLoading, updater) {
        when {
            place.type.isStore -> SpacePOIStoreDetailsView(place, isLoading, navigator)
            place.type == QUANTUM_ARCHIVER ->
                CenteredRegularText("Здесь можно работать с квантовыми хранилищами и складами. На сейчас любые операции проводятся бесплатно.")
            place.type == WORKSHOP ->
                CenteredRegularText("Здесь можно чинить оборудование.\n\n•Починка гимпердвигателя стоит 2.")
            place.type == HOSPITAL ->
                CenteredRegularText("Здесь можно получить медицинское обслуживание.\n\n•Излечить травму 3\n•Излечить недуг 5")
            place.type == BUYUP ->
                CenteredRegularText("Тут скупают все на свете (почти). Естествнно дешевле, чем потом можно приобрести это в магазинах.")
            place.type == SHOWROOM ->
                CenteredRegularText("Если у вас есть диковинки, здесь можно выставить их на всеобщее обозрение и получать пассивный доход каждый день.")
        }
    }
}