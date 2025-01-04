package com.sspirit.nadiiaspaceassistant.screens.building.events

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import com.sspirit.nadiiaspaceassistant.services.SkillChecksManager
import com.sspirit.nadiiaspaceassistant.services.dataproviders.CharacterDataProvider
import com.sspirit.nadiiaspaceassistant.services.dataproviders.missions.propertyevacuation.Completion
import com.sspirit.nadiiaspaceassistant.services.fabrics.CharacterTraitsGenerator
import com.sspirit.nadiiaspaceassistant.utils.simpleCoroutineLaunch

@Composable
fun AcidContainerEventView(navigator: NavHostController) {
    BuildingEventWithCheckView(
        navigator = navigator,
        description = "Открывая контейнер вы почувствовали странный запах. Узнаете ли вы его? Зависит от уровня вашего разума.",
        check = SkillChecksManager.acidContainerEvent(),
        failDescription = "К сожалению вы не смогли сходу узнать этот запах, хотя это был запах одной из самых распространенных кислот. Внутри контейнера что-то протекло, и часть кислоты из него попала вам на руку. Теперь у вас кислотный ожог: прийдется поберечь раненую конечность. Ваша сила снижена на 5. Так же одной рукой вы не сможете открывать очень сильно заедающие двери и перемещать большие предметы размером больше 1. А еще тут куча дополнительного текста чтобы провертиь что все корректно отобразиться даже если текста будет так много что он прям аж не влезет в один экран. И в два не влезет и вобще куа аж никуда не влезет и должно все-равно все быть видно с помощью прокрутки.",
        successDescription = "В последний момент вы узнаете этот запах - запах одной из распространенных кислот! Вы успеваете отбросить контейнер преждем. чем кислота попадает вам на руки. Видимо внутри что-то протекло, опознать что это было уже невозможно.",
        onFail = { state ->
            simpleCoroutineLaunch(state) {
                val trait = CharacterTraitsGenerator.oneDayArmAcidBurn()
                CharacterDataProvider.addTrait(trait)
            }
        },
    )
}