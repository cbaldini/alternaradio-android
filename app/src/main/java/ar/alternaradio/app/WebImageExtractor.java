package ar.alternaradio.app;

import android.webkit.WebView;
import android.util.Log;
import java.util.ArrayList;
import java.util.List;

public class WebImageExtractor {
    private static final String TAG = "WebImageExtractor";

    public interface ImageExtractionCallback {
        void onImagesExtracted(List<String> imageUrls);
        void onExtractionFailed(String error);
    }

    public static void extractImages(WebView webView, ImageExtractionCallback callback) {
        if (webView == null) {
            callback.onExtractionFailed("WebView is null");
            return;
        }

        webView.evaluateJavascript(
                "(function() {" +
                        "  var images = [];" +
                        "  var imgElements = document.querySelectorAll('img');" +
                        "  for (var i = 0; i < imgElements.length; i++) {" +
                        "    var src = imgElements[i].src;" +
                        "    if (src && src.length > 10) {" +  // Filtrar URLs muy cortas (probablemente inválidas)
                        "      images.push(src);" +
                        "    }" +
                        "  }" +
                        "  return JSON.stringify(images);" +
                        "})();",
                result -> {
                    try {
                        if (result == null || result.equals("\"[]\"")) {
                            callback.onExtractionFailed("No images found");
                            return;
                        }

                        // Remover comillas si existen
                        String jsonString = result;
                        if (jsonString.startsWith("\"")) {
                            jsonString = jsonString.substring(1);
                        }
                        if (jsonString.endsWith("\"")) {
                            jsonString = jsonString.substring(0, jsonString.length() - 1);
                        }

                        // Parsear array de URLs manualmente
                        List<String> urls = new ArrayList<>();
                        String[] parts = jsonString.split(",");
                        for (String part : parts) {
                            String url = part.trim()
                                    .replace("[", "")
                                    .replace("]", "")
                                    .replace("\"", "")
                                    .replace("\\", "");
                            // Validar que sea una URL válida
                            if (!url.isEmpty() && (url.startsWith("http") || url.startsWith("/"))) {
                                urls.add(url);
                            }
                        }

                        Log.d(TAG, "✓ Extracted " + urls.size() + " images");
                        if (urls.isEmpty()) {
                            callback.onExtractionFailed("No valid images found");
                        } else {
                            callback.onImagesExtracted(urls);
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "✗ Error extracting images: " + e.getMessage(), e);
                        callback.onExtractionFailed(e.getMessage());
                    }
                }
        );
    }

    public static void extractSliderImages(WebView webView, String selectorClass, ImageExtractionCallback callback) {
        if (webView == null) {
            callback.onExtractionFailed("WebView is null");
            return;
        }

        String jsCode = String.format(
                "(function() {" +
                        "  var images = [];" +
                        "  var container = document.querySelector('.%s');" +
                        "  if (!container) return JSON.stringify([]);" +
                        "  var imgElements = container.querySelectorAll('img');" +
                        "  for (var i = 0; i < imgElements.length; i++) {" +
                        "    var src = imgElements[i].src;" +
                        "    if (src && src.length > 0) {" +
                        "      images.push(src);" +
                        "    }" +
                        "  }" +
                        "  return JSON.stringify(images);" +
                        "})();",
                selectorClass
        );

        webView.evaluateJavascript(jsCode, result -> {
            try {
                if (result == null) {
                    extractImages(webView, callback);
                    return;
                }

                String jsonString = result;
                if (jsonString.startsWith("\"")) {
                    jsonString = jsonString.substring(1);
                }
                if (jsonString.endsWith("\"")) {
                    jsonString = jsonString.substring(0, jsonString.length() - 1);
                }

                List<String> urls = new ArrayList<>();
                String[] parts = jsonString.split(",");
                for (String part : parts) {
                    String url = part.trim()
                            .replace("[", "")
                            .replace("]", "")
                            .replace("\"", "")
                            .replace("\\", "");
                    if (!url.isEmpty()) {
                        urls.add(url);
                    }
                }

                Log.d(TAG, "Extracted " + urls.size() + " slider images");
                if (urls.isEmpty()) {
                    extractImages(webView, callback);
                } else {
                    callback.onImagesExtracted(urls);
                }
            } catch (Exception e) {
                Log.e(TAG, "Error extracting slider images: " + e.getMessage(), e);
                extractImages(webView, callback);
            }
        });
    }
}

