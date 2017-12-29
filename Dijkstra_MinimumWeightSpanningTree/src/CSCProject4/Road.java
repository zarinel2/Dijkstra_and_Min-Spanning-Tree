//Author: William Tyler Wilson
package CSCProject4;

//------------------------------------------------------------
//This is the Road Class, containing two intersections that it
//links between
//------------------------------------------------------------
public class Road {
	
	//Base variables of a road
	public String name;
	public Intersection roadFirst = null;
	public Intersection roadSecond = null;
	public double length = 0;
	
	//'used' is for use in creating minimum weight spanning trees
	public boolean used = false;
	
	//Road constructor
	public Road(String name1) {
		name = name1;
		
	}
	
	//These methods set the roads
	public void setRoadOne(Intersection roadFirst1) {
		roadFirst = roadFirst1;
	}
	public void setRoadTwo(Intersection roadSecond1) {
		roadSecond = roadSecond1;
	}
	
	//This method checks to see if the roads have been set
	public boolean roadBeenSet() {
		if (roadFirst != null && roadSecond != null) {
			return true;
		}
		return false;
	}
}
