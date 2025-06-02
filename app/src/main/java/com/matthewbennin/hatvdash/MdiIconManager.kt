package com.matthewbennin.hatvdash

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Picture
import android.util.Log
import com.caverock.androidsvg.SVG
import java.io.File
import java.io.FileOutputStream
import java.net.HttpURLConnection
import java.net.URL
import kotlin.concurrent.thread
import androidx.core.graphics.createBitmap

object MdiIconManager {

    private const val ICON_WIDTH = 96  // Adjust for display size on TV
    private const val ICON_HEIGHT = 96

    fun getIconFilename(mdi: String): String {
        return "mdi_${mdi.removePrefix("mdi:")}.svg"
    }

    fun getCachedIconFile(context: Context, mdi: String): File {
        val filename = getIconFilename(mdi)
        return File(context.cacheDir, filename)
    }

    fun isIconCached(context: Context, mdi: String): Boolean {
        return getCachedIconFile(context, mdi).exists()
    }

    fun loadOrFetchIcon(
        context: Context,
        mdi: String,
        onIconReady: (Bitmap?) -> Unit
    ) {
        val file = getCachedIconFile(context, mdi)

        if (file.exists()) {
            renderSvgToBitmap(file, onIconReady)
            return
        }

        val url = "https://api.iconify.design/$mdi.svg"

        thread {
            try {
                val connection = URL(url).openConnection() as HttpURLConnection
                connection.connectTimeout = 5000
                connection.readTimeout = 5000
                connection.connect()

                if (connection.responseCode == 200) {
                    connection.inputStream.use { input ->
                        FileOutputStream(file).use { output ->
                            input.copyTo(output)
                        }
                    }
                    renderSvgToBitmap(file, onIconReady)
                } else {
                    Log.e("MdiIconManager", "SVG fetch failed: HTTP ${connection.responseCode}")
                    onIconReady(null)
                }

            } catch (e: Exception) {
                Log.e("MdiIconManager", "SVG fetch error: ${e.message}", e)
                onIconReady(null)
            }
        }
    }

    private fun renderSvgToBitmap(file: File, callback: (Bitmap?) -> Unit) {
        try {
            val svg = SVG.getFromInputStream(file.inputStream())
            svg.setDocumentWidth("${ICON_WIDTH}px")
            svg.setDocumentHeight("${ICON_HEIGHT}px")

            val picture: Picture = svg.renderToPicture()
            val bitmap = createBitmap(ICON_WIDTH, ICON_HEIGHT)
            val canvas = Canvas(bitmap)
            canvas.drawPicture(picture)

            // Apply tint after rendering
            val tinted = createBitmap(ICON_WIDTH, ICON_HEIGHT)
            val tintCanvas = Canvas(tinted)
            val paint = android.graphics.Paint().apply {
                colorFilter = android.graphics.PorterDuffColorFilter(
                    Color.rgb(68, 115, 158),  // Primary Blue
                    android.graphics.PorterDuff.Mode.SRC_IN
                )
            }
            tintCanvas.drawBitmap(bitmap, 0f, 0f, paint)

            callback(tinted)

        } catch (e: Exception) {
            Log.e("MdiIconManager", "SVG render error: ${e.message}", e)
            callback(null)
        }
    }


}
