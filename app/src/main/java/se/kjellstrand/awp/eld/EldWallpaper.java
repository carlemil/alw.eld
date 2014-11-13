package se.kjellstrand.awp.eld;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.os.Handler;
import android.service.wallpaper.WallpaperService;
import android.view.SurfaceHolder;

/**
 * Created by Carl-Emil Kjellstrand on 10/31/14.
 */
public class EldWallpaper extends WallpaperService {

	@Override
	public Engine onCreateEngine() {

		return new EldWPEngine();
	}

	class EldWPEngine extends Engine {

		private static final int FPS = 60;

		private EldGenerator eldGenerator = null;
		private Handler handler = new Handler();
		private boolean visible;
		private int frame = (int) System.currentTimeMillis()/1000;
		private float scale = 4.0f;

		private Runnable iteration = new Runnable() {
			public void run() {
				iteration();
				drawFrame();
			}
		};

		@Override
		public void onDestroy() {
			super.onDestroy();
			// stop the animation
			handler.removeCallbacks(iteration);
		}

		@Override
		public void onVisibilityChanged(boolean visible) {
			this.visible = visible;
			if (visible) {
				iteration();
				drawFrame();
			} else {
				// stop the animation
				handler.removeCallbacks(iteration);
			}
		}

		@Override
		public void onSurfaceChanged(SurfaceHolder holder, int format,
				int width, int height) {
			eldGenerator = new EldGenerator(getApplicationContext(),
					(int) (width / scale), (int) (height / scale));
			iteration();
			drawFrame();
		}

		@Override
		public void onSurfaceDestroyed(SurfaceHolder holder) {
			super.onSurfaceDestroyed(holder);
			visible = false;
			// stop the animation
			handler.removeCallbacks(iteration);
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

			Matrix matrix = new Matrix();
			matrix.reset();
			matrix.setScale(scale, scale);
			c.drawBitmap(bitmap, matrix, paint);
		}

		protected void iteration() {
			handler.removeCallbacks(iteration);
			if (visible) {
				handler.postDelayed(iteration, 1000 / FPS);
			}
		}
	}
}
