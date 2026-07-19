package ar.alternaradio.app;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.viewpager2.widget.ViewPager2;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.widget.ImageView;
import android.os.Build;
import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.webkit.WebSettings;
import android.webkit.WebChromeClient;
import android.view.KeyEvent;
import android.view.View;
import android.view.Gravity;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.FrameLayout;
import ar.alternaradio.app.SevenSegmentView;
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
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private WebView webView;
    private SevenSegmentView frequencyDisplay;
    private TextView nowPlayingTitle;
    private Button btnDialLeft;
    private Button btnDialRight;
    private Button btnInfo;
    private Button btnWeb;
    private View dialNeedle;

    // Variables para carousel y animaciones
    private ViewPager2 imageCarousel;
    private FrameLayout speakerContainer;
    private EqualizerAnimator equalizerAnimator;
    private List<View> eqBars;
    private ImageCarouselAdapter carouselAdapter;
    private List<String> carouselImages;
    private SpeakerAnimationController speakerAnimator;
    private boolean isCarouselVisible = true;
    private int currentImageIndex = 0;

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
            updateNowPlayingDisplay("Error cargando");
        }
    };

    // Extracción periódica de canciones (ya no usamos Runnable manual)
    // Se inicia en onCreate con NowPlayingExtractor.startPeriodicExtraction()

    // Runnable para animar el parlante continuamente
    private final Runnable animateSpeakerRunnable = new Runnable() {
        private int animationPhase = 0;

        @Override
        public void run() {
            if (speakerAnimator != null && isCarouselVisible && !isDestroyed() && !isFinishing()) {
                switch (animationPhase % 4) {
                    case 0:
                        speakerAnimator.pulseBeat();
                        break;
                    case 1:
                        speakerAnimator.openAndClose();
                        break;
                    case 2:
                        speakerAnimator.rotateOnce();
                        break;
                    case 3:
                        speakerAnimator.vibrate();
                        break;
                }
                animationPhase++;
            }
            mainHandler.postDelayed(this, 1500); // Animar cada 1.5 segundos
        }
    };

    // Runnable para auto-scroll del carousel
    private final Runnable autoScrollCarouselRunnable = new Runnable() {
        @Override
        public void run() {
            if (imageCarousel != null && isCarouselVisible && !carouselImages.isEmpty() && !isDestroyed() && !isFinishing()) {
                int nextIndex = (currentImageIndex + 1) % carouselImages.size();
                imageCarousel.setCurrentItem(nextIndex, true);
            }
            mainHandler.postDelayed(this, 5000); // Auto-scroll cada 5 segundos
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

            // Obtener vistas
            webView = findViewById(R.id.webview);
            nowPlayingTitle = findViewById(R.id.nowPlayingTitle);
            frequencyDisplay = findViewById(R.id.frequencyDisplay);
            if (frequencyDisplay != null) frequencyDisplay.setText("88.1");
            btnDialLeft = findViewById(R.id.btnDialLeft);
            btnDialRight = findViewById(R.id.btnDialRight);
            btnInfo = findViewById(R.id.btnInfo);
            btnWeb = findViewById(R.id.btnWeb);
            dialNeedle = findViewById(R.id.dialNeedle);
            imageCarousel = findViewById(R.id.imageCarousel);
            speakerContainer = findViewById(R.id.speakerContainer);

            // Inicializar ecualizador animado
            eqBars = new ArrayList<>();
            for (int i = 1; i <= 12; i++) {
                int viewId = getResources().getIdentifier("eq" + i, "id", getPackageName());
                View bar = findViewById(viewId);
                if (bar != null) {
                    eqBars.add(bar);
                    Log.d(TAG, "✓ EQ bar " + i + " encontrado");
                } else {
                    Log.w(TAG, "✗ EQ bar " + i + " NO encontrado");
                }
            }
            if (!eqBars.isEmpty()) {
                equalizerAnimator = new EqualizerAnimator(eqBars);
                Log.d(TAG, "✓ Ecualizador inicializado con " + eqBars.size() + " barras");
                // Iniciar animación inmediatamente
                equalizerAnimator.startAnimation();
            } else {
                Log.e(TAG, "✗ No se encontraron barras del ecualizador");
            }


            if (webView == null) {
                Log.e(TAG, "✗ WebView es null");
                finish();
                return;
            }

            // Aplicar outline redondeado al speakerContainer para recortar la imagen
            if (speakerContainer != null) {
                speakerContainer.post(() -> {
                    android.view.ViewOutlineProvider outlineProvider = new android.view.ViewOutlineProvider() {
                        @Override
                        public void getOutline(android.view.View view, android.graphics.Outline outline) {
                            outline.setRoundRect(0, 0, view.getWidth(), view.getHeight(),
                                android.util.TypedValue.applyDimension(
                                    android.util.TypedValue.COMPLEX_UNIT_DIP, 24,
                                    getResources().getDisplayMetrics()
                                )
                            );
                        }
                    };
                    speakerContainer.setOutlineProvider(outlineProvider);
                    speakerContainer.setClipToOutline(true);
                });
            }

            // Posicionar el dial needle después del 88 (aproximadamente 5-10% del ancho)
            if (dialNeedle != null && dialNeedle.getParent() instanceof FrameLayout) {
                FrameLayout parent = (FrameLayout) dialNeedle.getParent();

                // Esperar a que el layout se haya renderizado
                parent.post(() -> {
                    FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) dialNeedle.getLayoutParams();
                    if (params != null && parent.getWidth() > 0) {
            // Posicionar el dial needle (80px más a la izquierda, aproximadamente 10% del ancho)
                    params.leftMargin = (int) (parent.getWidth() * 0.10);
                    params.gravity = Gravity.CENTER_VERTICAL | Gravity.LEFT;
                        dialNeedle.setLayoutParams(params);
                        Log.d(TAG, "✓ Dial needle posicionado al 75% - leftMargin: " + params.leftMargin);
                    }
                });
            }

            // Inicializar carousel
            carouselImages = new ArrayList<>();
            carouselAdapter = new ImageCarouselAdapter(this, carouselImages);
            imageCarousel.setAdapter(carouselAdapter);

            // Configurar carousel como vertical (de arriba hacia abajo)
            imageCarousel.setOrientation(ViewPager2.ORIENTATION_VERTICAL);

            // Callbacks del carousel
            imageCarousel.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
                @Override
                public void onPageSelected(int position) {
                    super.onPageSelected(position);
                    currentImageIndex = position;
                    if (speakerAnimator != null) {
                        speakerAnimator.complexBeatAnimation();
                    }
                    Log.d(TAG, "→ Carousel image: " + (position + 1) + "/" + carouselImages.size());
                }
            });

            // Inicializar animador del parlante
            speakerAnimator = new SpeakerAnimationController(speakerContainer);

            // Botones de control
            if (btnInfo != null) {
                btnInfo.setOnClickListener(v -> {
                    Log.d(TAG, "→ INFO click");
                    if (!isCarouselVisible) {
                        // Primera vez: extraer imágenes
                        extractWebContent();
                    }
                    toggleCarousel();
                });
            }
            if (btnWeb != null) {
                btnWeb.setOnClickListener(v -> {
                    Log.d(TAG, "→ WEB click");
                    openInBrowser(HOME_URL);
                });
            }
            if (btnDialLeft != null) {
                btnDialLeft.setOnClickListener(v -> {
                    if (isCarouselVisible && currentImageIndex > 0) {
                        imageCarousel.setCurrentItem(currentImageIndex - 1, true);
                    }
                });
            }
            if (btnDialRight != null) {
                btnDialRight.setOnClickListener(v -> {
                    if (isCarouselVisible && currentImageIndex < carouselImages.size() - 1) {
                        imageCarousel.setCurrentItem(currentImageIndex + 1, true);
                    }
                });
            }

            configureWebView();
            webView.post(this::maybeLoadHome);

            // Iniciar extracción periódica de canciones (se mantiene fijo)
            mainHandler.postDelayed(() -> {
                NowPlayingExtractor.startPeriodicExtraction(webView, new NowPlayingExtractor.NowPlayingCallback() {
                    @Override
                    public void onSongExtracted(String songTitle) {
                        lastSongTitle = songTitle;
                        updateNowPlayingDisplay(songTitle);

                        // Iniciar animación del ecualizador cuando se extrae canción
                        if (equalizerAnimator != null && !equalizerAnimator.isAnimating()) {
                            equalizerAnimator.startAnimation();
                        }
                    }

                    @Override
                    public void onExtractionFailed(String error) {
                        Log.w(TAG, "⚠ Error extrayendo canción: " + error);
                    }
                });
            }, 2000);

            // Iniciar animación del parlante
            mainHandler.post(animateSpeakerRunnable);

            // Iniciar auto-scroll del carousel
            mainHandler.post(autoScrollCarouselRunnable);

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
                    updateNowPlayingDisplay("Cargando...");
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
                        Log.d(TAG, "→ Título página (onPageFinished): " + lastPageTitle);
                        // NO actualizar nowPlayingTitle - NowPlayingExtractor lo hace correctamente
                    }
                } catch (Throwable ignored) {
                    // noop
                }

                Log.d(TAG, "✓ Página cargada: " + url);

                // Si terminó, cancelamos timeout.
                mainHandler.removeCallbacks(loadTimeoutRunnable);
            }

            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                super.onReceivedError(view, request, error);
                if (request != null && request.isForMainFrame()) {
                    CharSequence desc = (error != null) ? error.getDescription() : "Error desconocido";
                    Log.e(TAG, "✗ Error WebView (main frame): " + desc);
                    updateNowPlayingDisplay("Error de conexión");
                }
            }

            @Override
            @SuppressWarnings("deprecation")
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                super.onReceivedError(view, errorCode, description, failingUrl);
                Log.e(TAG, "✗ Error WebView: " + description + " (" + errorCode + ") url=" + failingUrl);
                updateNowPlayingDisplay("Error de conexión");
            }

            @Override
            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                // IMPORTANTE: no bypass. Mostramos error para evitar negro.
                String msg = (error != null) ? error.toString() : "SSL error";
                Log.e(TAG, "✗ SSL Error: " + msg);
                if (handler != null) handler.cancel();
                updateNowPlayingDisplay("Error de seguridad SSL");
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
            public void onReceivedTitle(WebView view, String title) {
                super.onReceivedTitle(view, title);
                if (title != null && !title.trim().isEmpty()) {
                    lastPageTitle = title.trim();
                    Log.d(TAG, "→ Título página: " + lastPageTitle);
                    // NO actualizar nowPlayingTitle aquí - NowPlayingExtractor lo hace correctamente
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

    private void updateNowPlayingDisplay(String text) {
        if (nowPlayingTitle != null) {
            nowPlayingTitle.setText(text);
        }
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

        String song = (lastSongTitle != null && !lastSongTitle.trim().isEmpty()) ? lastSongTitle.trim() : "Alterna Radio";
        String page = (lastPageTitle != null && !lastPageTitle.trim().isEmpty()) ? lastPageTitle.trim() : HOME_URL;

        // Mostrar el título de la canción como contenido principal
        String notificationTitle = "🎵 AHORA SUENA";
        String notificationContent = song;

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
                .setContentTitle(notificationTitle)
                .setContentText(notificationContent)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(notificationContent))
                .setContentIntent(pendingIntent)
                .setOngoing(false)
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_LOW);

        NotificationManagerCompat.from(this).notify(NOTIF_ID, builder.build());
        Log.d(TAG, "✓ Notificación publicada: " + notificationTitle + " - " + notificationContent);
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
        Log.d(TAG, "→ onPause (pantalla apagada, sonido continúa)");
        // NO pausar WebView para que el sonido siga reproduciéndose
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

        // Detener extracción periódica
        NowPlayingExtractor.stopPeriodicExtraction();

        // Limpiar todos los Runnables
        mainHandler.removeCallbacks(loadTimeoutRunnable);
        mainHandler.removeCallbacks(animateSpeakerRunnable);
        mainHandler.removeCallbacks(autoScrollCarouselRunnable);

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

    private void extractWebContent() {
        // Extraer imágenes desde la web
        Log.d(TAG, "→ extractWebContent: Extrayendo imágenes...");
        WebImageExtractor.extractImages(webView, new WebImageExtractor.ImageExtractionCallback() {
            @Override
            public void onImagesExtracted(List<String> imageUrls) {
                Log.d(TAG, "✓ Imágenes extraídas: " + imageUrls.size());
                if (!imageUrls.isEmpty()) {
                    carouselAdapter.updateImages(imageUrls);
                    currentImageIndex = 0;
                    if (imageCarousel != null) {
                        imageCarousel.setCurrentItem(0, false);
                    }
                }
            }

            @Override
            public void onExtractionFailed(String error) {
                Log.w(TAG, "⚠ Error extrayendo imágenes: " + error);
            }
        });
    }

    private void toggleCarousel() {
        isCarouselVisible = !isCarouselVisible;

        android.widget.ImageView textureOverlay = findViewById(R.id.speakerTextureOverlay);

        if (isCarouselVisible) {
            // Abrir: deslizar textura hacia arriba y desaparecer
            if (textureOverlay != null) {
                textureOverlay.animate()
                        .translationY(-textureOverlay.getHeight())
                        .alpha(0f)
                        .setDuration(400)
                        .withEndAction(() -> textureOverlay.setVisibility(View.GONE))
                        .start();
            }
        } else {
            // Cerrar: volver la textura desde arriba
            if (textureOverlay != null) {
                textureOverlay.setVisibility(View.VISIBLE);
                textureOverlay.setTranslationY(-textureOverlay.getHeight());
                textureOverlay.setAlpha(0f);
                textureOverlay.animate()
                        .translationY(0f)
                        .alpha(1f)
                        .setDuration(400)
                        .start();
            }
        }
        Log.d(TAG, "→ Carousel " + (isCarouselVisible ? "mostrado" : "oculto"));
    }
}
