package com.sspirit.nadiiaspaceassistant.services

import android.util.Log
import com.sspirit.nadiiaspaceassistant.models.missions.building.BuildingDoor
import com.sspirit.nadiiaspaceassistant.models.missions.building.BuildingDoorTurn

object PropertyEvacuationTimeManager {

    fun handleDoorOpeningTry(door: BuildingDoor) {
        if (door.turn == BuildingDoorTurn.AUTOMATIC)
            Log.d("TimeManager", "Automatic door opens immediately")
        else
            Log.d("TimeManager", "Try to open manual door: -20 sec")
    }

    fun handleDoorHackingTry() {
        Log.d("TimeManager", "Try to hacking door: -30 sec")
    }

    fun handleDoorDestruction() {
        Log.d("TimeManager", "Destruct door immediately")
    }

    fun handleVentGrilleRemoving() {
        Log.d("TimeManager", "Remove ventilation grille immediately")
    }

    fun handleVentCrawlingTry() {
        Log.d("TimeManager", "Try to crawl through vent: -40 sec")
    }
}