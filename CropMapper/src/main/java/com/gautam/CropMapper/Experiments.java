package com.gautam.CropMapper;

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
}
