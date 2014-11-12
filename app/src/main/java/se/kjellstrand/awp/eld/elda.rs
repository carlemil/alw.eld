
#pragma version(1)
#pragma rs java_package_name(se.kjellstrand.awp.eld)
#pragma rs_fp_relaxed 

rs_allocation inAllocation;

void root(const int *in, int *out, uint32_t x, uint32_t y) {
  const int v10 = *(const int*)rsGetElementAt(inAllocation, x);
  const int v12 = *(const int*)rsGetElementAt(inAllocation, x);
  const int v21 = *(const int*)rsGetElementAt(inAllocation, x);
  //return *v;
   *out = *in;
}