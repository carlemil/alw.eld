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

	public EldGenerator(Context context, int width, int height) {
		this.width = width;
		this.height = height;

		RenderScript rs = RenderScript.create(context,
				RenderScript.ContextType.DEBUG);
		rs.setPriority(RenderScript.Priority.LOW);

		coloriseScript = new ScriptC_colorize(rs);
		eldaScript = new ScriptC_elda(rs);
		
		eldaScript.set_width(width);
		eldaScript.set_height(height);

		bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);

		allocationSeed = Allocation.createSized(rs, Element.I32(rs), width * height);
		allocationEldad = Allocation.createSized(rs, Element.I32(rs), width * height);
		allocationColorized = Allocation.createSized(rs, Element.I32(rs), width * height); 
		
		//Allocation.createFromBitmap(rs, bitmap, Allocation.MipmapControl.MIPMAP_NONE, Allocation.USAGE_SCRIPT);

		
		  Type t0, t1;        // Verify dimensions
	        t0 = allocationEldad.getType();
	        t1 = allocationColorized.getType();
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

		t = System.currentTimeMillis();
		renderToBitmap();
		Log.d(TAG, "renderToBitmap: " + (System.currentTimeMillis() - t));
		
		// swapAllocations();
		return bitmap;
	}

	private void seedEldAsLine(int frame) {
		int[] eldValues = new int[width * height];
		allocationEldad.copyTo(eldValues);
		for (int y = height - 14; y < height; y++) {
			for (int x = 0; x < width; x++) {
				eldValues[(y * width) + x] = 255; //(255<<24)+(127<<16)+(255<<8)+255; 
				//(int) ((Math.sin((x + frame) / 20) + 1) * 512);
			}
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

	private void swapAllocations() {
		// Swap buffers
		Allocation tmp = allocationSeed;
		allocationSeed = allocationEldad;
		allocationEldad = tmp;
	}
}
