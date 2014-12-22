package com.gautam.CropMapper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;

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
}
