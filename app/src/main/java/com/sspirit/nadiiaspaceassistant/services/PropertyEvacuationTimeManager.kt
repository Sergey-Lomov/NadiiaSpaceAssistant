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

    fun handleDoorClosing() {
        Log.d("TimeManager", "Door was closed immediately")
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

    fun handleHoleMaking() {
        Log.d("TimeManager", "Make a hole immediately")
    }

    fun handleJumpingIntoHole() {
        Log.d("TimeManager", "Jump into a hole immediately")
    }

    fun handleCarefullyDownIntoHole() {
        Log.d("TimeManager", "Carefully down into a hole: -40 sec")
    }

    fun handleDownByHeap() {
        Log.d("TimeManager", "Down into hole uses heap: -20 sec")
    }

    fun handleUpByHeap() {
        Log.d("TimeManager", "Up into hole uses heap: -20 sec")
    }

    fun handleBigObjectMoving() {
        Log.d("TimeManager", "Big object moved immediately")
    }
}