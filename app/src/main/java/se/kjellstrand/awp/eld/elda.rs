
#pragma version(1)
#pragma rs java_package_name(se.kjellstrand.awp.eld)
#pragma rs_fp_relaxed 

rs_allocation inAllocation;
int width;
int height;

void root(const int *in, int *out, uint32_t x, uint32_t y) {
	int pos = x + y * width;
	if(pos > 0 && pos < width * (height - 1)){
		const int v10 = *(const int*)rsGetElementAt(inAllocation, pos - 1);
		const int v12 = *(const int*)rsGetElementAt(inAllocation, pos + 1);
		const int v21 = *(const int*)rsGetElementAt(inAllocation, pos + width);
		*out = (*in + v10 + v12 + v21)>>2;
	}
}