package com.sspirit.nadiiaspaceassistant.services

import android.util.Log
import androidx.compose.runtime.MutableState
import com.sspirit.nadiiaspaceassistant.R
import com.sspirit.nadiiaspaceassistant.models.missions.building.BuildingDoor
import com.sspirit.nadiiaspaceassistant.models.missions.building.BuildingDoorTurn
import com.sspirit.nadiiaspaceassistant.models.missions.building.BuildingRoom
import com.sspirit.nadiiaspaceassistant.models.missions.building.transport.BuildingTransport
import com.sspirit.nadiiaspaceassistant.utils.StateObservableValue
import com.sspirit.nadiiaspaceassistant.utils.Updater
import com.sspirit.nadiiaspaceassistant.utils.update
import java.time.Duration
import java.time.LocalDateTime
import java.util.Timer
import java.util.TimerTask

data class CustomTimer(
    val id: String,
    val title: String,
    val duration: Int,
    val timeLeft: StateObservableValue<Double> = StateObservableValue(duration.toDouble()),
    val onFinish: () -> Unit
) {
    fun addObserver(observer: MutableState<Double>) = timeLeft.addObserver(observer)
    fun removeObserver(observer: MutableState<Double>) = timeLeft.removeObserver(observer)
}

object PropertyEvacuationTimeManager {

    const val nodeOptimizationBonus = 120
    const val bigStabilizationBonus = 240
    const val smallStabilizationBonus = 180
    const val cablesFallFail = 45
    const val ceilingFallFail = 45
    const val defenseTurretsFail = 45
    const val panicAttackFail = 30

    private val timeLeftMedia = mapOf(
        600.0 to R.raw.self_destruction_10min,
        300.0 to R.raw.self_destruction_5min,
        120.0 to R.raw.self_destruction_2min,
        60.0 to R.raw.self_destruction_1min,
        30.0 to R.raw.self_destruction_30sec,
        10.0 to R.raw.self_destruction_10sec,
        5.0 to R.raw.self_destruction_5sec,
        0.0 to R.raw.self_destruction
    )

    val timeLeft = StateObservableValue(0f.toDouble())
    val isActive = StateObservableValue(false)
    val customTimers: MutableMap<String, CustomTimer> = mutableMapOf()
    val customTimersObservers: MutableSet<Updater> = mutableSetOf()

    private val timer = Timer()
    private var previousTime: LocalDateTime? = null

    private object TimeTask : TimerTask() {
        override fun run() {
            if (!isActive.value) return

            val delta = if (previousTime != null) {
                val duration = Duration.between(previousTime, LocalDateTime.now())
                duration.toMillis() / 1000.0
            } else
                1.0

            changeTimeLeft(-delta)

            var listUpdated = false
            val iterator = customTimers.entries.iterator()
            while (iterator.hasNext()) {
                val timer = iterator.next().value
                timer.timeLeft.value -= delta
                if (timer.timeLeft.value <= 0) {
                    iterator.remove()
                    timer.onFinish()
                    listUpdated = true
                }
            }

            if (listUpdated) customTimersObservers.update()
            previousTime = LocalDateTime.now()
        }
    }

    init {
        timer.schedule(TimeTask, 0, 1000)
    }

    fun setupTimeLeft(seconds: Int) {
        timeLeft.value = seconds.toDouble()
        previousTime = null
    }

    fun play() {
        isActive.value = true
    }

    fun pause() {
        isActive.value = false
        previousTime = null
    }

    fun addCustomTimer(timer: CustomTimer) {
        customTimers[timer.id] = timer
        customTimersObservers.update()
    }

    fun getCustomTimer(id: String): CustomTimer? {
        return customTimers[id]
    }

    fun removeCustomTimer(id: String) {
        customTimers.remove(id)
        customTimersObservers.update()
    }

    fun changeTimeLeft(delta: Double) {
        val new = timeLeft.value + delta
        timeLeftMedia.forEach {
            if (timeLeft.value > it.key && it.key >= new)
                MediaManager.playResource(it.value)
        }
        timeLeft.value = new
    }

    fun doorOpeningTry(door: BuildingDoor) {
        if (door.turn == BuildingDoorTurn.AUTOMATIC) {
            Log.d("TimeManager", "Automatic door opens immediately")
        }
        else {
            Log.d("TimeManager", "Try to open manual door: -20 sec")
            changeTimeLeft(-20.0)
        }
    }

    fun doorClosing() {
        Log.d("TimeManager", "Door was closed immediately")
    }

    fun doorHackingTry() {
        Log.d("TimeManager", "Try to hacking door: -30 sec")
        changeTimeLeft(-30.0)
    }

    fun doorDestruction() {
        Log.d("TimeManager", "Destruct door immediately")
    }

    fun ventGrilleRemoving() {
        Log.d("TimeManager", "Remove ventilation grille immediately")
    }

    fun ventCrawlingTry() {
        Log.d("TimeManager", "Try to crawl through vent: -40 sec")
        changeTimeLeft(-40.0)
    }

    fun holeMaking() {
        Log.d("TimeManager", "Make a hole immediately")
    }

    fun jumpingIntoHole() {
        Log.d("TimeManager", "Jump into a hole immediately")
    }

    fun carefullyDownIntoHole() {
        Log.d("TimeManager", "Carefully down into a hole: -40 sec")
        changeTimeLeft(-40.0)
    }

    fun downByHeap() {
        Log.d("TimeManager", "Down into hole uses heap: -10 sec")
        changeTimeLeft(-10.0)
    }

    fun upByHeap() {
        Log.d("TimeManager", "Up into hole uses heap: -10 sec")
        changeTimeLeft(-10.0)
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
        changeTimeLeft(-duration.toDouble())
    }

    fun safetyConsoleHackingTry() {
        Log.d("TimeManager", "Try to hacking safety console: -20 sec")
        changeTimeLeft(-20.0)
    }

    fun acidChargeRecharge() {
        Log.d("TimeManager", "Acid charge recharged: -20 sec")
        changeTimeLeft(-20.0)
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
        if (success) {
            Log.d("TimeManager", "Energy node optimization success: +$nodeOptimizationBonus sec")
            changeTimeLeft(nodeOptimizationBonus.toDouble())
        }
        else {
            Log.d("TimeManager", "Energy node optimization failed immediately")
        }
    }

    fun energyCoreRodUsage(isBig: Boolean) {
        if (isBig) {
            Log.d("TimeManager", "Big reactor rod used: +$bigStabilizationBonus sec")
            changeTimeLeft(bigStabilizationBonus.toDouble())
        }
        else {
            Log.d("TimeManager", "Small reactor rod used: +$smallStabilizationBonus sec")
            changeTimeLeft(smallStabilizationBonus.toDouble())
        }
    }

    fun mainframeGoalDataSearch() {
        Log.d("TimeManager", "Searched goal data in mainframe: -30 sec")
        changeTimeLeft(-30.0)
    }

    fun autoDoctorHealing() {
        Log.d("TimeManager", "Healed uses auto-doctor: -30 sec")
        changeTimeLeft(-30.0)
    }

    fun cablesFallFail() {
        Log.d("TimeManager", "Time lost to free from cables: -$cablesFallFail sec")
        changeTimeLeft(-cablesFallFail.toDouble())
    }

    fun ceilingFallFail() {
        Log.d("TimeManager", "Time lost to repair after ceiling club: -$ceilingFallFail sec")
        changeTimeLeft(-ceilingFallFail.toDouble())
    }

    fun defenseTurretsFail() {
        Log.d("TimeManager", "Time lost to free from cables: -$defenseTurretsFail sec")
        changeTimeLeft(-defenseTurretsFail.toDouble())
    }

    fun panicAttackFail() {
        Log.d("TimeManager", "Time lost due to panic attack: -$panicAttackFail sec")
        changeTimeLeft(-panicAttackFail.toDouble())
    }
}