package com.puyapps.cutepetsmemorygame;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MemoryApp<T> {
	private final int cols, rows;
	private final T[] values;
	private final int[] positions;
	
	private boolean[] visible;
	private float[] timeout;
	
	@SuppressWarnings("unchecked")
	public MemoryApp(List<T> values, int cols, int rows) {
		if (cols * rows % 2 == 1 || cols < 1 || rows < 1 || cols * rows < 2 || cols * rows / 2 != values.size()) {
			throw new RuntimeException("Invalid table: " + cols + "x" + rows + ". Elements: " + values.size());
		}
		
		this.cols = cols;
		this.rows = rows;
		
		int rc = rows * cols,
			rch = rc / 2;

		this.values = (T[]) values.toArray();
		positions = new int[rc];
		visible = new boolean[rc];
		timeout = new float[rc];

		ArrayList<Integer> aL = new ArrayList<Integer>(rch);
		for (int i = 0; i < rch; i++) {
			aL.add(i);
			aL.add(i);
		}
		
		Collections.shuffle(aL);
		
		for (int i = 0; i < rc; i++) {
			positions[i] = aL.get(i).intValue();
		}
	}
	
	public void update(float deltaTime) {
		float time = deltaTime;

		for (int i = 0; i < positions.length; i++) {
			if (visible[i]) {
				if (timeout[i] > 0) {
					timeout[i] -= time;
					
					if (timeout[i] <= 0) {
						timeout[i] = 0;
						
						visible[i] = false;
					}
				
				} else if (timeout[i] < 0) {
					timeout[i] += time;
					
					if (timeout[i] >= 0) {
						timeout[i] = 0;
					}
				}
			}
		}
	}
	
	public T get(int col, int row) {		
		int k = getK(col, row);

		if (visible[k]) {
			return values[positions[k]];
	
		} else {
			return null;
		}
	}
	
	public float secondsToVisible(int col, int row) {
		int k = getK(col, row);
		
		if (timeout[k] < 0) {
			return -timeout[k];
	
		} else {
			return 0;
		}
	}
	
	public float secondsToInvisible(int col, int row) {
		int k = getK(col, row);
		
		if (timeout[k] > 0) {
			return timeout[k];
	
		} else {
			return 0;
		}
	}
	
	public ArrayList<Integer> getUnpaired() {
		// Get unpaired
		ArrayList<Integer> unpaired = new ArrayList<Integer>();
		
		for (int i = 0; i < positions.length; i++) {
			if (visible[i]) {
				boolean found = false;
				
				for (int j = 0; j < positions.length; j++) {
					if (visible[j] && i != j && positions[i] == positions[j]) {
						found = true;
						break;
					}
				}
				
				if (!found) {
					unpaired.add(i);
				}
			}
		}
		
		return unpaired;
	}
	
	public void turn(int col, int row) {
		int k = getK(col, row);
		
		// Get unpaired
		ArrayList<Integer> unpaired = this.getUnpaired();
		
		if (visible[k]) {
			if (unpaired.size() == 1 && unpaired.get(0) == k) {
				if (timeout[k] < 0) {
					timeout[k] = -timeout[k];
				} else {
					timeout[k] = MemoryGame.CARDS_TIMEOUT;
				}
			}
		
		} else {
			if (unpaired.size() >= 2) {
				for (int i = 0; i < unpaired.size(); i++) {
					visible[unpaired.get(i)] = false;
					timeout[unpaired.get(i)] = 0;
				}
			
			}
			
			if (unpaired.size() == 1 && positions[unpaired.get(0)] != positions[k]) {
				timeout[unpaired.get(0)] = MemoryGame.CARDS_TIMEOUT;
				timeout[k] = MemoryGame.CARDS_TIMEOUT;
			
			} else {
				timeout[k] = -MemoryGame.CARDS_TIMEOUT;
			}
			
			visible[k] = true;
		}
	}
	
	public boolean isComplete() {
		for (int i = 0; i < this.visible.length; i++) {
			if (!visible[i]) {
				return false;
			}
		}
		
		for (int i = 0; i < this.timeout.length; i++) {
			if (timeout[i] != 0) {
				return false;
			}
		}
		
		return true;
	}
	
	public float getPanelScore() {
		int cVisible = 0,
			cUnpaired = this.getUnpaired().size();

		for (int i = 0; i < this.visible.length; i++) {
			if (!visible[i]) {
				cVisible++;
			}
		}
		
		return ((float)cVisible - ((float)cUnpaired)) / ((float)visible.length);
	}
	
	private int getK(int col, int row) {
		if (col <= 0 || col > cols || row <= 0 || row > rows) {
			throw new RuntimeException("Invalid value: " + col + "x" + row);
		}
		
		return (col - 1) * rows + row - 1;
	}

}
