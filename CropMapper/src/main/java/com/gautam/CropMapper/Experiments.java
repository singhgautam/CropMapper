package com.gautam.CropMapper;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.gautam.Utilities.*;

public class Experiments extends Sparql {
	Experiments(String endpoint){
		super(endpoint);
	}
	
	Map<String, Integer> predicateToDomainRange(String p){
		Map<String, Integer> out = new HashMap<String, Integer>();
		String qString = 
				"SELECT DISTINCT ?S ?O "+
				"WHERE "+
				"{ "+
				"?S <"+p+"> ?O . "+
				//"?SD owl:sameAs ?S "+
				//"?OD owl:sameAs ?O "+ 
				//"FILTER(STRSTARTS(STR(?S),\"http:\\dbpedia.org\")) "+
				//"FILTER(STRSTARTS(STR(?O),\"http:\\dbpedia.org\")) "+
				"} LIMIT 50";
		Query q = QueryFactory.create(PREFIX+qString);
		QueryExecution qExecution = QueryExecutionFactory.sparqlService(ENDPOINT, q);
		ResultSet qResults = qExecution.execSelect();
		while(qResults.hasNext()){
			QuerySolution thisRow = qResults.next();
			if( thisRow.get("S").isLiteral() || thisRow.get("O").isLiteral() ) continue;
			String subject = thisRow.get("S").toString();
			String object = thisRow.get("O").toString();
			List<String> domains = getClassFor(subject);
			List<String> ranges = getClassFor(object);
			//System.out.println(subject+" || "+object+" \n "+domains+" \n "+ranges);
			System.out.println(subject+" :: "+object);
			for(String domain : domains){
				if(!domain.contains("dbpedia.org")) continue;
				for(String range : ranges){
					if(!range.contains("dbpedia.org")) continue;
					String domainRange = "<"+domain+"><"+range+">";
					int domainRangeCount = 1;
					if(out.containsKey(domainRange)){
						domainRangeCount = out.get(domainRange)+1;
					}
					out.put(domainRange,domainRangeCount);
				}
			}
		}
		return out;
	}
	
	Map<String,Integer> predicateToTargetDomainRange(String p, String te){
		Map<String, Integer> out = new HashMap<String, Integer>();
		Sparql tEndpoint = new Sparql(te);
		String qString = 
				"SELECT DISTINCT ?S ?O "+
				"WHERE "+
				"{ "+
				"?SD <"+p+"> ?OD . "+
				"?SD owl:sameAs ?S . "+
				"?OD owl:sameAs ?O . "+ 
				"FILTER(STRSTARTS(STR(?S),\"http://dbpedia.org\")) . "+
				"FILTER(STRSTARTS(STR(?O),\"http://dbpedia.org\")) . "+
				"} LIMIT 50";
		Query q = QueryFactory.create(PREFIX+qString);
		QueryExecution qExecution = QueryExecutionFactory.sparqlService(ENDPOINT, q);
		ResultSet qResults = qExecution.execSelect();
		while(qResults.hasNext()){
			QuerySolution thisRow = qResults.next();
			String subject = thisRow.get("S").toString();
			String object = thisRow.get("O").toString();
			System.out.println(subject+" :: "+object);
			List<String> domains_target = tEndpoint.getClassFor(subject);
			List<String> ranges_target = tEndpoint.getClassFor(object);
			List<String> predicates_target = new ArrayList<String>();
			String qString_target = 
					"SELECT DISTINCT ?P "+
					"WHERE "+
					"{ "+
					"<"+subject+"> ?P <"+object+"> . "+
					"FILTER(STRSTARTS(STR(?P),\"http://dbpedia.org/ontology\")) "+
					"} ";
			Query q_target = QueryFactory.create(PREFIX+qString_target);
			QueryExecution qExecution_target = QueryExecutionFactory.sparqlService(te, q_target);
			ResultSet qResults_target = qExecution_target.execSelect();
			while(qResults_target.hasNext()){
				QuerySolution thisRow_target = qResults_target.next();
				String thisPredicate_target = thisRow_target.get("P").toString();
				predicates_target.add(thisPredicate_target);
			}
			for(String thisDomain : domains_target){
				if(!thisDomain.contains("dbpedia.org")) continue;
				if(thisDomain.toLowerCase().contains("wiki")) continue;
				for(String thisRange : ranges_target){
					if(!thisRange.contains("dbpedia.org")) continue;
					if(thisRange.toLowerCase().contains("wiki")) continue;
					for(String thisPredicate_target : predicates_target){
						if(!thisPredicate_target.contains("dbpedia.org")) continue;
						if(thisPredicate_target.toLowerCase().contains("wiki")) continue;
						String thisKey = "<"+thisDomain+"><"+thisRange+"><"+thisPredicate_target+">";
						System.out.println("  "+thisKey);
						int thisCount = 1;
						if(out.containsKey(thisKey)){
							thisCount = out.get(thisKey)+1;
						}
						out.put(thisKey, thisCount);
					}
				}
			}
		}
		return out;
	}
	
	Map<String,Double> predicateToTargetDomainRangeWeighted(String p, String te){
		Map<String, Double> out_pre = new HashMap<String, Double>();
		Sparql tEndpoint = new Sparql(te);
		String qString = 
				"SELECT DISTINCT ?S ?O "+
				"WHERE "+
				"{ "+
				"?SD <"+p+"> ?OD . "+
				"?SD owl:sameAs ?S . "+
				"?OD owl:sameAs ?O . "+ 
				"FILTER(STRSTARTS(STR(?S),\"http://dbpedia.org\")) . "+
				"FILTER(STRSTARTS(STR(?O),\"http://dbpedia.org\")) . "+
				"} LIMIT 50";
		Query q = QueryFactory.create(PREFIX+qString);
		QueryExecution qExecution = QueryExecutionFactory.sparqlService(ENDPOINT, q);
		ResultSet qResults = qExecution.execSelect();
		while(qResults.hasNext()){
			QuerySolution thisRow = qResults.next();
			String subject = thisRow.get("S").toString();
			String object = thisRow.get("O").toString();
			//System.out.println(subject+" :: "+object);
			List<String> domains_target = tEndpoint.getClassFor(subject);
			List<String> ranges_target = tEndpoint.getClassFor(object);
			List<String> predicates_target = new ArrayList<String>();
			String qString_target = 
					"SELECT DISTINCT ?P "+
					"WHERE "+
					"{ "+
					"<"+subject+"> ?P <"+object+"> . "+
					"FILTER(STRSTARTS(STR(?P),\"http://dbpedia.org/ontology\")) "+
					"} ";
			Query q_target = QueryFactory.create(PREFIX+qString_target);
			QueryExecution qExecution_target = QueryExecutionFactory.sparqlService(te, q_target);
			ResultSet qResults_target = qExecution_target.execSelect();
			while(qResults_target.hasNext()){
				QuerySolution thisRow_target = qResults_target.next();
				String thisPredicate_target = thisRow_target.get("P").toString();
				predicates_target.add(thisPredicate_target);
			}
			Map<String, Double> domWeightMap = new HashMap<String, Double>();
			Map<String, Double> ranWeightMap = new HashMap<String, Double>();
			for(String thisDomain : domains_target){
				if(!thisDomain.contains("dbpedia.org")) continue;
				if(thisDomain.toLowerCase().contains("wiki")) continue;
				for(String thisRange : ranges_target){
					if(!thisRange.contains("dbpedia.org")) continue;
					if(thisRange.toLowerCase().contains("wiki")) continue;
					for(String thisPredicate_target : predicates_target){
						if(!thisPredicate_target.contains("dbpedia.org")) continue;
						if(thisPredicate_target.toLowerCase().contains("wiki")) continue;
						String thisKey = "<"+thisDomain+"><"+thisRange+"><"+thisPredicate_target+">";
						//String thisKey = "<"+thisPredicate_target+">";
						String domKey = "<"+thisDomain+"><"+p+">";
						String ranKey = "<"+thisRange+"><"+p+">";
						Double domWeight; Double ranWeight;
						if(!domWeightMap.containsKey(domKey)){
							domWeight = probPropGDom(p, thisDomain, "http://ko.dbpedia.org");
							domWeightMap.put(domKey, domWeight);
						}else{
							domWeight = domWeightMap.get(domKey);
						}
						if(!ranWeightMap.containsKey(ranKey)){
							ranWeight = probPropGRan(p, thisRange, "http://ko.dbpedia.org");
							ranWeightMap.put(ranKey, ranWeight);
						}else{
							ranWeight = ranWeightMap.get(ranKey);
						}
						//Double weight = 1.0 - (1.0 - domWeight)*(1.0 - ranWeight);
						Double weight = domWeight*ranWeight;
						//System.out.println("  "+thisKey);
						Double thisScore = weight;
						if(out_pre.containsKey(thisKey)){
							thisScore = out_pre.get(thisKey)+weight;
						}
						out_pre.put(thisKey, thisScore);
					}
				}
			}
		}
		Map<String, Double> out = Algorithms.sort(out_pre);
		return out;
	}
	
	Map<String,Double> predicateToTargetDomainRangeWeightedTop5(String p, String te){
		Map<String, Double> out_pre = new HashMap<String, Double>();
		Sparql tEndpoint = new Sparql(te);
		String qString = 
				"SELECT DISTINCT ?S ?O "+
				"WHERE "+
				"{ "+
				"?SD <"+p+"> ?OD . "+
				"?SD owl:sameAs ?S . "+
				"?OD owl:sameAs ?O . "+ 
				"FILTER(STRSTARTS(STR(?S),\"http://dbpedia.org\")) . "+
				"FILTER(STRSTARTS(STR(?O),\"http://dbpedia.org\")) . "+
				"} LIMIT 200";
		Query q = QueryFactory.create(PREFIX+qString);
		QueryExecution qExecution = QueryExecutionFactory.sparqlService(ENDPOINT, q);
		ResultSet qResults = qExecution.execSelect();
		while(qResults.hasNext()){
			QuerySolution thisRow = qResults.next();
			String subject = thisRow.get("S").toString();
			String object = thisRow.get("O").toString();
			//System.err.println(subject+" :: "+object);
			List<String> domains_target = tEndpoint.getClassFor(subject);
			List<String> ranges_target = tEndpoint.getClassFor(object);
			List<String> predicates_target = new ArrayList<String>();
			String qString_target = 
					"SELECT DISTINCT ?P "+
					"WHERE "+
					"{ "+
					"<"+subject+"> ?P <"+object+"> . "+
					"FILTER(STRSTARTS(STR(?P),\"http://dbpedia.org/\")) "+
					"} ";
			Query q_target = QueryFactory.create(PREFIX+qString_target);
			QueryExecution qExecution_target = QueryExecutionFactory.sparqlService(te, q_target);
			ResultSet qResults_target = qExecution_target.execSelect();
			while(qResults_target.hasNext()){
				QuerySolution thisRow_target = qResults_target.next();
				String thisPredicate_target = thisRow_target.get("P").toString();
				predicates_target.add(thisPredicate_target);
			}
			Map<String, Double> domWeightMap = new HashMap<String, Double>();
			Map<String, Double> ranWeightMap = new HashMap<String, Double>();
			for(String thisDomain : domains_target){
				if(!thisDomain.contains("dbpedia.org")) continue;
				if(thisDomain.toLowerCase().contains("wiki")) continue;
				for(String thisRange : ranges_target){
					if(!thisRange.contains("dbpedia.org")) continue;
					if(thisRange.toLowerCase().contains("wiki")) continue;
					for(String thisPredicate_target : predicates_target){
						if(!thisPredicate_target.contains("dbpedia.org")) continue;
						if(thisPredicate_target.toLowerCase().contains("wiki")) continue;
						if(isKoreanProperty(thisPredicate_target)) continue;
						String thisKey = "<"+thisDomain+"><"+thisRange+"><"+thisPredicate_target+">";
						//String thisKey = "<"+thisPredicate_target+">";
						String domKey = "<"+thisDomain+"><"+p+">";
						String ranKey = "<"+thisRange+"><"+p+">";
						Double domWeight; Double ranWeight;
						if(!domWeightMap.containsKey(domKey)){
							domWeight = probPropGDom(p, thisDomain, "http://ko.dbpedia.org");
							domWeightMap.put(domKey, domWeight);
						}else{
							domWeight = domWeightMap.get(domKey);
						}
						if(!ranWeightMap.containsKey(ranKey)){
							ranWeight = probPropGRan(p, thisRange, "http://ko.dbpedia.org");
							ranWeightMap.put(ranKey, ranWeight);
						}else{
							ranWeight = ranWeightMap.get(ranKey);
						}
						//Double weight = 1.0 - (1.0 - domWeight)*(1.0 - ranWeight);
						Double weight = domWeight*ranWeight;
						if(Double.isNaN(weight)) weight = 0.0;
						//System.out.println("  "+thisKey);
						Double thisScore = weight;
						if(out_pre.containsKey(thisKey)){
							thisScore = out_pre.get(thisKey)+weight;
						}
						out_pre.put(thisKey, thisScore);
					}
				}
			}
			qExecution_target.close();
		}
		qExecution.close();
		Map<String, Double> out_sort = Algorithms.sort(out_pre);
		Map<String, Double> out_5 = new LinkedHashMap<String, Double>();
		int c = 0;
		for(Entry<String, Double> thisEntry : out_sort.entrySet()){
			if(c >= 5) break;
			out_5.put(thisEntry.getKey(), thisEntry.getValue());
			c++;
		}
		return (Map<String, Double>) out_5;
	}
	
	List<String> getKoreanPropertyListFromFile(String file, Integer limit) throws IOException{
		BufferedReader br = new BufferedReader(new FileReader(file));
		String thisProperty;
		List<String> out = new ArrayList<String>();
		int c = 0;
		while((thisProperty = br.readLine())!=null && c != limit){
			if(thisProperty.split("/")[thisProperty.split("/").length-1].replaceAll("[A-Za-z0-9]+", "").length()==0) continue;
			out.add(thisProperty);
			c++;
		}
		return out;
	}
}
