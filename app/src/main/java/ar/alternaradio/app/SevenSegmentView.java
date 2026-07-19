package ar.alternaradio.app;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

/**
 * Custom View que dibuja números en estilo 7 segmentos LED, como la pantalla de la imagen de referencia.
 */
public class SevenSegmentView extends View {

    // Segmentos para cada dígito 0-9
    // Orden: [a=top, b=top-right, c=bottom-right, d=bottom, e=bottom-left, f=top-left, g=middle]
    private static final boolean[][] DIGIT_SEGS = {
        {true,  true,  true,  true,  true,  true,  false}, // 0
        {false, true,  true,  false, false, false, false}, // 1
        {true,  true,  false, true,  true,  false, true},  // 2
        {true,  true,  true,  true,  false, false, true},  // 3
        {false, true,  true,  false, false, true,  true},  // 4
        {true,  false, true,  true,  false, true,  true},  // 5
        {true,  false, true,  true,  true,  true,  true},  // 6
        {true,  true,  true,  false, false, false, false}, // 7
        {true,  true,  true,  true,  true,  true,  true},  // 8
        {true,  true,  true,  true,  false, true,  true},  // 9
    };

    private String text = "88.1";
    private final Paint onPaint  = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint offPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    // Proporciones base (se escalan al tamaño real del View)
    private static final float BASE_DIGIT_W  = 42f;
    private static final float BASE_DIGIT_H  = 72f;
    private static final float BASE_THICK    = 7f;
    private static final float BASE_GAP      = 1.5f;
    private static final float BASE_SPACING  = 6f;
    private static final float BASE_DOT_W    = 14f;

    public SevenSegmentView(Context context) {
        super(context);
        init();
    }

    public SevenSegmentView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public SevenSegmentView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        // Segmento encendido: rojo brillante con halo
        onPaint.setColor(Color.parseColor("#FF2200"));
        onPaint.setStyle(Paint.Style.FILL);
        onPaint.setShadowLayer(10f, 0f, 0f, Color.parseColor("#FF4400"));
        setLayerType(LAYER_TYPE_SOFTWARE, null); // necesario para sombras

        // Segmento apagado: rojo muy oscuro (ghost segment)
        offPaint.setColor(Color.parseColor("#2a0500"));
        offPaint.setStyle(Paint.Style.FILL);
    }

    /** Actualiza el texto a mostrar y repinta */
    public void setText(String t) {
        this.text = (t != null) ? t : "";
        invalidate();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        float scale = computeScale(MeasureSpec.getSize(heightMeasureSpec));
        float totalW = computeTotalWidth(scale);
        int w = MeasureSpec.getSize(widthMeasureSpec);
        int h = MeasureSpec.getSize(heightMeasureSpec);
        if (MeasureSpec.getMode(widthMeasureSpec) == MeasureSpec.AT_MOST) {
            w = (int) totalW + 4;
        }
        if (MeasureSpec.getMode(heightMeasureSpec) == MeasureSpec.AT_MOST) {
            h = (int) (BASE_DIGIT_H + BASE_THICK + 4);
        }
        setMeasuredDimension(w, h);
    }

    private float computeScale(int viewHeight) {
        if (viewHeight <= 0) return 1f;
        return viewHeight / (BASE_DIGIT_H + BASE_THICK + 4f);
    }

    private float computeTotalWidth(float scale) {
        if (text == null || text.isEmpty()) return 0;
        float w = 0;
        for (char c : text.toCharArray()) {
            if (c == '.') w += (BASE_DOT_W + BASE_SPACING) * scale;
            else if (Character.isDigit(c)) w += (BASE_DIGIT_W + BASE_SPACING) * scale;
        }
        return w;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (text == null || text.isEmpty()) return;

        float scale = computeScale(getHeight());
        float dw = BASE_DIGIT_W  * scale;
        float dh = BASE_DIGIT_H  * scale;
        float t  = BASE_THICK    * scale;
        float g  = BASE_GAP      * scale;
        float sp = BASE_SPACING  * scale;
        float dotW = BASE_DOT_W  * scale;

        float totalW = computeTotalWidth(scale);
        float x = (getWidth() - totalW) / 2f;
        float y = (getHeight() - dh - t) / 2f;

        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            if (c == '.') {
                // Punto decimal
                float dotX = x + dotW / 2f - t / 2f;
                float dotY = y + dh;
                RectF dot = new RectF(dotX, dotY - t, dotX + t, dotY);
                canvas.drawRoundRect(dot, t / 2f, t / 2f, onPaint);
                x += dotW + sp;
            } else if (Character.isDigit(c)) {
                drawDigit(canvas, x, y, c - '0', dw, dh, t, g);
                x += dw + sp;
            }
        }
    }

    private void drawDigit(Canvas canvas, float x, float y, int d,
                            float w, float h, float t, float g) {
        boolean[] s = DIGIT_SEGS[d];
        float half = h / 2f;

        // a - top
        drawH(canvas, x, y, w, t, g, s[0]);
        // b - top-right
        drawV(canvas, x + w - t, y, half, t, g, s[1]);
        // c - bottom-right
        drawV(canvas, x + w - t, y + half, half, t, g, s[2]);
        // d - bottom
        drawH(canvas, x, y + h - t, w, t, g, s[3]);
        // e - bottom-left
        drawV(canvas, x, y + half, half, t, g, s[4]);
        // f - top-left
        drawV(canvas, x, y, half, t, g, s[5]);
        // g - middle
        drawH(canvas, x, y + half - t / 2f, w, t, g, s[6]);
    }

    private void drawH(Canvas canvas, float x, float y, float w, float t, float g, boolean on) {
        RectF r = new RectF(x + g, y, x + w - g, y + t);
        canvas.drawRoundRect(r, t / 2f, t / 2f, on ? onPaint : offPaint);
    }

    private void drawV(Canvas canvas, float x, float y, float h, float t, float g, boolean on) {
        RectF r = new RectF(x, y + g, x + t, y + h - g);
        canvas.drawRoundRect(r, t / 2f, t / 2f, on ? onPaint : offPaint);
    }
}

