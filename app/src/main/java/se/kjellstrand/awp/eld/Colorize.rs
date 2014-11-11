
#pragma version(1)
#pragma rs java_package_name(se.kjellstrand.awp.eld)
#pragma rs_fp_relaxed 

int range;
uchar *color;

void root(const float *in, float *out, uint32_t x, uint32_t y) {
    *out = *in;
    
    //out->b = (float)255;//color[c*3+0];
    //    out->g = color[c*3+1];
    //    out->r = color[c*3+2];
}