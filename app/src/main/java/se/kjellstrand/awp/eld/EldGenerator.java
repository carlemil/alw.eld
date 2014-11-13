package se.kjellstrand.awp.eld;

import android.content.Context;
import android.graphics.Bitmap;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.util.Log;

/**
 * Created by Carl-Emil Kjellstrand on 11/3/14.
 */
public class EldGenerator {

	private static final String TAG = EldGenerator.class.getCanonicalName();
	private Bitmap bitmap;
	private Allocation allocationSeed;
	private Allocation allocationEldad;
	private Allocation allocationColorized;

	public int width;
	public int height;

	private ScriptC_colorize coloriseScript;

	private ScriptC_elda eldaScript;

	private RenderScript rs;

	private SirpinskyGenerator spg;

	private int paletteSize = 200;

	public EldGenerator(Context context, int width, int height) {
		this.width = width;
		this.height = height;

		rs = RenderScript.create(context, RenderScript.ContextType.DEBUG);
		rs.setPriority(RenderScript.Priority.LOW);

		setupAllocations(width, height);

		setupColorizeScript();

		setupEldaScript(width, height);

		bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);

		int[] colors = new int[] { 0xff000000, 0xffff0000, 0xffffff00,
				0xffffffff };
		setupPalette(context, colors);

		spg = new SirpinskyGenerator(250);
	}

	public Bitmap getBitmapForFrame(int frame) {
		long t = System.currentTimeMillis();
		renderSirpinskySeed(frame);
		rs.finish();
		Log.d(TAG, "seedEldAsLine: " + (System.currentTimeMillis() - t));

		t = System.currentTimeMillis();
		renderEld();
		rs.finish();
		Log.d(TAG, "renderEld: " + (System.currentTimeMillis() - t));

		t = System.currentTimeMillis();
		renderColors();
		rs.finish();
		Log.d(TAG, "renderColors: " + (System.currentTimeMillis() - t));

		t = System.currentTimeMillis();
		copyToBitmap();
		rs.finish();
		Log.d(TAG, "renderToBitmap: " + (System.currentTimeMillis() - t));

		return bitmap;
	}

	private void renderSirpinskySeed(int frame) {
		int[] eldValues = new int[width * height];
		allocationEldad.copyTo(eldValues);
		
		int bufferSize = 10;
		
		int x1 = getSeed(frame, 45f, bufferSize);
		int x2 = getSeed(frame, 66f, bufferSize);
		int x3 = getSeed(frame, 73f, bufferSize);
		int y1 = getSeed(frame, 55f, bufferSize);
		int y2 = getSeed(frame, 76f, bufferSize);
		int y3 = getSeed(frame, 47f, bufferSize);
		
		int[] spgValues = spg.getSirpinsky(x1, y1, x2, y2, x3, y3);
		for (int i = 0; i < spg.getSize(); i++) {
			int x = spgValues[i * 2];
			int y = spgValues[i * 2 + 1];
			eldValues[(y * width) + x] = paletteSize;
		}
		allocationSeed.copyFrom(eldValues);
	}

	private int getSeed(int frame, float frequency, int bufferSize) {
		return (int) ((Math.sin(frame / frequency) + 1) / 2f * (width - bufferSize*2) + bufferSize);
	}

	private void renderEld() {
		eldaScript.set_inAllocation(allocationSeed);
		eldaScript.forEach_root(allocationSeed, allocationEldad);
	}

	private void renderColors() {
		coloriseScript.forEach_root(allocationEldad, allocationColorized);
	}

	private void copyToBitmap() {
		int[] bitmapValues = new int[width * height];
		allocationColorized.copyTo(bitmapValues);
		bitmap.setPixels(bitmapValues, 0, width, 0, 0, width, height);
	}

	private void setupAllocations(int width, int height) {
		allocationSeed = Allocation.createSized(rs, Element.I32(rs), width
				* height);
		allocationEldad = Allocation.createSized(rs, Element.I32(rs), width
				* height);
		allocationColorized = Allocation.createSized(rs, Element.I32(rs), width
				* height);
	}

	private void setupColorizeScript() {
		coloriseScript = new ScriptC_colorize(rs);
	}

	private void setupEldaScript(int width, int height) {
		eldaScript = new ScriptC_elda(rs);
		eldaScript.set_width(width);
		eldaScript.set_height(height);
		eldaScript.set_fadeOutSpeed(4);
	}

	private void setupPalette(Context context, int[] colors) {
		int[] d = Palette.getPalette(context, colors, paletteSize);

		Element type = Element.I32(rs);
		Allocation colorAllocation = Allocation.createSized(rs, type,
				paletteSize);
		coloriseScript.bind_color(colorAllocation);

		colorAllocation.copy1DRangeFrom(0, paletteSize, d);
	}
}
