package com.sspirit.nadiiaspaceassistant.viewmodels

import androidx.compose.runtime.MutableState
import com.sspirit.nadiiaspaceassistant.models.character.CharacterTrait
import com.sspirit.nadiiaspaceassistant.utils.daysToNow

data class InfoDialogViewModel(
    val title: String,
    val info: String,
    var actions: MutableMap<String, (MutableState<Boolean>) -> Unit> = mutableMapOf()
) {
    companion object {
        fun newTrait(trait: CharacterTrait): InfoDialogViewModel {
            var info = trait.type.info + "\n\n\n" + trait.type.effectInfo
            if (trait.expiration != null) {
                val expiration = trait.expiration.daysToNow()
                info += "\n\n\nЗакончится через $expiration дн."
            }

            return InfoDialogViewModel(
                title = trait.type.title,
                info = info
            )
        }
    }
}