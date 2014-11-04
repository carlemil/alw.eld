package se.kjellstrand.awp.eld;

import android.content.Context;
import android.graphics.Bitmap;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicConvolve3x3;

/**
 * Created by Carl-Emil Kjellstrand on 11/3/14.
 */
public class EldGenerator {

    Element elementU8;
    Element elementRGBA;
    private Bitmap bitmap;
    private Allocation allocationIn;
    private Allocation allocationOut;
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
                0f, 0f, 0f,
                1f, 1f, 1f,
                0f, 1f, 0f};
        scriptIntrinsicConvolve3x3.setCoefficients(matrix);

        allocationIn = Allocation.createSized(rs, elementU8, (width * height));
        allocationOut = Allocation.createSized(rs, elementU8, (width * height));
        byte[] f = new byte[width*height];
        for(int x=0; x < width; x++){
            f[(height*(width-1))+x] = (byte)(Math.random()*100+155);
        }
        allocationIn.copyFrom(f);


        bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
    }

    public Bitmap getEldadBitmap() {


        byte[] eldValues = new byte[width*height];
        allocationOut.copyTo(eldValues);
        for(int y=0; y < height; y++) {
            for(int x=0; x < width; x++) {
                eldValues[x+(y*width)] = (byte) (Math.random() * 100 + 155);
            }
        }
        allocationIn.copyFrom(eldValues);


        //renderEld();

        renderColors();

        //swapAllocations();

        return bitmap;
    }

    private void renderColors() {
        byte[] eldValues = new byte[width*height];
        allocationOut.copyTo(eldValues);

        for(int y=0; y < height; y++) {
            for(int x=0; x < width; x++){
                int c = (int)(eldValues[x+(y*width)])&255;
                c += c * 256;
                c += c * 256;
                bitmap.setPixel(x,y, c);
            }
        }
    }

    private void renderEld() {
        scriptIntrinsicConvolve3x3.setInput(allocationIn);
        scriptIntrinsicConvolve3x3.forEach(allocationOut);
    }

    private void swapAllocations() {
        // Swap buffers
        Allocation tmp = allocationIn;
        allocationIn = allocationOut;
        allocationOut = tmp;
    }
}
