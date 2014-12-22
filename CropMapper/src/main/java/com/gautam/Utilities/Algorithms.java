package com.gautam.Utilities;

import java.util.Map;
import java.util.TreeMap;

public class Algorithms {
	public static Map<String, Double> sort(Map<String, Double> map){
		MapValueComparator mvc = new MapValueComparator(map);
		TreeMap<String, Double> sorted_map = new TreeMap<String, Double>(mvc);
		sorted_map.putAll(map);
		return (Map<String, Double>) sorted_map;
	}
}
