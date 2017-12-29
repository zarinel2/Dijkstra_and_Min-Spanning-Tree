//Author: William Tyler Wilson
package CSCProject4;

import java.util.Comparator;

public class EdgeCompare implements Comparator<Road>{
	
	@Override
	public int compare(Road arg0, Road arg1) {
		
		return (arg0.length < arg1.length) ? -1:1;
	}
	
}
