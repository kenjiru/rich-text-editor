package ro.kenjiru.ui.widgets.richtexteditor.spans;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.text.Layout;
import android.text.Spanned;

public class BulletSpan extends android.text.style.BulletSpan {
    private static final int GRAPHIC_SIZE = 14;
    private BulletType bulletType = BulletType.TRIANGLE;

    private Paint mPaintBorder;
    private int mIndent = 1;

    public enum BulletType {FULL_CIRCLE, EMPTY_CIRCLE, TRIANGLE}

    public BulletSpan(int gapWidth) {
        super(gapWidth);

        mPaintBorder = new Paint();
        mPaintBorder.setStyle(Paint.Style.STROKE);
        mPaintBorder.setAntiAlias(true);
        mPaintBorder.setColor(Color.RED);
    }

    public BulletSpan(int gapWidth, BulletType bulletType) {
        this(gapWidth);

        this.bulletType = bulletType;
    }

    public void indent() {
        ++mIndent;
    }

    public void outdent() {
        if (mIndent > 1) {
            --mIndent;
        }
    }

    @Override
    public int getLeadingMargin(boolean first) {
        return super.getLeadingMargin(first) * mIndent;
    }

    @Override
    public void drawLeadingMargin(Canvas c, Paint p, int x, int dir,
                                  int top, int baseline, int bottom,
                                  CharSequence text, int start, int end,
                                  boolean first, Layout l) {
        c.drawRect(x, top, x + l.getWidth(), bottom, mPaintBorder);

        if (((Spanned) text).getSpanStart(this) == start) {
            x += super.getLeadingMargin(true) * (mIndent - 1);

            if (bulletType == BulletType.TRIANGLE) {
                drawTriangle(c, p, x + 10, dir, top, bottom);
            } else {
                drawCircle(c, p, x + 10, dir, top, bottom, bulletType == BulletType.EMPTY_CIRCLE);
            }
        }
    }

    private void drawCircle(Canvas canvas, Paint paint, int x, int dir, int top, int bottom, boolean emptyCircle) {
        Paint.Style style = paint.getStyle();

        if (emptyCircle) {
            // Only for empty circles, we need to turn off antialias
            // otherwise the circles will have different sizes.
            paint.setAntiAlias(false);
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(0);
        } else {
            paint.setStyle(Paint.Style.FILL);
        }

        canvas.drawCircle(x + dir * GRAPHIC_SIZE / 2,
                (top + bottom) / 2.0f,
                GRAPHIC_SIZE / 2,
                paint);

        paint.setAntiAlias(true);
        paint.setStyle(style);
    }

    private void drawTriangle(Canvas canvas, Paint paint, int x, int dir, int top, int bottom) {
        Paint.Style style = paint.getStyle();

        paint.setStrokeWidth(2);
        paint.setStyle(Paint.Style.FILL);

        float y = (top + bottom) / 2.0f - GRAPHIC_SIZE / 2;

        Path path = new Path();
        path.setFillType(Path.FillType.EVEN_ODD);
        path.moveTo(x, y);
        path.lineTo(x + 0.87f * GRAPHIC_SIZE, y + GRAPHIC_SIZE/2);
        path.lineTo(x, y + GRAPHIC_SIZE);
        path.lineTo(x, y);
        path.close();

        canvas.drawPath(path, paint);

        paint.setStyle(style);
    }
}
