package robertschmitz.bahnhofsuhr;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.text.format.Time;

public class BahnhofsuhrWatchFace extends AbstractHandbasedWatchFace implements WatchFaceRenderer {

    private final static float TWO_PI = (float) Math.PI * 2;

    protected Paint mBackgroundPaint;

    protected Paint mHandMinPaint;
    protected Paint mHandMinAmbientPaint;

    protected Paint mHandHourPaint;
    protected Paint mHandHourAmbientPaint;

    protected Paint mHandSecPaint;
    protected Paint mHandSecCirclePaint;

    protected Paint mMarkBoldPaint;
    protected Paint mMarkBoldAmbientPaint;

    protected Paint mMarkThinPaint;

    protected Paint mCenterFillPaint;
    protected Paint mCenterStrokePaint;
    protected Paint mCenterAmbientPaint;

    protected Bitmap mBgImgOrig;
    protected Bitmap mBgImgScaled;

    private long mMillisInMinLess2 = (60 - 2) * 1000;

    private Path mPathSec = new Path();

    @Override
    public void onCreate(Resources resources) {
        super.onCreate(resources);

        mBgImgOrig = BitmapFactory.decodeResource(resources, R.drawable.gradient);

        mCenterFillPaint = new Paint();
        mCenterFillPaint.setColor(resources.getColor(R.color.hand_red));
        mCenterFillPaint.setAntiAlias(true);
        mCenterFillPaint.setStyle(Paint.Style.FILL_AND_STROKE);

        mCenterStrokePaint = new Paint();
        mCenterStrokePaint.setAntiAlias(true);
        mCenterStrokePaint.setColor(resources.getColor(R.color.hand_red));
        mCenterStrokePaint.setStyle(Paint.Style.STROKE);

        mBackgroundPaint = new Paint();
        mBackgroundPaint.setColor(resources.getColor(R.color.background_invert));

        mHandMinPaint = new Paint();
        mHandMinPaint.setColor(resources.getColor(R.color.hand_dark));
        mHandMinPaint.setStrokeWidth(6.5f);
        mHandMinPaint.setAntiAlias(true);
        mHandMinPaint.setStrokeCap(Paint.Cap.SQUARE);
        mHandMinPaint.setShadowLayer(3, 0, 0, resources.getColor(R.color.shadow));

        mHandMinAmbientPaint = new Paint(mHandMinPaint);
        mHandMinAmbientPaint.setColor(resources.getColor(R.color.hands_invert));
        //mHandMinAmbientPaint.setStrokeWidth(4);

        mCenterAmbientPaint = new Paint(mHandMinPaint);
        mCenterAmbientPaint.setColor(resources.getColor(R.color.hands_circle_invert));

        mHandHourPaint = new Paint(mHandMinPaint);
        mHandHourPaint.setStrokeWidth(8.5f);

        mHandHourAmbientPaint = new Paint(mHandHourPaint);
        mHandHourAmbientPaint.setColor(resources.getColor(R.color.hands_invert));
        //mHandHourAmbientPaint.setStrokeWidth(5);

        mHandSecPaint = new Paint();
        mHandSecPaint.setColor(resources.getColor(R.color.hand_red));
        mHandSecPaint.setAntiAlias(true);
        mHandSecPaint.setStrokeCap(Paint.Cap.ROUND);
        mHandSecPaint.setStyle(Paint.Style.FILL);
        mHandSecPaint.setShadowLayer(3, 0, 0, resources.getColor(R.color.shadow));

        mHandSecCirclePaint = new Paint(mHandSecPaint);
        mHandSecCirclePaint.setStyle(Paint.Style.STROKE);
        mHandSecCirclePaint.setStrokeWidth(2.5f);

        mMarkBoldPaint = new Paint();
        mMarkBoldPaint.setColor(resources.getColor(R.color.marks));
        mMarkBoldPaint.setStrokeWidth(10);
        mMarkBoldPaint.setAntiAlias(true);
        mMarkBoldPaint.setStrokeCap(Paint.Cap.SQUARE);

        mMarkBoldAmbientPaint = new Paint(mMarkBoldPaint);
        mMarkBoldAmbientPaint.setColor(resources.getColor(R.color.marks_invert));
        mMarkBoldAmbientPaint.setStrokeWidth(2);

        mMarkThinPaint = new Paint(mMarkBoldPaint);
        mMarkThinPaint.setStrokeWidth(4);

    }

    @Override
    protected float getSecRotation(Time time) {
        long millisInMinPassed = System.currentTimeMillis() % MILLIS_IN_MIN;
        if (millisInMinPassed > mMillisInMinLess2) {
            return 0;
        } else {
            return (millisInMinPassed / (float) mMillisInMinLess2) * 2 * (float) Math.PI;
        }
    }

    @Override
    protected void drawMarks(Canvas canvas, Rect bounds) {

        int width = bounds.width();
        int height = bounds.height();

        for(int i=0; i<60; i++) {
            float deg = (i/60f) * TWO_PI;
            float vx = (float) Math.sin(deg);
            float vy = (float) -Math.cos(deg);

            float stop = 145;

            if (i % 5 == 0) {
                float start = 120;
                canvas.drawLine(vx * start, vy * start, vx * stop, vy * stop, getMarkBoldPaint());
            } else if (!mAmbientMode){
                float start = 140;
                canvas.drawLine(vx * start, vy * start, vx * stop, vy * stop, mMarkThinPaint);
            }

        }
    }


    @Override
    protected void drawBackground(Canvas canvas, Rect bounds) {
        if (mAmbientMode) {
            canvas.drawRect(0, 0, bounds.width(), bounds.height(), mBackgroundPaint);
        } else {
            if (mBgImgScaled == null || mBgImgScaled.getWidth() != bounds.width() || mBgImgScaled.getHeight() != bounds.height()) {
                if (mBgImgScaled != null) {
                    mBgImgScaled.recycle();
                }
                mBgImgScaled = Bitmap.createScaledBitmap(mBgImgOrig, bounds.width(), bounds.height(), true);
            }
            canvas.drawBitmap(mBgImgScaled, 0, 0, mBackgroundPaint);
        }
    }

    protected void drawHandHour(Canvas canvas) {
        canvas.drawLine(0, 30, 0, -100, getHandHourPaint());
    }

    protected void drawHandMin(Canvas canvas) {
        canvas.drawLine(0, 30, 0, -130, getHandMinPaint());
        canvas.drawCircle(0, 0, 9, getCenterFillPaint());
    }

    protected void drawHandSec(Canvas canvas) {
        mPathSec.reset();
        mPathSec.moveTo(-3, 40);
        mPathSec.lineTo(3, 40);
        mPathSec.lineTo(1.5f, -80);
        mPathSec.lineTo(-1.5f, -80);
        mPathSec.lineTo(-2, 40);
        mPathSec.moveTo(-1.5f, -110);
        mPathSec.lineTo(-0.5f, -135);
        mPathSec.lineTo(0.5f, -135);
        mPathSec.lineTo(1.5f, -110);

        canvas.drawPath(mPathSec, mHandSecPaint);
        canvas.drawCircle(0, -95, 15, mHandSecCirclePaint);
    }

    @Override
    protected void drawCenter(Canvas canvas) {
        canvas.drawCircle(0, 0, 7, getCenterFillPaint());
        canvas.drawCircle(0, 0, 2, getHandMinPaint());
    }

    @Override
    protected AbstractHandbasedWatchFace.HandAnimator getHourHandAnimator() {
        return new HandAnimator(new TickFunction(), 2000);
    }

    @Override
    protected HandAnimator getMinHandAnimator() {
        return new HandAnimator(new TickFunction(), 2000);
    }

    private class TickFunction implements Function {

        private final float mJumpTime = 0.125f;

        @Override
        public float apply(float t) {
            float t2 = t > mJumpTime ? 1 : (1 / mJumpTime) * t;
            if (t > 0.1) {
                float invT = 1 - t;
                float z = invT * invT * (float) Math.cos((10 * invT + 20 * t) * t * Math.PI * 4) * 0.125f;
                return t2 + z;
            }
            return t2;
        }
    }

    @Override
    protected int getInteractiveFPS() {
        return 30;
    }

    @Override
    protected WatchFaceRenderer getWatchFaceRendererInstance() {
        return this;
    }

    public void onAmbientModeChanged(boolean ambientMode, boolean lowBitAmbient) {
        super.onAmbientModeChanged(ambientMode, lowBitAmbient);

        boolean aa = !lowBitAmbient;
        mCenterAmbientPaint.setAntiAlias(aa);
        mHandHourAmbientPaint.setAntiAlias(aa);
        mHandMinAmbientPaint.setAntiAlias(aa);
    }

    protected Paint getHandHourPaint() {
        return mAmbientMode ? mHandHourAmbientPaint : mHandHourPaint;
    }

    protected Paint getHandMinPaint() {
        return mAmbientMode ? mHandMinAmbientPaint : mHandMinPaint;
    }

    private Paint getCenterFillPaint() {
        return mAmbientMode ? mCenterAmbientPaint : mCenterFillPaint;
    }

    private Paint getMarkBoldPaint() {
        return mAmbientMode ? mMarkBoldAmbientPaint : mMarkBoldPaint;
    }
}
