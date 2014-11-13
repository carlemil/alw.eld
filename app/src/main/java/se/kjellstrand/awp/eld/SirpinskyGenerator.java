package se.kjellstrand.awp.eld;

public class SirpinskyGenerator {

	private int size;

	private int[] rndData;

	private int bufferSize = 10;

	private int width;
	private int height;

	public SirpinskyGenerator(int size, int width, int height) {
		this.size = size;
		this.width = width;
		this.height = height;

		rndData = new int[size];

		for (int i = 0; i < size; i++) {
			rndData[i] = (int) (Math.random() * 3) + 1;
		}
	}

	public int[] renderSirpinsky(int frame, int seedValue, int[] sirpinskyPoints) {
		int x1 = getSeedX(frame, 45f, width);
		int x2 = getSeedX(frame, 66f, width);
		int x3 = getSeedX(frame, 73f, width);
		int y1 = getSeedY(frame, 55f, height);
		int y2 = getSeedY(frame, 76f, height);
		int y3 = getSeedY(frame, 47f, height);

		int[] spgValues = getSirpinsky(x1, y1, x2, y2, x3, y3);
		for (int i = 0; i < size; i++) {
			int x = spgValues[i * 2];
			int y = spgValues[i * 2 + 1];
			sirpinskyPoints[(y * width) + x] = seedValue;
		}

		return sirpinskyPoints;
	}

	private int[] getSirpinsky(int x1, int y1, int x2, int y2, int x3, int y3) {
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

	private int getSeedX(int frame, float frequency, int width) {
		return (int) ((Math.sin(frame / frequency) + 1) / 2f
				* (width - bufferSize * 2) + bufferSize);
	}

	private int getSeedY(int frame, float frequency, int height) {
		int drawArea = height * 2 / 3;
		int spaceAboveDrawArea = height - drawArea;
		return (int) ((Math.sin(frame / frequency) + 1) / 2f
				* (drawArea - bufferSize * 2) + bufferSize)
				+ spaceAboveDrawArea;
	}
}
