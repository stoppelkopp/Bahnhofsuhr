package robertschmitz.bahnhofsuhr;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.text.format.Time;

public class BahnhofsuhrWatchFaceSwiss extends BahnhofsuhrWatchFace {

    protected Paint mHandSecNoShadowPaint;

    @Override
    public void onCreate(Resources resources) {
        super.onCreate(resources);

        mHandSecCirclePaint.setStyle(Paint.Style.FILL_AND_STROKE);

        mHandMinPaint.setStrokeWidth(8f);
        mHandHourPaint.setStrokeWidth(10f);

        mHandSecPaint.setStrokeCap(Paint.Cap.SQUARE);
        mHandSecPaint.setStrokeWidth(6);
        mHandSecPaint.setStyle(Paint.Style.STROKE);

        mHandSecNoShadowPaint = new Paint(mHandSecPaint);
        mHandSecNoShadowPaint.setShadowLayer(0, 0, 0, resources.getColor(R.color.shadow));
    }

    protected void drawHandHour(Canvas canvas) {
        canvas.drawLine(0, 20, 0, -107, getHandHourPaint());
    }


    protected void drawHandMin(Canvas canvas) {
        canvas.drawLine(0, 20, 0, -145, getHandMinPaint());
    }


    protected void drawHandSec(Canvas canvas) {
        canvas.drawLine(0, 40, 0, -80, mHandSecPaint);;
        canvas.drawCircle(0, -95, 15, mHandSecCirclePaint);
        canvas.drawLine(0, -75, 0, -85, mHandSecNoShadowPaint); //overdraw circle shadow
    }

    public void onAmbientModeChanged(boolean ambientMode, boolean lowBitAmbient) {
        super.onAmbientModeChanged(ambientMode, lowBitAmbient);
    }

}
