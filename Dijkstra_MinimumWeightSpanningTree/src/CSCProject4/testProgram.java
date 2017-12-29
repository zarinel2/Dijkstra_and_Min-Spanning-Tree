//Author: William Tyler Wilson
package CSCProject4;

import java.awt.Color;
import java.awt.Frame;
import java.awt.Graphics;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ListIterator;
import java.util.Scanner;

import javax.swing.JFrame;

public class testProgram {

	public static void main(String[] args) throws FileNotFoundException {
		//The Filename is the first argument
		String filename = args[0];
		File file = new File(filename);
		
		//booleans that show the map, print directions using dijkstra's algorithm (shortest path) and show
		//minimum weight spanning tree
		boolean show = false;
		boolean directions = false;
		boolean meridianmap = false;
		
		//These are the intersection variables for directions (dijkstra's algorithm) if used
		String intersection1 = "";
		String intersection2 = "";
		
		//Loop through the arguements, and set variables accordingly
		for (int i=1; i < args.length; i++) {
			if (args[i].equals("-show")) {
				show = true;
			} else if (args[i].equals("-meridianmap")) {
				meridianmap = true;
			} else if (args[i].equals("-directions")) {
				directions = true;
				intersection1 = args[i+1];
				intersection2 = args[i+2];
			}
		}
		
		//Create the scanner to read in the file given, and create a graph
		Scanner scan = new Scanner(file);
		Graph graph = new Graph();
		
		//set largest values and smallest values to guarntee they will change
		double largestX = -5000;
		double largestY = -5000;
		double smallestX = 5000;
		double smallestY = 5000;
		
		//set the window size variable, which defines the x and y of the JFrame
		int windowSize = 800;
		
		//Loop through the lines of the file
		while (scan.hasNextLine()) {
			
			//Read in the lines and split them by tabs
			String line= scan.nextLine();
			String[] lineS = line.split("\t");
			
			//If the first letter is an 'i', add an intersection accordingly
			if (lineS[0].equals("i")) {
				//Create intersection
				Intersection newInter = new Intersection(lineS[1], Double.parseDouble(lineS[2]), Double.parseDouble(lineS[3]));
				
				//Set largest and smallest values if necessary
				largestX = Math.max(largestX, Double.parseDouble(lineS[2]));
				largestY = Math.max(largestY, (Double.parseDouble(lineS[3])));
				smallestX = Math.min(smallestX, Double.parseDouble(lineS[2]));
				smallestY = Math.min(smallestY, (Double.parseDouble(lineS[3])));
				
				//Add the intersection to the graph
				graph.insertIntersection(newInter);
				
			} else if (lineS[0].equals("r")) {
				
				//Add the road to the graph, and its intersections, time it by uncommenting the following code
				//long start2 = System.currentTimeMillis();
				graph.insertRoad(lineS[1], lineS[2], lineS[3]);
				//System.out.println("Time to run: "+(System.currentTimeMillis()-start2));
				
			}
		}
		//Creates the array of roads at correct length now that insertRoad has finished
		graph.createArrayRoads();
		
		
		//---------------------------------------------------------
		// CREATE VISUAL GRAPH
		//---------------------------------------------------------
		
		//Scale the graph to fit
		double deltaX = (largestX - smallestX);
		double deltaY = (largestY - smallestY);
		double scalingValue = (windowSize)/Math.max(deltaX, deltaY);
		
		//Create the JFrame and set it to not visible unless -show is set to true
		JFrame frame = new JFrame("CSC Project 4");
		frame.setVisible(false);
		frame.setSize(windowSize,windowSize);
		
		
		if (show) {
			//If show is true, This creates the Graph and scales it properly
			frame.setVisible(true);
			Graphics g = frame.getGraphics();
			g.fillRect(0, 0, 2000, 2000);
			
			int xvalue = 0;
			int yvalue = 0;
			int xvalueRoad1 = 0;
			int yvalueRoad1 = 0;
			int xvalueRoad2 = 0;
			int yvalueRoad2 = 0;
			
			//Loop through the Intersections using a listIterator
			ListIterator<Intersection> interDrawIter =  graph.intersections.listIterator();
			while (interDrawIter.hasNext()) {
				//Get the x and y values of the Intersection and scale to the windowSize
				Intersection inter = interDrawIter.next();
				xvalue = (int) (windowSize-(inter.x - smallestX)*scalingValue);
				yvalue = (int) ((inter.y - smallestY)*scalingValue);
				g.setColor(Color.green);
				
				//Only for ur.txt, I print a period at each of the intersections, for testing
				if (filename.equals("ur.txt")) {
					g.drawString(".", yvalue, xvalue);
				}
				
				//Iterate through the list of roads inside the Intersection
				ListIterator<Road> edgesDrawIter =  inter.edges.listIterator();
				while (edgesDrawIter.hasNext()) {
					
					//Get the next road and draw a line using x and y values from the start and end points of the road
					//thus creating the road and connecting the intersections
					Road road = edgesDrawIter.next();
					Intersection road1 = road.roadFirst;
					xvalueRoad1 = (int) (windowSize-(road1.x - smallestX) * scalingValue);
					yvalueRoad1 = (int) ((road1.y - smallestY) * scalingValue);
					Intersection road2 = road.roadSecond;
					xvalueRoad2 = (int) (windowSize-(road2.x - smallestX) * scalingValue);
					yvalueRoad2 = (int) ((road2.y - smallestY) * scalingValue);
					g.setColor(Color.black);
					g.drawLine(yvalueRoad1, xvalueRoad1, yvalueRoad2, xvalueRoad2);
				}
			}
		}
		
		
		//---------------------------------------------
		// DIJKSTRAS ALGORITHM
		//---------------------------------------------
		if (directions) {
			
			//Get the final intersection in the shortest path, and print distance
			Intersection finalLength = graph.shortestPathStart(intersection1, intersection2);
			
			if (finalLength == null) {
				//If there is no path, say that there is none.
				System.out.println("There is no path from " + intersection1 + " to " + intersection2);
			} else {
				//Print the distance in miles
				System.out.println("Length from " + intersection1 + " to " + intersection2 + " is: " + (finalLength.bestLength) + " in miles.");
				//Print the Shortest Path from intersection start to end, whereFrom is a method using recursion to
				// do this.
				System.out.println(whereFrom("", finalLength));
				
				//Draw CYAN lines over the shortest path to show it as the path traveled
				if (show) {
					Graphics g = frame.getGraphics();
					Intersection current = finalLength;
					Intersection previous = finalLength.fromIntersection;
					
					//Set the color to cyan to draw the line
					g.setColor(Color.CYAN);
					int xvalueInter1 = 0;
					int yvalueInter1 = 0;
					int xvalueInter2 = 0;
					int yvalueInter2 = 0;
					
					//Loop to guarantee it draws all the lines used in the shortest path
					while (!current.equals(previous)) {
						
						//Scale the X and Y values to the windowSize
						xvalueInter1 = (int) (windowSize-(current.x - smallestX) * scalingValue);
						yvalueInter1 = (int) ((current.y - smallestY) * scalingValue);
			
						yvalueInter2 = (int) ((previous.y - smallestY) * scalingValue);
						xvalueInter2 = (int) (windowSize-(previous.x - smallestX) * scalingValue);
						
						//Draw multiple lines to make it a thicker line easily.
						g.drawLine(yvalueInter1, xvalueInter1, yvalueInter2, xvalueInter2);
						g.drawLine(yvalueInter1, xvalueInter1+1, yvalueInter2, xvalueInter2+1);
						g.drawLine(yvalueInter1, xvalueInter1-1, yvalueInter2, xvalueInter2-1);
						g.drawLine(yvalueInter1+1, xvalueInter1, yvalueInter2+1, xvalueInter2);
						g.drawLine(yvalueInter1-1, xvalueInter1, yvalueInter2-1, xvalueInter2);
						
						//Continue progressing backwards in the algorithm and printing previous parts of the shortest
						//Path.
						current = previous;
						previous = current.fromIntersection;
					}
				}
			}
		}
		
		
		//--------------------------------------------
		//Minimum Weight Spanning Tree
		//--------------------------------------------
		if (meridianmap) {
			//The method is called betterMinimumWeightSpanningTree because I have another algorithm as well, Prim's.
			//However, this one I was able to create as much more efficient then it, but I kept the old one for some reason.
			graph.betterMinimumWeightSpanningTree();
			//graph.minimumWeightSpanningTree();
			ListIterator<Road> listIterator = graph.minTree.listIterator();
			
			//Print the roads in the minimum weight spanning tree
			System.out.println("Roads in minimum weight spanning tree:");
			while (listIterator.hasNext()) {
				Road road = listIterator.next();
				System.out.println(road.name);
			}
		}
		
		if (show && meridianmap) {
			Graphics g = frame.getGraphics();
			ListIterator<Road> listIterator = graph.minTree.listIterator();
			//Show the Minimum Weight Spanning Tree by Iterating through the roads and drawing them in Magenta
			while (listIterator.hasNext()) {
				Road road = listIterator.next();
				
				int xvalueInter1 = 0;
				int yvalueInter1 = 0;
				int xvalueInter2 = 0;
				int yvalueInter2 = 0;
				
				//Scale the x and y values accordingly to the windowSize
				xvalueInter1 = (int) (windowSize-(road.roadFirst.x - smallestX) * scalingValue);
				yvalueInter1 = (int) ((road.roadFirst.y - smallestY) * scalingValue);
	
				yvalueInter2 = (int) ((road.roadSecond.y - smallestY) * scalingValue);
				xvalueInter2 = (int) (windowSize-(road.roadSecond.x - smallestX) * scalingValue);
				
				//Draw the line in Magenta (Slightly thicker)
				g.setColor(Color.MAGENTA);
				g.drawLine(yvalueInter1, xvalueInter1, yvalueInter2, xvalueInter2);
				g.drawLine(yvalueInter1, xvalueInter1+1, yvalueInter2, xvalueInter2+1);
				g.drawLine(yvalueInter1, xvalueInter1-1, yvalueInter2, xvalueInter2-1);
				g.drawLine(yvalueInter1+1, xvalueInter1, yvalueInter2+1, xvalueInter2);
				g.drawLine(yvalueInter1-1, xvalueInter1, yvalueInter2-1, xvalueInter2);
				
			}
		}
		
		
	}
	
	//------------------------------------------------
	// Prints path of Djikstra's Algorithm
	//------------------------------------------------
	public static String whereFrom(String baseString, Intersection inter) {
		if (inter.equals(inter.fromIntersection)) {
			return baseString + inter.name;
		}
		baseString += whereFrom(baseString, inter.fromIntersection) + "->";
		return baseString + inter.name;
	}
}
