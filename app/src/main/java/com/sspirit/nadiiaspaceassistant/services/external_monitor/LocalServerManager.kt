package com.sspirit.nadiiaspaceassistant.services.external_monitor

import com.sspirit.nadiiaspaceassistant.NadiiaSpaceApplication
import fi.iki.elonen.NanoHTTPD
import java.io.ByteArrayInputStream
import java.io.IOException
import java.io.InputStream
import java.nio.charset.StandardCharsets
import java.util.UUID
import kotlin.concurrent.thread

private const val port = 9296
private const val IMAGES_ENDPOINT = "image"
private const val IMAGES_FOLDER = "ServerImages"
private const val UPDATES_TIMEOUT = 5 * 60 * 1000

object LocalServerManager : NanoHTTPD(port) {
    private val updatesNotifier = LocalServerUpdatesNotifier("initial")

    fun startServer() {
        try {
            start(SOCKET_READ_TIMEOUT, false)
            updatesNotifier.waitingTimeout = UPDATES_TIMEOUT
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    fun setImage(path: String) {
        updatesNotifier.value = path
    }

    override fun serve(session: IHTTPSession?): Response {
        if (session == null) {
            return newFixedLengthResponse(Response.Status.NOT_FOUND, "text/plain", "404 Not Found")
        }

        val imagesPrefix = "/${IMAGES_ENDPOINT}/"
        if (session.uri.startsWith(imagesPrefix)) {
            val path = session.uri.drop(imagesPrefix.length)
            return serveImage(path)
        }

        return when (session.uri) {
            "/" -> serveHtmlPage()
            "/events" -> createEventResponse()
            else -> newFixedLengthResponse(Response.Status.NOT_FOUND, "text/plain", "404 Not Found")
        }
    }

    private fun createEventResponse(): Response {
        return try {
            updatesNotifier.waitUpdates()
            newFixedLengthResponse(Response.Status.OK, "text/plain", updatesNotifier.value)
        } catch (e: InterruptedException) {
            e.printStackTrace()
            newFixedLengthResponse(Response.Status.INTERNAL_ERROR, "text/plain", "Error")
        }
    }

    private fun serveHtmlPage(): Response {
        val htmlContent = """
            <!DOCTYPE html>
            <html lang="en">
            <head>
                <meta charset="UTF-8">
                <meta name="viewport" content="width=device-width, initial-scale=1.0">
                <style>
                    body { text-align: center; font-family: Arial, sans-serif; }
                    img { width: 80%; max-width: 600px; }
                </style>
            </head>
            <body bgcolor="black">
                <img id="image" src="/image/initial.png" alt="Изображение" onclick="toggleFullScreen()" style="max-width: 100vw; max-height: 100vh; width: auto; height: auto;">
                
                <script>
                    function toggleFullScreen() {
                        if (!document.fullscreenElement) {
                            document.documentElement.requestFullscreen()
                        } else {
                            document.exitFullscreen();
                        }
                    }
                </script>
                
                <script>
                function fetchData() {
                    fetch('/events')
                        .then(response => response.text())
                        .then(data => {
                            document.getElementById("image").src = "/image/" + data;
                            fetchData();
                        })
                        .catch(error => {
                            console.error('Error fetching data:', error);
                            setTimeout(fetchData, 1000);
                        });
                }

                fetchData();
            </script>
                
            </body>
            </html>
        """.trimIndent()

        return newFixedLengthResponse(Response.Status.OK, "text/html", htmlContent)
    }

    private fun serveImage(path: String): Response =
        try {
            val context = NadiiaSpaceApplication.getContext()
            val inputStream: InputStream = context.assets.open("$IMAGES_FOLDER/$path")
            val size = inputStream.available().toLong()
            newChunkedResponse(Response.Status.OK, "image/png", inputStream).apply {
                addHeader("Content-Length", size.toString())
            }
        } catch (e: Exception) {
            newFixedLengthResponse(Response.Status.NOT_FOUND, "text/plain", "Image not found or error")
        }
}