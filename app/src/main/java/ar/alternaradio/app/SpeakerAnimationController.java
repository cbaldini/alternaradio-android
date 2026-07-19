package ar.alternaradio.app;

import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.util.Log;

public class SpeakerAnimationController {
    private static final String TAG = "SpeakerAnimator";
    private View speakerView;

    public SpeakerAnimationController(View speakerView) {
        this.speakerView = speakerView;
    }

    /** Pulso suave de opacidad, sin rotación */
    public void pulseBeat() {
        if (speakerView == null) return;
        AnimationSet set = new AnimationSet(true);
        AlphaAnimation fadeOut = new AlphaAnimation(1.0f, 0.85f);
        fadeOut.setDuration(200);
        AlphaAnimation fadeIn = new AlphaAnimation(0.85f, 1.0f);
        fadeIn.setDuration(200);
        fadeIn.setStartOffset(200);
        set.addAnimation(fadeOut);
        set.addAnimation(fadeIn);
        speakerView.startAnimation(set);
    }

    /** Alias para compatibilidad: no hace nada (sin rotación) */
    public void rotateOnce() { /* sin rotación */ }
    public void openAndClose() { pulseBeat(); }
    public void vibrate() { /* sin vibración */ }
    public void complexBeatAnimation() { pulseBeat(); }
}
