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

    Element elementF32;
    Element elementRGBA;
    private Bitmap bitmap;
    private Allocation allocationU8in;
    private Allocation allocationU8out;
    private ScriptIntrinsicConvolve3x3 scriptIntrinsicConvolve3x3;
    private int width;
    private int height;

    public EldGenerator(Context context, int width, int height) {
        this.width = width;
        this.height = height;

        RenderScript rs = RenderScript.create(context, RenderScript.ContextType.DEBUG);
        rs.setPriority(RenderScript.Priority.LOW);

        elementF32 = Element.F32(rs);
        elementRGBA = Element.RGBA_8888(rs);

        scriptIntrinsicConvolve3x3 = ScriptIntrinsicConvolve3x3.create(rs, elementF32);

        float[] matrix = new float[]{
                0f, 0f, 0f,
                1f, 1f, 1f,
                0f, 1f, 0f};
        scriptIntrinsicConvolve3x3.setCoefficients(matrix);

        allocationU8in = Allocation.createSized(rs, elementF32, (width * height));
        allocationU8out = Allocation.createSized(rs, elementF32, (width * height));
    }


    public Bitmap getEldadBitmap() {
        renderEld();

        renderColors();

        swapAllocations();

        return bitmap;
    }

    private void renderColors() {
    int size = width* height;
        for(int i=0; i < size; i++){

        }

    }

    private void renderEld() {
        scriptIntrinsicConvolve3x3.setInput(allocationU8in);
        scriptIntrinsicConvolve3x3.forEach(allocationU8out);
    }

    private void swapAllocations() {
        // Swap buffers
        Allocation tmp = allocationU8in;
        allocationU8in = allocationU8out;
        allocationU8out = tmp;
    }
}
