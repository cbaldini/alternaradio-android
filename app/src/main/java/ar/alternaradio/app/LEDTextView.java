package ar.alternaradio.app;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.util.AttributeSet;
import androidx.appcompat.widget.AppCompatTextView;

/**
 * TextView personalizado con fuente monospace y efecto pixelado
 */
public class LEDTextView extends AppCompatTextView {

    public LEDTextView(Context context) {
        super(context);
        initView();
    }

    public LEDTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public LEDTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    private void initView() {
        // Configurar fuente monospace/terminal
        setTypeface(Typeface.MONOSPACE);
        // Forzar color blanco
        setTextColor(0xFFFFFFFF);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        // Aplicar efecto pixelado desactivando antialiasing
        Paint paint = getPaint();
        paint.setAntiAlias(false);

        // Llamar al dibujo normal del padre
        super.onDraw(canvas);
    }
}


