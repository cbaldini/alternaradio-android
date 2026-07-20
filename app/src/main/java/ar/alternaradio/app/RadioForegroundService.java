package ar.alternaradio.app;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.os.PowerManager;
import android.util.Log;

import androidx.core.app.NotificationCompat;

/**
 * Servicio en primer plano que mantiene la radio reproduciendo
 * cuando la pantalla está apagada o la app pasa a segundo plano.
 * Adquiere un WakeLock parcial para evitar que la CPU duerma.
 */
public class RadioForegroundService extends Service {

    private static final String TAG = "RadioForegroundService";
    public static final String CHANNEL_ID = "alternaradio_fg_service";
    public static final int NOTIF_ID = 2001;

    public static final String ACTION_START = "ar.alternaradio.app.ACTION_START";
    public static final String ACTION_STOP  = "ar.alternaradio.app.ACTION_STOP";
    public static final String EXTRA_SONG   = "song_title";

    private PowerManager.WakeLock wakeLock;

    @Override
    public void onCreate() {
        super.onCreate();
        ensureChannel();

        PowerManager pm = (PowerManager) getSystemService(POWER_SERVICE);
        if (pm != null) {
            wakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "AlternaRadio::StreamLock");
            wakeLock.setReferenceCounted(false);
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent == null) return START_STICKY;

        String action = intent.getAction();

        if (ACTION_STOP.equals(action)) {
            stopForeground(true);
            stopSelf();
            return START_NOT_STICKY;
        }

        // ACTION_START o relanzamiento
        String song = intent.getStringExtra(EXTRA_SONG);
        if (song == null || song.trim().isEmpty()) song = "Alterna Radio FM 88.1";

        startForeground(NOTIF_ID, buildNotification(song));

        if (wakeLock != null && !wakeLock.isHeld()) {
            wakeLock.acquire(12 * 60 * 60 * 1000L); // máximo 12 horas
            Log.d(TAG, "WakeLock adquirido");
        }

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        if (wakeLock != null && wakeLock.isHeld()) {
            wakeLock.release();
            Log.d(TAG, "WakeLock liberado");
        }
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    // ─── helpers ────────────────────────────────────────────────────────────

    private Notification buildNotification(String song) {
        Intent openIntent = new Intent(this, MainActivity.class);
        openIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        int piFlags = (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                ? (PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE)
                : PendingIntent.FLAG_UPDATE_CURRENT;
        PendingIntent pi = PendingIntent.getActivity(this, 0, openIntent, piFlags);

        return new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("🎵 AHORA SUENA")
                .setContentText(song)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(song))
                .setContentIntent(pi)
                .setOngoing(true)
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .build();
    }

    private void ensureChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel ch = new NotificationChannel(
                    CHANNEL_ID,
                    "Reproducción de radio",
                    NotificationManager.IMPORTANCE_LOW);
            ch.setDescription("Mantiene la radio sonando en segundo plano");
            NotificationManager nm = getSystemService(NotificationManager.class);
            if (nm != null) nm.createNotificationChannel(ch);
        }
    }
}

