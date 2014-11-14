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
	
	private int[] sirpinskyPoints;
	
	private int[] bitmapValues;

	private int paletteSize = 512;
	
	private int nbrOfSirpinskyPoints = 300;
	
	private int eldFadeOutSpeed = 8;

	public EldGenerator(Context context, int width, int height) {
		this.width = width;
		this.height = height;

		rs = RenderScript.create(context, RenderScript.ContextType.DEBUG);
		rs.setPriority(RenderScript.Priority.LOW);

		setupAllocations(width, height);

		setupColorizeScript();

		setupEldaScript(width, height);

		bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
		bitmapValues = new int[width * height];
		
		int[] colors = new int[] { 0xff000000, 0xffff0000, 0xffffff00,
				0xffffffff };
		setupPalette(context, colors);

		spg = new SirpinskyGenerator(nbrOfSirpinskyPoints , width, height);
		sirpinskyPoints = new int[width * height];
	}

	public Bitmap getBitmapForFrame(int frame) {
//		long t = System.currentTimeMillis();
		renderSirpinsky(frame);
//		rs.finish();
//		Log.d(TAG, "seedEldAsLine: " + (System.currentTimeMillis() - t));
//
//		t = System.currentTimeMillis();
		renderEld();
//		rs.finish();
//		Log.d(TAG, "renderEld: " + (System.currentTimeMillis() - t));
//
//		t = System.currentTimeMillis();
		renderColors();
//		rs.finish();
//		Log.d(TAG, "renderColors: " + (System.currentTimeMillis() - t));
//
//		t = System.currentTimeMillis();
		copyToBitmap();
//		rs.finish();
//		Log.d(TAG, "renderToBitmap: " + (System.currentTimeMillis() - t));
		return bitmap;
	}

	private void renderSirpinsky(int frame) {
		allocationEldad.copyTo(sirpinskyPoints);
		spg.renderSirpinsky(frame, paletteSize, sirpinskyPoints);
		allocationSeed.copyFrom(sirpinskyPoints);
	}

	private void renderEld() {
		eldaScript.set_inAllocation(allocationSeed);
		eldaScript.forEach_root(allocationSeed, allocationEldad);
	}

	private void renderColors() {
		coloriseScript.forEach_root(allocationEldad, allocationColorized);
	}

	private void copyToBitmap() {
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
		eldaScript.set_fadeOutSpeed(eldFadeOutSpeed );
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
