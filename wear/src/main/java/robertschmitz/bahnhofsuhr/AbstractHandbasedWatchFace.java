package robertschmitz.bahnhofsuhr;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.text.format.Time;

abstract public class AbstractHandbasedWatchFace extends AbstractWatchFace implements WatchFaceRenderer {

    private final static float TWO_PI = (float) Math.PI * 2;

    protected boolean mAmbientMode = false;

    protected Resources resources;
    public static final long MILLIS_IN_MIN = 60 * 1000;

    private HandAnimator mSecHandAnimator = getSecHandAnimator();
    private HandAnimator mMinHandAnimator = getMinHandAnimator();
    private HandAnimator mHourHandAnimator = getHourHandAnimator();

    private Matrix mMatrix;
    private float normedEdge = getNormedEdge();
    private float actualEdge = normedEdge;

    @Override
    public void onCreate(Resources resources) {
        this.resources = resources;
    }

    abstract protected void drawMarks(Canvas canvas, Rect bounds);

    @Override
    public void renderWatchFace(Time time, Canvas canvas, Rect bounds, boolean ambientMode) {

        mAmbientMode = ambientMode;

        int width = bounds.width();
        int height = bounds.height();

        float centerX = width / 2f;
        float centerY = height / 2f;

        if (ambientMode) {
            resetAnimators();
        }

        drawBackground(canvas, bounds);

        updateMatrix(canvas, bounds);

        canvas.save();
        canvas.translate(centerX, centerY);
        drawMarks(canvas, bounds);
        canvas.restore();

        float secRot = getSecRotation(time);
        float minRot = getMinRotation(time);
        float hrRot = getHourRotation(time);

        if (mSecHandAnimator != null) {
            mSecHandAnimator.setCurrentAngle(secRot);
            secRot = mSecHandAnimator.getAnimatedAngle();
        }

        if (mMinHandAnimator != null) {
            mMinHandAnimator.setCurrentAngle(minRot);
            minRot = mMinHandAnimator.getAnimatedAngle();
        }

        if (mHourHandAnimator != null) {
            mHourHandAnimator.setCurrentAngle(hrRot);
            hrRot = mHourHandAnimator.getAnimatedAngle();
        }

        canvas.save();
        canvas.translate(centerX, centerY);
        canvas.rotate(minRot / TWO_PI * 360);
        drawHandMin(canvas);
        canvas.restore();

        canvas.save();
        canvas.translate(centerX, centerY);
        canvas.rotate(hrRot / TWO_PI * 360);
        drawHandHour(canvas);
        canvas.restore();

        if (!ambientMode) {
            canvas.save();
            canvas.translate(centerX, centerY);
            canvas.rotate(secRot / TWO_PI * 360);
            drawHandSec(canvas);
            canvas.restore();
        }


        canvas.save();
        canvas.translate(centerX, centerY);
        drawCenter(canvas);
        canvas.restore();
    }

    protected float getMinRotation(Time time) {
        return time.minute / 30f * (float) Math.PI;
    }

    protected float getHourRotation(Time time) {
        return (((time.hour + (time.minute / 60f)) / 6f) * (float) Math.PI);
    }

    protected float getSecRotation(Time time) {
        return time.second / 30f * (float) Math.PI;
    }

    private void resetAnimators() {
        if (mSecHandAnimator != null) {
            mSecHandAnimator.reset();
        }
        if (mMinHandAnimator != null) {
            mMinHandAnimator.reset();
        }
        if (mHourHandAnimator != null) {
            mHourHandAnimator.reset();
        }
    }

    private void updateMatrix(Canvas canvas, Rect bounds) {
        actualEdge = bounds.width() > bounds.height() ? bounds.height() : bounds.width();

        if (mMatrix == null || actualEdge != normedEdge) {
            int width = bounds.width();
            int height = bounds.height();

            float centerX = width / 2f;
            float centerY = height / 2f;

            float scale = actualEdge / normedEdge;

            mMatrix = new Matrix();

            if (actualEdge != normedEdge) {
                mMatrix.postTranslate(-centerX, -centerY);
                mMatrix.postScale(scale, scale);
                mMatrix.postTranslate(centerX, centerY);
            }
            canvas.setMatrix(mMatrix);
        }
    }


    private float getNormedEdge() {
        return 320;
    }

    protected HandAnimator getSecHandAnimator() {
        return null;
    }

    protected HandAnimator getMinHandAnimator() {
        return null;
    }

    protected HandAnimator getHourHandAnimator() {
        return null;
    }

    abstract protected void drawBackground(Canvas canvas, Rect bounds);
    abstract protected void drawHandHour(Canvas canvas);
    abstract protected void drawHandMin(Canvas canvas);
    abstract protected void drawHandSec(Canvas canvas);
    abstract protected void drawCenter(Canvas canvas);

    @Override
    protected int getInteractiveFPS() {
        return 2;
    }

    @Override
    protected WatchFaceRenderer getWatchFaceRendererInstance() {
        return this;
    }

    public void onAmbientModeChanged(boolean ambientMode, boolean lowBitAmbient) {
        mAmbientMode = ambientMode;

        if (ambientMode) {
            resetAnimators();
        }
    }

    protected interface Function {
        float apply(float t);
    }

    protected class HandAnimator {
        private float mOldAngle;
        private float mNewAngle;

        private boolean mInitialized = false;
        private boolean mInterpolating = false;
        private long mInterpolationTimeStarted = 0;

        private final long mInterpolationTimeLength;
        private final Function f;

        public HandAnimator(Function f, long interpolationTimeLength) {
            this.f = f;
            this.mInterpolationTimeLength = interpolationTimeLength;
        }

        private void reset() {
            mInitialized = false;
        }

        private void setCurrentAngle(float angle) {
            if (!mInitialized) {
                mOldAngle = angle;
                mInitialized = true;
                mInterpolating = false;
            } else if (!mInterpolating && angle != mOldAngle) {
                while (angle < mOldAngle) {
                    mOldAngle -= Math.PI * 2;
                }
                mNewAngle = angle;
                mInterpolationTimeStarted = System.currentTimeMillis();
                mInterpolating = true;
            }
        }

        private float getAnimatedAngle() {
            if (!mInterpolating) {
                return mOldAngle;
            }
            float t = (System.currentTimeMillis() - mInterpolationTimeStarted) / (float) mInterpolationTimeLength;
            if (t >= 1) {
                mInterpolating = false;
                mOldAngle = mNewAngle;
                return mOldAngle;
            }
            t = (f.apply(t));
            return (mOldAngle * (1 - t) + mNewAngle * t);
        }
    }
}
