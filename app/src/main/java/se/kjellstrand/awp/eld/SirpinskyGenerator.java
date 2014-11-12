package se.kjellstrand.awp.eld;

public class SirpinskyGenerator {

	public int[] getSirpinsky(int size, int x1, int y1, int x2, int y2, int x3, int y3){
		int[] result = new int[size*2];
		int x=x1, y=y1;
		for(int i=0;i<size;i++){
			result[i*2]=x;
			result[i*2+1]=y;
		}
		
		return result;
	}
	
}
