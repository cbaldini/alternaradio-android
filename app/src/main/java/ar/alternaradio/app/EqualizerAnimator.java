package ar.alternaradio.app;

import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.view.View;
import android.view.ViewGroup;
import android.util.Log;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class EqualizerAnimator {
    private static final String TAG = "EqualizerAnimator";
    private final List<View> eqBars;
    private final Random random = new Random();
    private AnimatorSet animatorSet;
    private boolean isAnimating = false;

    public EqualizerAnimator(List<View> bars) {
        this.eqBars = new ArrayList<>(bars);
        Log.d(TAG, "EqualizerAnimator creado con " + bars.size() + " barras");
    }

    public void startAnimation() {
        if (isAnimating) return;
        isAnimating = true;

        Log.d(TAG, "Iniciando animación de ecualizador");

        // Cancelar animación anterior si existe
        if (animatorSet != null) {
            animatorSet.cancel();
        }

        animatorSet = new AnimatorSet();
        List<ValueAnimator> animators = new ArrayList<>();

        // Animar cada barra con altura aleatoria
        for (View bar : eqBars) {
            ViewGroup.LayoutParams params = bar.getLayoutParams();
            int initialHeight = params.height > 0 ? params.height : 10;

            // Altura aleatoria entre 30% y 200% de la altura original
            int minHeight = Math.max(2, (int) (initialHeight * 0.3f));
            int maxHeight = (int) (initialHeight * 2.0f);
            int randomHeight = minHeight + random.nextInt(Math.max(1, maxHeight - minHeight));

            ValueAnimator animator = ValueAnimator.ofInt(initialHeight, randomHeight, initialHeight);
            animator.setDuration(250 + random.nextInt(200)); // Entre 250-450ms (3x más rápido)
            animator.addUpdateListener(animation -> {
                int value = (int) animation.getAnimatedValue();
                ViewGroup.LayoutParams lp = bar.getLayoutParams();
                lp.height = value;
                bar.setLayoutParams(lp);
            });
            animators.add(animator);
        }

        animatorSet.playTogether(animators.toArray(new ValueAnimator[0]));

        // Repetir animación cuando termine
        animatorSet.addListener(new android.animation.AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(android.animation.Animator animation) {
                isAnimating = false; // Reset para que startAnimation() pueda continuar
                startAnimation();   // Siempre reiniciar en loop
            }
        });

        animatorSet.start();
        Log.d(TAG, "AnimatorSet iniciado con " + animators.size() + " animadores");
    }

    public void stopAnimation() {
        Log.d(TAG, "Deteniendo animación");
        if (animatorSet != null) {
            animatorSet.cancel();
        }
        isAnimating = false;
    }

    public boolean isAnimating() {
        return isAnimating;
    }
}
