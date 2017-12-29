//Author: William Tyler Wilson
package CSCProject4;

import java.util.Arrays;

//----------------------------------------------------------
// Heap Class created for finding the shortest path from one
// Place to another using Djikstra's algorithm
// Most of this implementation is from the Heap Lab.
//----------------------------------------------------------
public class MyHeap implements Heap<Intersection>{
	private Intersection[] heapArray;
	private int size;
	private int default_capacity;
	
	public MyHeap(int capacityInput) {
		size = 1;
		default_capacity = capacityInput;
		heapArray = new Intersection[default_capacity+1];
	}
	public MyHeap() {
		size = 1;
		default_capacity = 10;
		heapArray = new Intersection[default_capacity+1];
	}
	public MyHeap(Intersection[] heapPreMade) {
		size = heapPreMade.length;
		default_capacity = heapPreMade.length;
		heapArray = new Intersection[default_capacity+1];
		heapify(heapPreMade);
	}
	
	public int checkForIntersection(Intersection inter) {
		for (int i=0; i < size; i++) {
			if (heapArray[i] == null) {
				return -1;
			}
			else if (heapArray[i].equals(inter)) {
				return i;
			}
		}
		return -1;
	}
	
	//This method inserts the Intersection at a specific spot and bubbles it up
	public void insertSpecial(int place, Intersection toInsert) {
		heapArray[place] = toInsert;
		bubbleUp(place);
	}
	
	public void heapify(Intersection[] heap) {
		for (int i=0; i < heap.length; i++) {
			heapArray[i+1] = heap[i];
		}
		for (int i=heapArray.length/2; i > 0; i--) {
			bubbleDown(i);
		}
	}
	
	public void insert(Intersection item) {
		if (size < default_capacity) {
			size++;
			heapArray[size-1] = item;
			bubbleUp(size-1);
		} else {
			heapArray = Arrays.copyOf(heapArray, default_capacity+1);
			default_capacity = default_capacity+1;
			size++;
			heapArray[size-1] = item;
			bubbleUp(size-1);
		}
		
	}
	public void bubbleUp(int sizeItem) {
		if (sizeItem > 0 && sizeItem/2 > 0) {
			while ((heapArray[sizeItem]).compare(heapArray[sizeItem/2]) < 0 && sizeItem > 0) {
				swap(sizeItem, sizeItem/2);
				sizeItem /= 2;
				if (sizeItem/2 <= 0) {
					break;
				}
			}
		}
		
	}
	
	public void bubbleDown(int index) {
		if (index*2 >= size && index*2+1 > size) {
			
		} else if (index*2 < size && index*2+1 > size) {
			
			
			if (heapArray[index].compare(heapArray[index*2]) > 0) {
				swap(index, index*2);
				bubbleDown(index*2);
			}
		} else if (index*2 < size && index*2+1 < size){
			if (heapArray[index*2].compare(heapArray[index*2+1])> 0) {
				//left one is greater - swap with right
				if (heapArray[index].compare(heapArray[index*2+1]) > 0) {
					swap(index, index*2+1);
					bubbleDown(index*2+1);
				}
			} else {
				
				if (heapArray[index].compare(heapArray[index*2]) > 0) {
					swap(index, index*2);
					bubbleDown(index*2);
				}
			}
		}
	}
	
	public Intersection deleteMin() {
		swap(1, size-1);
		size--;
		Intersection inter = heapArray[size];
		heapArray[size] = null;
		bubbleDown(1);
		return inter;
		
	}
	
	public void swap(int toSwitch1, int toSwitch2) {
		Intersection tempValue = heapArray[toSwitch1];
		heapArray[toSwitch1] = heapArray[toSwitch2];
		heapArray[toSwitch2] = tempValue;
	}

	public boolean isEmpty() {
		if (size <= 1) {
			return true;
		}
		return false;
	}

	public int size() {
		return size-1;
	}

	
	public void printHeap() {
		for (int i=0; i < heapArray.length; i++) {
			
			if (heapArray[i] != null) System.out.print(i + ": " + heapArray[i].name + ":" + heapArray[i].bestLength + " ");
		}
		System.out.println();
	}

}

