 package ar.alternaradio.app;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RadialGradient;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.view.View;

/**
 * Vista de knob (perilla) que dibuja un botón circular con un indicador rojo estático.
 * NO rota su fondo — sólo cambia la posición del indicador.
 */
public class KnobView extends View {

    private float indicatorAngle = -135f; // grados desde arriba, sentido horario

    private final Paint knobPaint      = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint borderPaint    = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint innerRingPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint indicatorPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint shadowPaint    = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint centerPaint    = new Paint(Paint.ANTI_ALIAS_FLAG);

    public KnobView(Context context) {
        super(context);
        init();
    }

    public KnobView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public KnobView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        borderPaint.setColor(Color.parseColor("#606060"));
        borderPaint.setStyle(Paint.Style.STROKE);
        borderPaint.setStrokeWidth(2f);

        innerRingPaint.setColor(Color.parseColor("#2a2a2a"));
        innerRingPaint.setStyle(Paint.Style.STROKE);
        innerRingPaint.setStrokeWidth(1f);

        indicatorPaint.setColor(Color.parseColor("#CC0000"));
        indicatorPaint.setStyle(Paint.Style.STROKE);
        indicatorPaint.setStrokeCap(Paint.Cap.ROUND);
        indicatorPaint.setStrokeWidth(5f);

        shadowPaint.setColor(Color.parseColor("#BB000000"));
        shadowPaint.setStyle(Paint.Style.FILL);

        centerPaint.setColor(Color.parseColor("#3a3a3a"));
        centerPaint.setStyle(Paint.Style.FILL);
    }

    /** Ángulo en grados (0 = arriba, sentido horario). -135 = posición "mínimo". */
    public void setIndicatorAngle(float angleDeg) {
        this.indicatorAngle = angleDeg;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        float w = getWidth();
        float h = getHeight();
        float cx = w / 2f;
        float cy = h / 2f;
        float radius = Math.min(cx, cy) - 5f;
        if (radius <= 0) return;

        // Sombra exterior
        canvas.drawCircle(cx + 3f, cy + 4f, radius, shadowPaint);

        // Fondo del knob con gradiente radial (cuerpo oscuro)
        RadialGradient gradient = new RadialGradient(
            cx - radius * 0.25f, cy - radius * 0.25f,
            radius * 1.3f,
            new int[]{
                Color.parseColor("#4e4e4e"),
                Color.parseColor("#2a2a2a"),
                Color.parseColor("#111111")
            },
            new float[]{0f, 0.45f, 1f},
            Shader.TileMode.CLAMP
        );
        knobPaint.setShader(gradient);
        canvas.drawCircle(cx, cy, radius, knobPaint);

        // Borde exterior plateado
        canvas.drawCircle(cx, cy, radius, borderPaint);

        // Aro interior sutil
        canvas.drawCircle(cx, cy, radius * 0.80f, innerRingPaint);

        // Indicador rojo — línea desde ~55% hasta ~78% del radio
        float angleRad = (float) Math.toRadians(indicatorAngle - 90f); // -90 porque 0° = arriba
        float innerR = radius * 0.40f;
        float outerR = radius * 0.76f;
        float ix1 = cx + innerR * (float) Math.cos(angleRad);
        float iy1 = cy + innerR * (float) Math.sin(angleRad);
        float ix2 = cx + outerR * (float) Math.cos(angleRad);
        float iy2 = cy + outerR * (float) Math.sin(angleRad);
        canvas.drawLine(ix1, iy1, ix2, iy2, indicatorPaint);

        // Punto central
        canvas.drawCircle(cx, cy, radius * 0.10f, centerPaint);
    }
}

