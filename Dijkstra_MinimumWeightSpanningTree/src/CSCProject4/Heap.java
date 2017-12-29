//Author: William Tyler Wilson
package CSCProject4;

@SuppressWarnings("hiding")
public interface Heap<Intersection> {
	public void insert(Intersection item);
	public boolean isEmpty();
	public int size();
	public Intersection deleteMin();
}
