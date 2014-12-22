package com.gautam.Utilities;

import java.util.Comparator;
import java.util.Map;

public class MapValueComparator implements Comparator<String>{
	Map<String, Double> base;
	public MapValueComparator(Map<String, Double> base){
		this.base = base;
	}
	public int compare(String o1, String o2) {
		if (base.get(o1) >= base.get(o2)) {
            return -1;
        } else {
            return 1;
        }
	}
	
}
