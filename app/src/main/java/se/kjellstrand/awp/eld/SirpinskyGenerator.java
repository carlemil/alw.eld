package se.kjellstrand.awp.eld;

public class SirpinskyGenerator {

	private int size;

	private int[] rndData;

	public SirpinskyGenerator(int size) {
		this.size = size;
		rndData = new int[size];
		for (int i = 0; i < size; i++) {
			rndData[i] = (int) (Math.random() * 3) + 1;
		}
	}

	public int[] getSirpinsky(int x1, int y1, int x2, int y2, int x3, int y3) {
		int[] result = new int[size * 2];
		int x = x1, y = y1, tx = x, ty = y;
		for (int i = 0; i < size; i++) {
			
			switch (rndData[i]) {
			case 1:
				tx = x1;
				ty = y1;
				break;
			case 2:
				tx = x2;
				ty = y2;
				break;
			case 3:
				tx = x3;
				ty = y3;
				break;
			}

			x = (x + tx) >> 1;
			y = (y + ty) >> 1;

			result[i * 2] = x;
			result[i * 2 + 1] = y;
		}

		return result;
	}

	public int getSize() {
		return size;
	}

}
