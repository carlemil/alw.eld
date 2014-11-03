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

    private Bitmap bitmap;

    private Allocation allocation;

    private ScriptIntrinsicConvolve3x3 scriptIntrinsicConvolve3x3;

    public EldGenerator(Context context, int width, int height){

        RenderScript rs = RenderScript.create(context, RenderScript.ContextType.DEBUG);
        rs.setPriority(RenderScript.Priority.LOW);
        Element element = Element.U8(rs);

        scriptIntrinsicConvolve3x3 = ScriptIntrinsicConvolve3x3.create(rs, element);

        float[] matrix = new float[]{
                0f,0f,0f,
                1f,1f,1f,
                0f,1f,0f};
        scriptIntrinsicConvolve3x3.setCoefficients(matrix);

        //allocation = Allocation.;
    }

    public Bitmap getEldadBitmap(){
        return bitmap;
    }
}
