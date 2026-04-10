package ar.alternaradio.app;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.os.Build;
import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.webkit.WebSettings;
import android.webkit.WebChromeClient;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.graphics.Bitmap;
import android.util.Log;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.webkit.SslErrorHandler;
import android.net.http.SslError;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceError;

public class MainActivity extends AppCompatActivity {
    private WebView webView;
    private ProgressBar progressBar;
    private LinearLayout fallbackContainer;
    private TextView fallbackText;
    private Button btnRetry;
    private Button btnOpenBrowser;

    private static final String TAG = "AlternaRadio";

    private static final String HOME_URL = "https://radio.alterna.ar";

    private static final String NOTIF_CHANNEL_ID = "alternaradio_playback";
    private static final int NOTIF_ID = 1001;

    private boolean hasLoadedHome = false;

    // Últimos títulos conocidos (se usan para la notificación al ir a background)
    private String lastPageTitle = null;
    private String lastSongTitle = null;

    private final Handler mainHandler = new Handler(Looper.getMainLooper());
    private final Runnable loadTimeoutRunnable = new Runnable() {
        @Override
        public void run() {
            // Si a esta altura seguimos sin página finalizada, mostramos fallback.
            Log.w(TAG, "⚠ Timeout cargando WebView (pantalla negra posible)");
            showFallback("No se pudo cargar la página. Tocá Reintentar o Abrir en el navegador.");
        }
    };

    private volatile boolean pageFinishedOnce = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "======== INICIO ALTERNA RADIO ========");

        ensureNotificationChannel();

        try {
            setContentView(R.layout.activity_main);
            Log.d(TAG, "✓ Layout cargado");

            webView = findViewById(R.id.webview);
            progressBar = findViewById(R.id.progressBar);
            fallbackContainer = findViewById(R.id.fallbackContainer);
            fallbackText = findViewById(R.id.fallbackText);
            btnRetry = findViewById(R.id.btnRetry);
            btnOpenBrowser = findViewById(R.id.btnOpenBrowser);
            Log.d(TAG, "✓ Vistas obtenidas");

            if (webView == null) {
                Log.e(TAG, "✗ WebView es null");
                finish();
                return;
            }

            if (btnRetry != null) {
                btnRetry.setOnClickListener(v -> {
                    Log.d(TAG, "→ Reintentar click");
                    hasLoadedHome = false;
                    pageFinishedOnce = false;
                    hideFallback();
                    webView.post(this::maybeLoadHome);
                });
            }
            if (btnOpenBrowser != null) {
                btnOpenBrowser.setOnClickListener(v -> {
                    Log.d(TAG, "→ Abrir en navegador click");
                    openInBrowser(HOME_URL);
                });
            }

            configureWebView();

            // Mostrar algo siempre, para evitar negro “silencioso”.
            showFallback("Cargando...");

            // Cargar la URL de forma segura en el loop del UI thread, evitando carreras con el lifecycle.
            webView.post(this::maybeLoadHome);

        } catch (Exception e) {
            Log.e(TAG, "✗ ERROR: " + e.getClass().getSimpleName() + " - " + e.getMessage(), e);
            finish();
        }
    }

    private void configureWebView() {
        Log.d(TAG, "→ Configurando WebView");

        WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setDomStorageEnabled(true);
        settings.setDatabaseEnabled(true);
        settings.setMediaPlaybackRequiresUserGesture(false);
        settings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        settings.setLoadWithOverviewMode(true);
        settings.setUseWideViewPort(true);
        settings.setJavaScriptCanOpenWindowsAutomatically(true);
        Log.d(TAG, "✓ WebSettings configurados");

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                Log.d(TAG, "→ Página inicia: " + url);

                if (!pageFinishedOnce) {
                    showFallback("Cargando...");
                }

                if (progressBar != null) {
                    progressBar.setVisibility(View.VISIBLE);
                    progressBar.setProgress(10);
                }
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                pageFinishedOnce = true;

                // Capturar título de página desde WebView (fallback si WebChromeClient no lo dio)
                try {
                    String t = view.getTitle();
                    if (t != null && !t.trim().isEmpty()) {
                        lastPageTitle = t.trim();
                        // Si no tenemos título de canción de otro lado, usamos el mismo.
                        if (lastSongTitle == null || lastSongTitle.trim().isEmpty()) {
                            lastSongTitle = lastPageTitle;
                        }
                        Log.d(TAG, "→ Título (onPageFinished): " + lastPageTitle);
                    }
                } catch (Throwable ignored) {
                    // noop
                }

                Log.d(TAG, "✓ Página cargada: " + url);

                hideFallback();

                if (progressBar != null) {
                    progressBar.setVisibility(View.GONE);
                }

                // Si terminó, cancelamos timeout.
                mainHandler.removeCallbacks(loadTimeoutRunnable);
            }

            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                super.onReceivedError(view, request, error);
                if (request != null && request.isForMainFrame()) {
                    CharSequence desc = (error != null) ? error.getDescription() : "Error desconocido";
                    Log.e(TAG, "✗ Error WebView (main frame): " + desc);
                    showFallback("Error al cargar. Verificá tu conexión y reintentá.");
                }
            }

            @Override
            @SuppressWarnings("deprecation")
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                super.onReceivedError(view, errorCode, description, failingUrl);
                Log.e(TAG, "✗ Error WebView: " + description + " (" + errorCode + ") url=" + failingUrl);
                showFallback("Error al cargar. Verificá tu conexión y reintentá.");
            }

            @Override
            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                // IMPORTANTE: no bypass. Mostramos fallback para evitar negro.
                String msg = (error != null) ? error.toString() : "SSL error";
                Log.e(TAG, "✗ SSL Error: " + msg);
                if (handler != null) handler.cancel();
                showFallback("Problema de seguridad (SSL) al cargar la página. Abrí en el navegador.");
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                if (request == null || request.getUrl() == null) return false;
                Uri uri = request.getUrl();
                String scheme = uri.getScheme();
                if (scheme == null) return false;

                // Dejar http/https dentro de WebView.
                if (scheme.equalsIgnoreCase("http") || scheme.equalsIgnoreCase("https")) {
                    return false;
                }

                // Cualquier otro esquema (tel:, whatsapp:, intent:) lo mandamos afuera.
                try {
                    startActivity(new Intent(Intent.ACTION_VIEW, uri));
                    return true;
                } catch (Exception e) {
                    Log.w(TAG, "⚠ No se pudo abrir link externo: " + uri, e);
                    return false;
                }
            }
        });
        Log.d(TAG, "✓ WebViewClient establecido");

        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                if (progressBar != null) {
                    progressBar.setProgress(newProgress);
                    if (newProgress >= 100) {
                        progressBar.setVisibility(View.GONE);
                    } else {
                        progressBar.setVisibility(View.VISIBLE);
                    }
                }
            }

            @Override
            public void onReceivedTitle(WebView view, String title) {
                super.onReceivedTitle(view, title);
                if (title != null && !title.trim().isEmpty()) {
                    lastPageTitle = title.trim();
                    // Por ahora usamos el mismo title como canción (hasta que tengamos un selector más específico)
                    if (lastSongTitle == null || lastSongTitle.trim().isEmpty()) {
                        lastSongTitle = lastPageTitle;
                    }
                    Log.d(TAG, "→ Título (onReceivedTitle): " + lastPageTitle);
                }
            }
        });
        Log.d(TAG, "✓ WebChromeClient establecido");
    }

    private void maybeLoadHome() {
        if (hasLoadedHome) {
            Log.d(TAG, "↷ Home ya cargada, no recargo");
            return;
        }
        if (webView == null) {
            Log.e(TAG, "✗ maybeLoadHome: webView null");
            return;
        }
        if (isFinishing() || isDestroyed()) {
            Log.w(TAG, "⚠ maybeLoadHome: Activity no está en estado válido (finishing/destroyed)");
            return;
        }

        hasLoadedHome = true;

        // Watchdog: si WebView queda en negro/colgada, mostramos fallback.
        mainHandler.removeCallbacks(loadTimeoutRunnable);
        mainHandler.postDelayed(loadTimeoutRunnable, 15000);

        Log.d(TAG, "→ Cargando " + HOME_URL);
        webView.loadUrl(HOME_URL);
        Log.d(TAG, "✓ URL solicitada");
    }

    private void showFallback(String message) {
        if (fallbackText != null) fallbackText.setText(message);
        if (fallbackContainer != null) fallbackContainer.setVisibility(View.VISIBLE);
        if (webView != null) webView.setVisibility(View.VISIBLE); // mantenemos WebView debajo
    }

    private void hideFallback() {
        if (fallbackContainer != null) fallbackContainer.setVisibility(View.GONE);
    }

    private void openInBrowser(String url) {
        try {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
        } catch (Exception e) {
            Log.e(TAG, "✗ No se pudo abrir navegador", e);
        }
    }

    private void ensureNotificationChannel() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return;

        NotificationManager nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        if (nm == null) return;

        NotificationChannel existing = nm.getNotificationChannel(NOTIF_CHANNEL_ID);
        if (existing != null) return;

        NotificationChannel channel = new NotificationChannel(
                NOTIF_CHANNEL_ID,
                "Alterna Radio",
                NotificationManager.IMPORTANCE_LOW
        );
        channel.setDescription("Notificaciones al minimizar");
        channel.setLockscreenVisibility(android.app.Notification.VISIBILITY_PUBLIC);
        nm.createNotificationChannel(channel);
    }

    private void showNowPlayingNotification() {
        // Android 13+: si no se concedió permiso de notificaciones, no crashear.
        try {
            if (Build.VERSION.SDK_INT >= 33) {
                if (checkSelfPermission(android.Manifest.permission.POST_NOTIFICATIONS)
                        != android.content.pm.PackageManager.PERMISSION_GRANTED) {
                    Log.w(TAG, "⚠ Sin permiso POST_NOTIFICATIONS; no muestro notificación");
                    return;
                }
            }
        } catch (Throwable t) {
            // noop
        }

        String song = (lastSongTitle != null && !lastSongTitle.trim().isEmpty()) ? lastSongTitle.trim() : null;
        String page = (lastPageTitle != null && !lastPageTitle.trim().isEmpty()) ? lastPageTitle.trim() : null;

        // Formato: "Canción | Página" (sin "Suena ahora")
        String title;
        if (song != null && page != null) {
            title = song + " | " + page;
        } else if (song != null) {
            title = song;
        } else if (page != null) {
            title = page;
        } else {
            title = "Alterna Radio";
        }

        Intent openIntent = new Intent(this, MainActivity.class);
        openIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);

        PendingIntent pendingIntent = PendingIntent.getActivity(
                this,
                0,
                openIntent,
                (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                        ? (PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE)
                        : PendingIntent.FLAG_UPDATE_CURRENT
        );

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, NOTIF_CHANNEL_ID)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(title)
                .setContentText(HOME_URL)
                .setContentIntent(pendingIntent)
                .setOngoing(false)
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_LOW);

        NotificationManagerCompat.from(this).notify(NOTIF_ID, builder.build());
        Log.d(TAG, "✓ Notificación publicada: " + title);
    }

    private void cancelNowPlayingNotification() {
        try {
            NotificationManagerCompat.from(this).cancel(NOTIF_ID);
        } catch (Throwable ignored) {
            // noop
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        Log.d(TAG, "→ onNewIntent: " + intent);

        if (webView != null) {
            webView.post(this::maybeLoadHome);
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && webView != null && webView.canGoBack()) {
            webView.goBack();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "→ onStart");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "→ onResume");
        cancelNowPlayingNotification();
        if (webView != null) webView.onResume();
    }

    @Override
    protected void onPause() {
        Log.d(TAG, "→ onPause");
        if (webView != null) webView.onPause();
        super.onPause();
    }

    @Override
    protected void onStop() {
        Log.d(TAG, "→ onStop");
        showNowPlayingNotification();
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        Log.d(TAG, "→ onDestroy");

        mainHandler.removeCallbacks(loadTimeoutRunnable);

        if (webView != null) {
            try {
                webView.stopLoading();
                webView.loadUrl("about:blank");
                webView.setWebChromeClient(null);
                webView.setWebViewClient(null);
                webView.removeAllViews();
                webView.destroy();
            } catch (Throwable t) {
                Log.w(TAG, "⚠ Error limpiando WebView: " + t.getClass().getSimpleName() + " - " + t.getMessage());
            } finally {
                webView = null;
            }
        }
        super.onDestroy();
    }
}
