package com.sspirit.nadiiaspaceassistant.services

import android.media.MediaPlayer
import com.sspirit.nadiiaspaceassistant.NadiiaSpaceApplication
import com.sspirit.nadiiaspaceassistant.R

object MediaManager {
    fun playResource(res: Int) {
        val context = NadiiaSpaceApplication.getContext()
        val player = MediaPlayer.create(context, res)
        player.start()
        player.setOnCompletionListener { player.release() }
    }
}