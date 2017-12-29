//Author: William Tyler Wilson
package CSCProject4;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.ListIterator;

public class Graph {
	//Define initial values
	
	//Hashmap is used to create the table quickly
	 private HashMap<String, Intersection>intersectionHash = new HashMap<String, Intersection>();
	 
	 //Array is used for Minimum Weight Spanning Tree, and so is allRoads and minTree LinkedLists
	 Road[] allOfTheRoads;
	 
	 //Intersections is a linkedlist of all the intersections
	 LinkedList<Intersection> intersections;
	 
	 LinkedList<Road> allRoads;
	 LinkedList<Road> minTree;
	 
	 //current loops through the intersections assigning a value and totalIntersections is the number
	 //of intersections
	 public int current = 0;
	 public int totalIntersections;
	 
	 //tuples is used for the Minimum Weight Spanning tree
	 Tuple[] tuples;
	 
	 //Heap is necessary for DjikStra's algorithm
	 MyHeap heap = new MyHeap(2048);
	 
	 //Initialize the Graph
	 public Graph () {
		 intersections = new LinkedList<Intersection>();
		 minTree = new LinkedList<Road>();
		 allRoads = new LinkedList<Road>();
	 }
	 
	 //This method inserts an Intersection when creating the graph
	 public void insertIntersection(Intersection newInter) {
		 intersections.add(newInter);
		 this.intersectionHash.put(newInter.name, newInter);
		 newInter.number = current;
		 current++;
		 totalIntersections = current;
	 }
	 
	 //Prints all the intersections, which also prints all of their roads
	 public void printInter() {
		 ListIterator<Intersection> listIterator = intersections.listIterator();
			while (listIterator.hasNext()) {
				listIterator.next().printRoads();
			}
	 }
	 
	 //Initializes the array of roads, after the roads have been all added to the graph
	 public void createArrayRoads() {
		 allOfTheRoads = new Road[allRoads.size()];
	 }
	 
	//This method inserts a road using the two road names it connects
	public void insertRoad(String name, String road1, String road2) {
		Road road = new Road(name);
		
		//Get the intersections the road connects
		Intersection inter1 = this.intersectionHash.get(road1);
		Intersection inter2 = this.intersectionHash.get(road2);
		
		//Set the intersections in the road
		road.setRoadOne(inter1);
		road.setRoadTwo(inter2);
		
		//Add the roads to the intersections
		inter1.addRoad(road);
		inter2.addRoad(road);
		
		//add the road to allRoads LinkedList
		allRoads.add(road);
		
		//Calculate the distance in miles that the road goes for, and set road.length to this value
		double R = 6378.137;
		double dLat = road.roadSecond.x* Math.PI/180 - road.roadFirst.x * Math.PI/180;
		double dLon = road.roadSecond.y* Math.PI/180 - road.roadFirst.y * Math.PI/180;
		
		double a = Math.sin(dLat/2) * Math.sin(dLat/2) + Math.cos(road.roadFirst.x * Math.PI/180) * Math.cos(road.roadSecond.x*Math.PI/180) * Math.sin(dLon/2) * Math.sin(dLon/2);
		double c = 2* Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
		
		double d = R*c;
		road.length = (d*1000)/1609.34;
		
		//Print Road:
		//System.out.println("ROAD: " + road.name + "    " + road.roadFirst + ": " + road.roadSecond);
		
	}
	
	//Start finding the shortest path
	public Intersection shortestPathStart(String intersectionS, String intersectionE) {
		//Get the Intersection corresponding to the first and last intersections of the path.
		Intersection starter = stringToIntersection(intersectionS);
		Intersection finalGoal = stringToIntersection(intersectionE);
		
		//Set the first Intersection in the path to length 0, and found to true for it.
		starter.bestLength = 0;
		starter.beenFound = true;
		starter.fromIntersection = starter;
		
		//Loop through roads connected
		for (int i=0; i < starter.edges.size(); i++) {
			//The length is original Nodes length + the length of the edge.
			double roadLength = starter.bestLength + starter.edges.get(i).length;
			
			// If the road connected has not yet been found, proceed
			if (starter.edges.get(i).roadFirst.beenFound == false) {
				//If the length is less than the best length, reset the bestLength
				if (roadLength < starter.edges.get(i).roadFirst.bestLength) {
					starter.edges.get(i).roadFirst.bestLength = roadLength;
					starter.edges.get(i).roadFirst.fromIntersection = starter;
					
					//Check if the heap has the intersection, if it does, replace that specific spot and bubble it up
					//otherwise enter it into the heap.
					int result = heap.checkForIntersection(starter.edges.get(i).roadFirst);
					if (result == -1) {
						heap.insert(starter.edges.get(i).roadFirst);
					} else {
						heap.insertSpecial(result, starter.edges.get(i).roadFirst);
					}
				}
				
			//Check if the second road has been found
			} else if (starter.edges.get(i).roadSecond.beenFound == false) {
				//Check if the bestLength is less than the starters bestlength, if it is, set bestLength accordingly.
				if (roadLength < starter.edges.get(i).roadSecond.bestLength) {
					starter.edges.get(i).roadSecond.bestLength = roadLength;
					starter.edges.get(i).roadSecond.fromIntersection = starter;
					
					//Check if the heap has the intersection, if it does, replace that specific spot and bubble it up
					//otherwise enter it into the heap.
					int result = heap.checkForIntersection(starter.edges.get(i).roadSecond);
					if (result == -1) {
						heap.insert(starter.edges.get(i).roadSecond);
					} else {
						heap.insertSpecial(result, starter.edges.get(i).roadSecond);
					}
				}
			}
		}
		//If the heap is empty and no roads connect to the first, return null
		if (heap.isEmpty()) {
			return null;
		}
		//Print Heap if necessary:
		//heap.printHeap();
		
		//Continue the shortest Path, easier to continue here
		return shortestPathContinue(starter, finalGoal);
		
	}
	
	//Cointinues Dijkstra's algorithm and returns the final result
	public Intersection shortestPathContinue(Intersection intersectionS, Intersection finalGoal) {
		//While the heap is not empty, continue
		while (heap.isEmpty() == false) {
			//Take the first value off the top of the heap and mark it as found
			Intersection minIntersection = heap.deleteMin();
			minIntersection.beenFound = true;
			
			//if the minIntersection equals the finalGoal, break the loop and return the finalGoal as it is set.
			if (minIntersection.equals(finalGoal)) {
				break;
			}
			
			//Loop through the List of roads connected to the minIntersection
			ListIterator<Road> listIterator = minIntersection.edges.listIterator();
			while (listIterator.hasNext()) {
				
				//Set the road length accordingly
				Road currentI = listIterator.next();
				double roadLength = minIntersection.bestLength + currentI.length;
				
				//Check if the first road has been found and if its path is longer or shorter in turn
				if (currentI.roadFirst.beenFound == false) {
					if (roadLength < currentI.roadFirst.bestLength) {
						currentI.roadFirst.bestLength = roadLength;
						currentI.roadFirst.fromIntersection = minIntersection;
						//Check if the heap has the intersection, if it does, replace that specific spot and bubble it up
						//otherwise enter it into the heap.
						int result = heap.checkForIntersection(currentI.roadFirst);
						if (result == -1) {
							heap.insert(currentI.roadFirst);
						} else {
							heap.insertSpecial(result, currentI.roadFirst);
						}
					}
					
				//Check if the second road has been found and if its path is longer or shorter in turn
				} else if (currentI.roadSecond.beenFound == false) {
					if (roadLength < currentI.roadSecond.bestLength) {
						currentI.roadSecond.bestLength = roadLength;
						currentI.roadSecond.fromIntersection = minIntersection;
						//Check if the heap has the intersection, if it does, replace that specific spot and bubble it up
						//otherwise enter it into the heap.
						int result = heap.checkForIntersection(currentI.roadSecond);
						if (result == -1) {
							heap.insert(currentI.roadSecond);
						} else {
							heap.insertSpecial(result, currentI.roadSecond);
						}
					}
				}
			}
			//Print heap if necessary:
			//heap.printHeap();
			if (heap.isEmpty()) {
				return null;
			}
		}
		return finalGoal;
		
	}
	
	//Converts a string name into an Intersection, does not do it quickly however, not to be relied on
	public Intersection stringToIntersection(String interS) {
		for (int i=0; i < intersections.size(); i++) {
			if (intersections.get(i).name.equals(interS)) {
				return intersections.get(i);
			}
		}
		return null;
	}
	
	//------------------------------------------------------
	// Creates the minTree, which is the minimum Weight Spanning Tree
	// using a variation of Prim's algorithm, which is somewhat inefficient in my case.
	// I DO NOT USE THE FOLLOWING CODE, I simply did not delete it either.
	// This is my first implementation of the minimumWeightSpanningTree which works for smaller trees
	//------------------------------------------------------
	public void minimumWeightSpanningTree() {
		LinkedList<Intersection> knownIntersections = new LinkedList<Intersection>();
		Intersection first = intersections.getFirst();
		
		first.visitedMin = true;
		knownIntersections.add(first);
		Road minimum = new Road("low");
		minimum.length = Double.MAX_VALUE;
		//step 1 - put all values in massive array
		while (knownIntersections.size() < intersections.size()) {
			minimum = new Road("low");
			minimum.length = Double.MAX_VALUE;
			ListIterator<Intersection> listIterator = knownIntersections.listIterator();
			while (listIterator.hasNext()) {
				Intersection inter1 = listIterator.next();
				
				ListIterator<Road> roadIterator = inter1.edges.listIterator();
				while (roadIterator.hasNext()) {
					Road road = roadIterator.next();
					
					if (minimum.length > road.length && road.used == false) {
						minimum = road;
					}
				}
			}
			if (!minimum.roadSecond.visitedMin || !minimum.roadFirst.visitedMin) {
				minTree.add(minimum);
				if (!minimum.roadSecond.visitedMin) {
					minimum.roadSecond.visitedMin = true;
					knownIntersections.add(minimum.roadSecond);
				} 
				if (!minimum.roadFirst.visitedMin){
					minimum.roadFirst.visitedMin = true;
					knownIntersections.add(minimum.roadFirst);
				}
			}
			minimum.used = true;
		}
	}
	
	
	//---------------------------------------------------------------------
	// This is my current implementation of the MinimumWeightSpanningTree
	// It sorts the roads by their length, and adds them if they are not connected to each other already
	// This algorithm runs significantly faster then my previous implementation above.
	// It creates disjoint sets using Tuple's and Union as well as root() and depth() methods.
	//---------------------------------------------------------------------
	public void betterMinimumWeightSpanningTree() {
		//Create the arrays accordingly.
		tuples = new Tuple[intersections.size()];
		allOfTheRoads = allRoads.toArray(allOfTheRoads);
		
		//Sort the roads by their lengths
		Arrays.sort(allOfTheRoads, new EdgeCompare());
		
		//Create the tuple's of intersection and id's.
		for (int i=0; i < intersections.size(); i++) {
			tuples[i] = new Tuple(i, intersections.get(i));
		}
		
		//Create the disjoint sets and the minimum Spanning tree in turn.
		for (int i=0; i <allOfTheRoads.length; i++) {
			if (root(allOfTheRoads[i].roadFirst.number) != (root(allOfTheRoads[i].roadSecond.number))) {
				minTree.add(allOfTheRoads[i]);
				union(allOfTheRoads[i].roadFirst.number, allOfTheRoads[i].roadSecond.number);
			}
		}
	}
	
	//----------------------------------------------------------
	// The following methods are all used to create the minimum spanning tree
	// finding root() depth() and combining roads with union() in turn.
	//----------------------------------------------------------
	public int root(int i) {
		if (i == tuples[i].a) {
			return i;
		}
		else {
			return root(tuples[i].a);
		}
	}
	
	public int depth(int i) {
		if (i == tuples[i].a) {
			return 0;
		}
		else {
			return 1+depth(tuples[i].a);
		}
	}
	
	public void union(int a, int b) {
		if (root(a) == root(b)) return;
		if (depth(a) < depth(b)) {
			tuples[root(a)].a = tuples[root(b)].a;
		} else {
			tuples[root(b)].a = tuples[root(a)].a;
		}
	}
	
}
