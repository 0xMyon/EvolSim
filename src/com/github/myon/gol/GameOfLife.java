package com.github.myon.gol;

import com.github.myon.util.Util;

public class GameOfLife {

	private final boolean[][] map;
	private final int size;

	public GameOfLife(final int size) {
		this.size = size;
		this.map = new boolean[size][size];
		for (int x = 0; x < this.size; x++) {
			for(int y = 0; y < this.size; y++ ) {
				if (Util.nextBoolean()) {
					this.set(x, y);
				} else {
					this.unset(x, y);
				}
			}
		}


	}

	public GameOfLife(final GameOfLife that) {
		this.size = that.size;
		this.map = new boolean[this.size][this.size];
		for (int x = 0; x < this.size; x++) {
			for(int y = 0; y < this.size; y++ ) {
				if (that.get(x, y)) {
					switch (that.count(x,y)) {
					case 2:
					case 3:
						this.set(x, y);
						break;
					default:
						this.unset(x, y);
						break;
					}
				} else if (that.count(x, y) == 3) {
					this.set(x, y);
				} else {
					this.unset(x, y);
				}
			}
		}
	}

	private int count(final int x, final int y) {
		int result = 0;
		for (int i = -1; i <= 1; i++) {
			for (int j = -1; j <= 1; j++) {
				if ((i != 0 || j != 0) && this.getModulo(x+i, y+j)) {
					result++;
				}
			}
		}
		return result;
	}

	public boolean get(final int x, final int y) {
		return this.map[x][y];
	}

	public boolean getModulo(final int x, final int y) {
		return this.map[Math.floorMod(x, this.size)][Math.floorMod(y, this.size)];
	}

	public void set(final int x, final int y) {
		this.map[x][y] = true;
	}

	public void unset(final int x, final int y) {
		this.map[x][y] = false;
	}

	public GameOfLife next() {
		return new GameOfLife(this);
	}

	public int size() {
		return this.size;
	}

}
