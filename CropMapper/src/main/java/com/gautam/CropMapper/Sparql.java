package com.gautam.CropMapper;

import java.util.HashMap;
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
				//PREFIX+
				"SELECT DISTINCT ?S ?O "+
				"WHERE "+
				"{ "+
				"?S <"+predicateURI+"> ?O "+
				"} ";
		Query q = QueryFactory.create(qString);
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
	
}

