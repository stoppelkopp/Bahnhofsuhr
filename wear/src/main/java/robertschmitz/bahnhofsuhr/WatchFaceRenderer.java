package robertschmitz.bahnhofsuhr;

import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.text.format.Time;

public interface WatchFaceRenderer {

    void onCreate(Resources resources);
    void renderWatchFace(Time time, Canvas canvas, Rect bounds, boolean ambientMode);
    void onAmbientModeChanged(boolean ambientMode, boolean lowBitAmbient);
}
