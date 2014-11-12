
#pragma version(1)
#pragma rs java_package_name(se.kjellstrand.awp.eld)
#pragma rs_fp_relaxed 

void root(const int *in, int *out, uint32_t x, uint32_t y) {
//	int v = *in;
//    v = v & 255;
//    int *color = (255<<24)+(255<<16)+(123<<8)+255
    
    *out = *in + (255<<24);
}