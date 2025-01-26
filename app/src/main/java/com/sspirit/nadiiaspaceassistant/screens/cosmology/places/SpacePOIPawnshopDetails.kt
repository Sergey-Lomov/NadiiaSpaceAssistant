package com.sspirit.nadiiaspaceassistant.screens.cosmology.places

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import com.sspirit.nadiiaspaceassistant.services.dataproviders.ItemDataProvider
import com.sspirit.nadiiaspaceassistant.ui.CenteredRegularText
import com.sspirit.nadiiaspaceassistant.ui.ElementsList
import com.sspirit.nadiiaspaceassistant.ui.ScreenWrapper
import com.sspirit.nadiiaspaceassistant.ui.ScrollableColumn
import com.sspirit.nadiiaspaceassistant.ui.SpacedHorizontalDivider
import com.sspirit.nadiiaspaceassistant.ui.TitleValueRow

@Composable
fun SpacePOIPawnshopDetailsView() {
    ScrollableColumn {
        CenteredRegularText("Тут скупают все на свете (почти). Естественно дешевле, чем потом можно приобрести это в магазинах.")
        SpacedHorizontalDivider()
        val items = ItemDataProvider.descriptors.sortedBy { it.title }
        ElementsList(items) {
            val price = if (it.sellPrice != null) "${it.sellPrice}" else "-"
            TitleValueRow(it.title, price, rightPriority = true)
        }
    }
}