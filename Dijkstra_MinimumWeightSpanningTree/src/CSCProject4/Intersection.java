//Author: William Tyler Wilson
package CSCProject4;

import java.util.LinkedList;
import java.util.ListIterator;

public class Intersection {
	//This is the Intersection class, where Intersections are defined.
	
	//The variables that an intersection holds:
	public String name;
	public double x;
	public double y;
	public int number;
	
	//These are used for finding the Shortest Path:
	public double bestLength = Integer.MAX_VALUE;
	public Intersection fromIntersection;
	public boolean beenFound = false;
	
	//visitedMin is used for minimum Weight Spanning Trees
	public boolean visitedMin = false;
	
	//Each Intersection has a linkedList of edges that it contains
	public LinkedList<Road> edges = new LinkedList<Road>();
	
	//Constructor for an Intersection
	public Intersection(String name1, double x1, double y1) {
		name = name1;
		x = x1;
		y = y1;
	}
	
	//Adds a road to the intersection
	public void addRoad(Road road) {
		edges.add(road);
	}
	
	//Print all the roads that the intersection contains
	public void printRoads() {
		System.out.println(this.name + " has the Roads: ");
		ListIterator<Road> listIterator = edges.listIterator();
		while (listIterator.hasNext()) {
			System.out.println(listIterator.next().name);
		}
	}
	
	//Compare the intersections bestLength to another's. Used for shortest Path.
	public int compare(Intersection intersection2) {
		if (this.bestLength > intersection2.bestLength) {
			return 1;
		} else if (this.bestLength < intersection2.bestLength) {
			return -1;
		} else {
			return 0;
		}		
	}
}
