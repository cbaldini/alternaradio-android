package ar.alternaradio.app;

import android.webkit.WebView;
import android.util.Log;
import android.os.Handler;
import android.os.Looper;

public class NowPlayingExtractor {
    private static final String TAG = "NowPlayingExtractor";
    private static final long EXTRACTION_INTERVAL = 3000; // Cada 3 segundos
    private static Handler handler;
    private static Runnable extractionRunnable;
    private static String lastSongTitle = "";

    public interface NowPlayingCallback {
        void onSongExtracted(String songTitle);
        void onExtractionFailed(String error);
    }

    /**
     * Inicia la extracción periódica del título de la canción.
     * El valor se mantiene fijo y se actualiza cada X segundos si cambia.
     */
    public static void startPeriodicExtraction(WebView webView, NowPlayingCallback callback) {
        if (webView == null) {
            callback.onExtractionFailed("WebView is null");
            return;
        }

        if (handler == null) {
            handler = new Handler(Looper.getMainLooper());
        }

        // Detener extracción anterior si existe
        stopPeriodicExtraction();

        extractionRunnable = () -> {
            extractCurrentSong(webView, new NowPlayingCallback() {
                @Override
                public void onSongExtracted(String songTitle) {
                    // Solo notificar si cambió
                    if (!songTitle.equals(lastSongTitle)) {
                        lastSongTitle = songTitle;
                        Log.d(TAG, "🎵 Nueva canción: " + songTitle);
                        callback.onSongExtracted(songTitle);
                    }
                }

                @Override
                public void onExtractionFailed(String error) {
                    Log.d(TAG, "⚠️ Error: " + error);
                    // Mantener el último valor conocido
                    if (!lastSongTitle.isEmpty()) {
                        callback.onSongExtracted(lastSongTitle);
                    }
                }
            });

            // Programar siguiente extracción
            handler.postDelayed(extractionRunnable, EXTRACTION_INTERVAL);
        };

        // Ejecutar inmediatamente y luego periódicamente
        handler.post(extractionRunnable);
    }

    /**
     * Detiene la extracción periódica.
     */
    public static void stopPeriodicExtraction() {
        if (handler != null && extractionRunnable != null) {
            handler.removeCallbacks(extractionRunnable);
        }
    }

    /**
     * Extrae el título de la canción que suena ahora desde la web (llamada única).
     */
    public static void extractCurrentSong(WebView webView, NowPlayingCallback callback) {
        if (webView == null) {
            callback.onExtractionFailed("WebView is null");
            return;
        }

        // Primero intentar fetch a la API JSON de AzuraCast (plataforma común para radios)
        // Si no funciona, cae al scraping del DOM
        String jsCode = "(function() {" +
                "  var result = null;" +

                // Intento 1: API AzuraCast estándar
                "  try {" +
                "    var xhr = new XMLHttpRequest();" +
                "    xhr.open('GET', '/api/nowplaying/1', false);" +
                "    xhr.send();" +
                "    if (xhr.status === 200) {" +
                "      var data = JSON.parse(xhr.responseText);" +
                "      if (data && data.now_playing && data.now_playing.song) {" +
                "        var song = data.now_playing.song;" +
                "        result = (song.artist ? song.artist + ' - ' : '') + (song.title || '');" +
                "        result = result.trim();" +
                "      }" +
                "    }" +
                "  } catch(e) {}" +

                // Intento 2: Buscar en DOM por selectores específicos de radios
                "  if (!result || result.length < 2) {" +
                "    var selectors = [" +
                "      '.now-playing', '.nowplaying', '.current-track'," +
                "      '.song-title', '.track-title', '.np-title'," +
                "      '#now-playing', '#nowplaying', '#current-song'," +
                "      '[class*=\"nowplaying\"]', '[class*=\"now_playing\"]'," +
                "      '[class*=\"playing\"] .title', '[class*=\"playing\"] .song'" +
                "    ];" +
                "    for (var i = 0; i < selectors.length; i++) {" +
                "      var el = document.querySelector(selectors[i]);" +
                "      if (el) {" +
                "        var t = el.textContent.trim();" +
                "        if (t && t.length > 2 && t.length < 200) {" +
                "          result = t;" +
                "          break;" +
                "        }" +
                "      }" +
                "    }" +
                "  }" +

                // Intento 3: document.title pero SOLO tomando la parte antes del primer | o -
                "  if (!result || result.length < 2) {" +
                "    var t = document.title || '';" +
                "    if (t && t.length > 2 && t.length < 200) {" +
                "      if (t.indexOf(' | ') > -1) t = t.split(' | ')[0].trim();" +
                "      else if (t.indexOf(' - ') > -1) t = t.split(' - ')[0].trim();" +
                "      else if (t.indexOf('|') > -1) t = t.split('|')[0].trim();" +
                "      else if (t.indexOf('-') > -1) t = t.split('-')[0].trim();" +
                "      if (t && t.length > 2 && t.length < 200) {" +
                "        result = t;" +
                "      }" +
                "    }" +
                "  }" +

                "  return result ? result.trim() : '';" +
                "})();";

        webView.evaluateJavascript(jsCode, result -> {
            try {
                if (result == null || result.equals("null") || result.isEmpty()) {
                    callback.onExtractionFailed("No song title found");
                    return;
                }

                // Remover comillas de la respuesta JSON de evaluateJavascript
                String songTitle = result;
                if (songTitle.startsWith("\"")) songTitle = songTitle.substring(1);
                if (songTitle.endsWith("\"")) songTitle = songTitle.substring(0, songTitle.length() - 1);

                // Decodificar caracteres escapados
                songTitle = songTitle
                        .replace("\\\"", "\"")
                        .replace("\\n", " ")
                        .replace("\\r", "")
                        .replace("\\t", " ")
                        .replace("\\\\", "\\")
                        .trim();

                if (songTitle.isEmpty() || songTitle.equals("null")) {
                    callback.onExtractionFailed("Empty song title");
                } else {
                    Log.d(TAG, "✓ Song extracted: " + songTitle);
                    callback.onSongExtracted(songTitle);
                }
            } catch (Exception e) {
                Log.e(TAG, "✗ Error extracting song: " + e.getMessage(), e);
                callback.onExtractionFailed(e.getMessage());
            }
        });
    }
}
