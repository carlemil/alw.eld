package se.kjellstrand.awp.eld;

import android.content.Context;
import android.graphics.Bitmap;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.Type;
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

	private int width;
	private int height;

	private ScriptC_colorize coloriseScript;

	private ScriptC_elda eldaScript;
	
	private RenderScript rs;
	
	private SirpinskyGenerator spg;

	public EldGenerator(Context context, int width, int height) {
		this.width = width;
		this.height = height;

		rs = RenderScript.create(context,
				RenderScript.ContextType.DEBUG);
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


	public Bitmap getEldadBitmap(int frame) {
		long t = System.currentTimeMillis();
		seedEldAsLine(frame);
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
		renderToBitmap();
		rs.finish();
		Log.d(TAG, "renderToBitmap: " + (System.currentTimeMillis() - t));

		// swapAllocations();
		return bitmap;
	}

	private void seedEldAsLine(int frame) {
		int[] eldValues = new int[width * height];
		allocationEldad.copyTo(eldValues);
		int[] spgValues = spg.getSirpinsky(0, 0, 200, 0, 0, 100);
		for (int i = 0; i < spg.getSize(); i++) {
			int x= spgValues[i*2];
			int y= spgValues[i*2+1];
				eldValues[(y * width) + x] = 200;
		}
		allocationSeed.copyFrom(eldValues);
	}

	private void renderEld() {
		eldaScript.set_inAllocation(allocationSeed);
		eldaScript.forEach_root(allocationSeed, allocationEldad);
		
	}

	private void renderColors() {
		coloriseScript.forEach_root(allocationEldad, allocationColorized);
	}

	private void renderToBitmap() {
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
	}

	private void setupPalette(Context context, int[] colors) {
		int[] d = Palette.getPalette(context, colors, 200);

		Element type = Element.I32(rs);
		Allocation colorAllocation = Allocation.createSized(rs, type, 200);
		coloriseScript.bind_color(colorAllocation);

		colorAllocation.copy1DRangeFrom(0, 200, d);
	}
}
