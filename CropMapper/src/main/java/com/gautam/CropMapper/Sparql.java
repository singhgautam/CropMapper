package com.gautam.CropMapper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;

public class Sparql {

	String PREFIX = 
		"PREFIX dbpedia-owl: <http://dbpedia.org/ontology/> " +
		"PREFIX owl: <http://www.w3.org/2002/07/owl#> " +
		"PREFIX xsd: <http://www.w3.org/2001/XMLSchema#> " +
		"PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> " +
		"PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> " +
		"PREFIX foaf: <http://xmlns.com/foaf/0.1/> " + 
		"PREFIX dc: <http://purl.org/dc/elements/1.1/> " +
		"PREFIX : <http://dbpedia.org/resource/> " +
		"PREFIX dbpedia2: <http://dbpedia.org/property/> " +
		"PREFIX dbpedia: <http://dbpedia.org/> " +
		"PREFIX skos: <http://www.w3.org/2004/02/skos/core#> ";
	
	String ENDPOINT;
	
	Sparql(String endpoint){
		ENDPOINT = endpoint;
	}
	
	Map<String, String> predicateToSubjectObject(String predicateURI){
		Map<String, String> thisMap = new HashMap<String, String>();
		String qString = 
				"SELECT DISTINCT ?S ?O "+
				"WHERE "+
				"{ "+
				"?S <"+predicateURI+"> ?O "+
				"} ";
		Query q = QueryFactory.create(PREFIX+qString);
		QueryExecution qExecution = QueryExecutionFactory.sparqlService(ENDPOINT, q);
		ResultSet qResults = qExecution.execSelect();
		while(qResults.hasNext()){
			QuerySolution thisRow = qResults.next();
			String S = thisRow.get("S").toString();
			String O = thisRow.get("O").toString();
			thisMap.put(S, O);
		}
		qExecution.close();
		return thisMap;
	}
	
	List<String> getClassFor(String entity){
		String qString = 
				PREFIX+
				"SELECT DISTINCT ?C "+
				"WHERE "+
				"{ "+
				"<"+entity+"> rdf:type ?C"+
				"} ";
		Query q = QueryFactory.create(PREFIX+qString);
		QueryExecution qExecution = QueryExecutionFactory.sparqlService(ENDPOINT, q);
		ResultSet qResults = qExecution.execSelect();
		List<String> classList = new ArrayList<String>();
		while(qResults.hasNext()){
			QuerySolution thisRow = qResults.next();
			String C = thisRow.get("C").toString();
			classList.add(C);
		}
		qExecution.close();
		return classList;
	}
	
	List<String> getAllClassFor(String entity){
		LinkedList<String> classList = new LinkedList<String>(); 
		List<String> finalList = new ArrayList<String>();
		classList.addAll(getClassFor(entity));
		finalList.addAll(classList);
		while(classList.size()!=0){
			String thisClass = classList.removeFirst();
			List<String> thisClassList = getClassFor(thisClass);
			for(String className : thisClassList){
				if(!finalList.contains(className)){
					classList.add(className);
					finalList.add(className);
				}
			}
		}
		return finalList;
	}	
}

