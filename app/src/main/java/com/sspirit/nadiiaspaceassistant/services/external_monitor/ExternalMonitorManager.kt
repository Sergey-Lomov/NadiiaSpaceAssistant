package com.sspirit.nadiiaspaceassistant.services.external_monitor

import com.sspirit.nadiiaspaceassistant.models.cosmology.SpaceObject
import com.sspirit.nadiiaspaceassistant.models.cosmology.SpaceSystem
import java.util.Timer
import java.util.TimerTask

private const val GALLERY_DELAY = 10000.toLong()

object ExternalMonitorManager {
    private var galleryTimer:Timer? = null

    private class GalleryTask(val paths: Array<String>) : TimerTask() {
        private var index = 0

        override fun run() {
            val path = paths[index % paths.size]
            LocalServerManager.setImage(path)
            index++
        }
    }

    private fun starFile(system: SpaceSystem): String = "${system.id}/${system.id}.png"
    private fun objectFile(obj: SpaceObject): String = "${obj.parent.id}/${obj.id}.png"

    fun showSystemStar(system: SpaceSystem) = showSingleImage(starFile(system))
    fun showSpaceObject(obj: SpaceObject) = showSingleImage(objectFile(obj))

    private fun showSingleImage(path: String) {
        galleryTimer?.cancel()
        galleryTimer = null
        LocalServerManager.setImage(path)
    }

    fun showSystemGallery(system: SpaceSystem) {
        val objectsPaths = system.objects.map { objectFile(it) }
        val paths = arrayOf(starFile(system)).plus(objectsPaths)
        startGallery(paths)
    }

    private fun startGallery(paths: Array<String>) {
        galleryTimer?.cancel()
        galleryTimer = Timer()
        galleryTimer?.schedule(GalleryTask(paths), 0, GALLERY_DELAY)
    }
}