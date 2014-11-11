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
	private Allocation allocationIn;
	private Allocation allocationOut;
	private Allocation allocationBmp;

	private int width;
	private int height;

	private ScriptC_colorize coloriseScript;

	private ScriptC_elda elda;

	public EldGenerator(Context context, int width, int height) {
		this.width = width;
		this.height = height;

		RenderScript rs = RenderScript.create(context,
				RenderScript.ContextType.DEBUG);
		rs.setPriority(RenderScript.Priority.LOW);

		coloriseScript = new ScriptC_colorize(rs);
		elda = new ScriptC_elda(rs);

		bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);

		allocationIn = Allocation.createSized(rs, Element.I32(rs), width
				* height);
		allocationOut = Allocation.createSized(rs, Element.I32(rs), width
				* height);
		allocationBmp = Allocation.createFromBitmap(rs, bitmap, Allocation.MipmapControl.MIPMAP_NONE, Allocation.USAGE_SCRIPT);

		
		  Type t0, t1;        // Verify dimensions
	        t0 = allocationOut.getType();
	        t1 = allocationBmp.getType();
    	Log.d("TAG", "t0: "+t0.getCount()+" "+t0.getName()+" "+t0.getX()+" "+t0.getY()+" "+t0.getElement() );
    	Log.d("TAG", "t1: "+t1.getCount()+" "+t1.getName()+" "+t1.getX()+" "+t1.getY()+" "+t1.getElement() );

		
	}

	public Bitmap getEldadBitmap(int frame) {
		long t = System.currentTimeMillis();
		seedEldAsLine(frame);
		Log.d(TAG, "seedEldAsLine: " + (System.currentTimeMillis() - t));
		t = System.currentTimeMillis();
		renderEld();
		Log.d(TAG, "renderEld: " + (System.currentTimeMillis() - t));
		t = System.currentTimeMillis();
		renderColors();
		Log.d(TAG, "renderColors: " + (System.currentTimeMillis() - t));

		// swapAllocations();
		return bitmap;
	}

	private void seedEldAsLine(int frame) {
		int[] eldValues = new int[width * height];
		allocationOut.copyTo(eldValues);
		for (int y = height - 4; y < height; y++) {
			for (int x = 0; x < width; x++) {
				eldValues[(y * width) + x] = (int) ((Math.sin((x + frame) / 20) + 1) * 512);
			}
		}
		allocationIn.copyFrom(eldValues);
	}

	private void renderEld() {
		elda.forEach_root(allocationIn, allocationOut);
	}

	private void renderColors() {
		coloriseScript.forEach_root(allocationOut, allocationBmp);
		
		// colorAllocation.copy1DRangeFrom(0, theme.precission * 3, d);
		11-11 22:09:52.373: D/TAG(14607): t0: 90000 null 90000 0 android.renderscript.Element@c57a900
		11-11 22:09:52.373: D/TAG(14607): t1: 90000 null 300 300 android.renderscript.Element@c57aa80

	}

	private void swapAllocations() {
		// Swap buffers
		Allocation tmp = allocationIn;
		allocationIn = allocationOut;
		allocationOut = tmp;
	}
}
