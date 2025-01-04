package com.sspirit.nadiiaspaceassistant.services

import android.util.Log
import com.sspirit.nadiiaspaceassistant.models.missions.building.BuildingDoor
import com.sspirit.nadiiaspaceassistant.models.missions.building.BuildingDoorTurn
import com.sspirit.nadiiaspaceassistant.models.missions.building.BuildingRoom
import com.sspirit.nadiiaspaceassistant.models.missions.building.transport.BuildingTransport

object PropertyEvacuationTimeManager {

    val nodeOptimizationBonus = 120
    val cablesFallFail = 45
    val ceilingFallFail = 45
    val defenseTurretsFail = 45
    val panicAttackFail = 30

    var timeLeft: Int = 720

    fun doorOpeningTry(door: BuildingDoor) {
        if (door.turn == BuildingDoorTurn.AUTOMATIC)
            Log.d("TimeManager", "Automatic door opens immediately")
        else
            Log.d("TimeManager", "Try to open manual door: -20 sec")
    }

    fun doorClosing() {
        Log.d("TimeManager", "Door was closed immediately")
    }

    fun doorHackingTry() {
        Log.d("TimeManager", "Try to hacking door: -30 sec")
    }

    fun doorDestruction() {
        Log.d("TimeManager", "Destruct door immediately")
    }

    fun ventGrilleRemoving() {
        Log.d("TimeManager", "Remove ventilation grille immediately")
    }

    fun ventCrawlingTry() {
        Log.d("TimeManager", "Try to crawl through vent: -40 sec")
    }

    fun holeMaking() {
        Log.d("TimeManager", "Make a hole immediately")
    }

    fun jumpingIntoHole() {
        Log.d("TimeManager", "Jump into a hole immediately")
    }

    fun carefullyDownIntoHole() {
        Log.d("TimeManager", "Carefully down into a hole: -40 sec")
    }

    fun downByHeap() {
        Log.d("TimeManager", "Down into hole uses heap: -10 sec")
    }

    fun upByHeap() {
        Log.d("TimeManager", "Up into hole uses heap: -10 sec")
    }

    fun bigObjectMoving() {
        Log.d("TimeManager", "Big object moved immediately")
    }

    fun bigObjectTransportation(transport: BuildingTransport) {
        Log.d("TimeManager", "Big object transported by ${transport.title}")
    }

    fun playerTransportation(transport: BuildingTransport, from: BuildingRoom, to: BuildingRoom) {
        val duration = transport.timeCost(from, to)
        Log.d("TimeManager", "Player use ${transport.title}: -$duration sec")
    }

    fun safetyConsoleHackingTry() {
        Log.d("TimeManager", "Try to hacking safety console: -20 sec")
    }

    fun acidChargeRecharge() {
        Log.d("TimeManager", "Acid charge recharged: -20 sec")
    }

    fun holoPlanInvestigation() {
        Log.d("TimeManager", "Holo-plan investigated immediately")
    }

    fun ventUnlockedByConsole() {
        Log.d("TimeManager", "All vents unlocked by console immediately")
    }

    fun ventLockedByConsole() {
        Log.d("TimeManager", "All vents locked by console immediately")
    }

    fun energyNodeOptimization(success: Boolean) {
        if (success)
            Log.d("TimeManager", "Energy node optimization success: +$nodeOptimizationBonus sec")
        else
            Log.d("TimeManager", "Energy node optimization failed immediately")
    }

    fun energyCoreRodUsage(isBig: Boolean) {
        if (isBig)
            Log.d("TimeManager", "Big reactor rod used: +240 sec")
        else
            Log.d("TimeManager", "Small reactor rod used: +180 sec")
    }

    fun mainframeGoalDataSearch() {
        Log.d("TimeManager", "Searched goal data in mainframe: -30 sec")
    }

    fun autoDoctorHealing() {
        Log.d("TimeManager", "Healed uses auto-doctor: -30 sec")
    }

    fun cablesFallFail() {
        Log.d("TimeManager", "Time lost to free from cables: -$cablesFallFail sec")
    }

    fun ceilingFallFail() {
        Log.d("TimeManager", "Time lost to repair after ceiling club: -$ceilingFallFail sec")
    }

    fun defenseTurretsFail() {
        Log.d("TimeManager", "Time lost to free from cables: -$defenseTurretsFail sec")
    }

    fun panicAttackFail() {
        Log.d("TimeManager", "Time lost due to panic attack: -$panicAttackFail sec")
    }
}