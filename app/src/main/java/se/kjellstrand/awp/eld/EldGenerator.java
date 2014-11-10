package se.kjellstrand.awp.eld;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicConvolve3x3;
import android.renderscript.Type;
import android.util.Log;

/**
 * Created by Carl-Emil Kjellstrand on 11/3/14.
 */
public class EldGenerator {

    private static final String TAG = EldGenerator.class.getCanonicalName();
    Element elementU8;
    Element elementRGBA;
    private Bitmap bitmap;
    private Allocation allocationIn;
    private Allocation allocationOut;
    private Allocation allocationBmp;

    private ScriptIntrinsicConvolve3x3 scriptIntrinsicConvolve3x3;
    private int width;
    private int height;

    public EldGenerator(Context context, int width, int height) {
        this.width = width;
        this.height = height;

        RenderScript rs = RenderScript.create(context, RenderScript.ContextType.DEBUG);
        rs.setPriority(RenderScript.Priority.LOW);

        elementU8 = Element.U8(rs);
        elementRGBA = Element.RGBA_8888(rs);

        scriptIntrinsicConvolve3x3 = ScriptIntrinsicConvolve3x3.create(rs, elementU8);

        float[] matrix = new float[]{
                0000f, 0001f, 0000f,
                0000f, 0000f, 0000f,
                0000f, 0000f, 0000f};
        scriptIntrinsicConvolve3x3.setCoefficients(matrix);

        bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);

        Type tu8_2d = Type.createXY(rs, elementU8, width, height);
        allocationIn = Allocation.createTyped(rs, tu8_2d);
        allocationOut = Allocation.createTyped(rs, tu8_2d);
        allocationBmp = Allocation.createFromBitmap(rs, bitmap);

    }

    public Bitmap getEldadBitmap() {
        seedEldAsLine();
        renderEld();
        renderColors();
        //swapAllocations();
        return bitmap;
    }

    private void seedEldAsLine() {
        byte[] eldValues = new byte[width*height];
        allocationOut.copyTo(eldValues);
        for(int y=height-14; y < height; y++) {
        for (int x = 0; x < width; x++) {
            eldValues[(y*width) + x] = (byte) (Math.random() * 255);
        }
        }
        allocationIn.copyFrom(eldValues);
    }

    private void renderEld() {
        scriptIntrinsicConvolve3x3.setInput(allocationIn);
        scriptIntrinsicConvolve3x3.forEach(allocationOut);
    }

    private void renderColors() {
        byte[] eldValues = new byte[width*height];
        allocationOut.copyTo(eldValues);

        for(int y=0; y < height; y++) {
            for(int x=0; x < width; x++){
                byte c = eldValues[x+(y*width)];

                bitmap.setPixel(x, y, Color.argb(255,255,0,c));
            }
        }

    }


    private void swapAllocations() {
        // Swap buffers
        Allocation tmp = allocationIn;
        allocationIn = allocationOut;
        allocationOut = tmp;
    }
}
