package se.kjellstrand.awp.eld;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.os.Handler;
import android.service.wallpaper.WallpaperService;
import android.util.Log;
import android.view.SurfaceHolder;

/**
 * Created by Carl-Emil Kjellstrand on 10/31/14.
 */
public class EldWallpaper extends WallpaperService {

    EldGenerator eldGenerator = null;

    int height = 400;
    int width = 400;

    @Override
    public Engine onCreateEngine() {
        eldGenerator = new EldGenerator(getApplicationContext(), width, height);
        return new EldWPEngine();
    }

    class EldWPEngine extends  Engine {

        private final String TAG = EldWPEngine.class.getCanonicalName();

        private Handler mHandler = new Handler();

        private Runnable mIteration = new Runnable() {
            public void run() {
                iteration();
                drawFrame();
            }
        };

        private boolean mVisible;
        private int frame;

        @Override
        public void onDestroy() {
            super.onDestroy();
            // stop the animation
            mHandler.removeCallbacks(mIteration);
        }

        @Override
        public void onVisibilityChanged(boolean visible) {
            mVisible = visible;
            if (visible) {
                iteration();
                drawFrame();
            } else {
                // stop the animation
                mHandler.removeCallbacks(mIteration);
            }
        }

        @Override
        public void onSurfaceChanged(SurfaceHolder holder, int format,
                                     int width, int height) {
            iteration();
            drawFrame();
        }

        @Override
        public void onSurfaceDestroyed(SurfaceHolder holder) {
            super.onSurfaceDestroyed(holder);
            mVisible = false;
            // stop the animation
            mHandler.removeCallbacks(mIteration);
        }

        @Override
        public void onOffsetsChanged(float xOffset, float yOffset,
                                     float xOffsetStep, float yOffsetStep, int xPixelOffset,
                                     int yPixelOffset) {
            iteration();
            drawFrame();
        }

        protected void drawFrame() {
            SurfaceHolder holder = getSurfaceHolder();

            Canvas c = null;
            try {
                c = holder.lockCanvas();
                if (c != null) {
                    draw(c);
                }
            } finally {
                if (c != null)
                    holder.unlockCanvasAndPost(c);
            }
        }

        private void draw(Canvas c) {
            Bitmap bitmap = eldGenerator.getBitmapForFrame(frame++);
            Paint paint = new Paint();
            paint.setColor(0xffffffff);

            int xd = 300;
            int yd = 300;
            Matrix matrix = new Matrix();
            matrix.reset();
            // TODO find min size and save for use here.
            matrix.setScale(1000/width, 1000/height);
			c.drawBitmap(bitmap, matrix, paint);
        }

        protected void iteration() {
            // Reschedule the next redraw in 40ms
            mHandler.removeCallbacks(mIteration);
            if (mVisible) {
            	// TODO make constant?
                mHandler.postDelayed(mIteration, 1000 / 60);
            }
        }
    }
}
