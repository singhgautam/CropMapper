package com.gautam.CropMapper;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.Literal;

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
	
	double probPropGProp(String p1, String p2){
		int C_12 = 0; int C_2 = 0;
		String qString = 
				"SELECT (COUNT(?S) AS ?C) "+
				"WHERE "+
				"{ "+
				"?S <"+p1+"> ?O ."+
				"?S <"+p2+"> ?O ."+
				"} ";
		Query q = QueryFactory.create(PREFIX+qString);
		QueryExecution qExecution = QueryExecutionFactory.sparqlService(ENDPOINT, q);
		ResultSet qResults = qExecution.execSelect();
		if(qResults.hasNext()){
			QuerySolution thisRow = qResults.next();
			Literal C_12_literal = ((Literal) thisRow.get("C"));
			C_12 = C_12_literal.getInt();
		}
		qExecution.close();
		qString = 
				"SELECT (COUNT(?S) AS ?C) "+
				"WHERE "+
				"{ "+
				"?S <"+p2+"> ?O ."+
				"} ";
		q = QueryFactory.create(PREFIX+qString);
		qExecution = QueryExecutionFactory.sparqlService(ENDPOINT, q);
		qResults = qExecution.execSelect();
		if(qResults.hasNext()){
			QuerySolution thisRow = qResults.next();
			Literal C_2_literal = ((Literal) thisRow.get("C"));
			C_2 = C_2_literal.getInt();
		}
		qExecution.close();
		return ((double) C_12)/C_2;
	}
	
	Map<String, Double> allPropGProp(String p){
		int C_2 = 0;
		Map<String, Double> probMap = new TreeMap<String, Double>();
		String qString = 
				"SELECT ?P (COUNT(?S) AS ?C) "+
				"WHERE "+
				"{ "+
				"?S ?P ?O . "+
				"?S <"+p+"> ?O ."+
				"} GROUP BY ?P ORDER BY DESC(?C) LIMIT 1000";
		Query q = QueryFactory.create(PREFIX+qString);
		QueryExecution qExecution = QueryExecutionFactory.sparqlService(ENDPOINT, q);
		ResultSet qResults = qExecution.execSelect();
		while(qResults.hasNext()){
			QuerySolution thisRow = qResults.next();
			Literal literal = ((Literal) thisRow.get("C"));
			int thisC = literal.getInt();
			String thisP = thisRow.get("P").toString();
			if(thisP.equalsIgnoreCase(p)){
				C_2 = thisC;
			}else{
				probMap.put(thisP, ((double) thisC)/C_2);
			}
		}
		qExecution.close();
		return probMap;		
	}
	
	Double probPropGDomRan(String p, String dom, String ran, String g){
		String qString = 
				"SELECT (COUNT(?S) AS ?C) "+
				"WHERE "+
				"{ "+
				"?S rdf:type <"+dom+"> . "+
				"?O rdf:type <"+ran+"> . "+
				"?S <"+p+"> ?O . "+
				"} ";
		Query q = QueryFactory.create(PREFIX+qString);
		QueryExecution qExecution = QueryExecutionFactory.sparqlService(ENDPOINT, q);
		ResultSet qResults = qExecution.execSelect();
		Integer numPDR = 0; 
		if(qResults.hasNext()){
			QuerySolution thisRow = qResults.next();
			Literal literal = ((Literal) thisRow.get("C"));
			numPDR = literal.getInt();
		}
		qString = 
				"SELECT (COUNT(?S) AS ?C) "+
				"WHERE "+
				"{ "+
				"?S rdf:type <"+dom+"> . "+
				"?O rdf:type <"+ran+"> . "+
				"?S ?P ?O . "+
				"} ";
		q = QueryFactory.create(PREFIX+qString);
		qExecution = QueryExecutionFactory.sparqlService(ENDPOINT, q);
		qResults = qExecution.execSelect();
		Integer numDR = 1;
		if(qResults.hasNext()){
			QuerySolution thisRow = qResults.next();
			Literal literal = ((Literal) thisRow.get("C"));
			numDR = literal.getInt();
		}
		return (numPDR*1.0)/numDR;
	}
	
	Double probPropGDom(String p, String dom, String g){
		String qString = 
				"SELECT (COUNT(DISTINCT ?S) AS ?C) "+
				"WHERE "+
				"{ "+
				"?S rdf:type <"+dom+"> . "+
				"?S <"+p+"> ?O . "+
				"} ";
		Query q = QueryFactory.create(PREFIX+qString);
		QueryExecution qExecution = QueryExecutionFactory.sparqlService(ENDPOINT, q, g);
		ResultSet qResults = qExecution.execSelect();
		Integer numPDR = 0; 
		if(qResults.hasNext()){
			QuerySolution thisRow = qResults.next();
			Literal literal = ((Literal) thisRow.get("C"));
			numPDR = literal.getInt();
		}
		qExecution.close();
		qString = 
				"SELECT (COUNT(DISTINCT ?S) AS ?C) "+
				"WHERE "+
				"{ "+
				"?S rdf:type <"+dom+"> . "+
				//"?S ?P ?O . "+
				"} ";
		q = QueryFactory.create(PREFIX+qString);
		qExecution = QueryExecutionFactory.sparqlService(ENDPOINT, q, g);
		qResults = qExecution.execSelect();
		Integer numDR = 1;
		if(qResults.hasNext()){
			QuerySolution thisRow = qResults.next();
			Literal literal = ((Literal) thisRow.get("C"));
			numDR = literal.getInt();
		}
		qExecution.close();
		return (numPDR*1.0)/numDR;
	}
	
	Double probPropGRan(String p, String ran, String g){
		String qString = 
				"SELECT (COUNT(DISTINCT ?O) AS ?C) "+
				"WHERE "+
				"{ "+
				"?O rdf:type <"+ran+"> . "+
				"?S <"+p+"> ?O . "+
				"} ";
		Query q = QueryFactory.create(PREFIX+qString);
		QueryExecution qExecution = QueryExecutionFactory.sparqlService(ENDPOINT, q, g);
		ResultSet qResults = qExecution.execSelect();
		Integer numPDR = 0; 
		if(qResults.hasNext()){
			QuerySolution thisRow = qResults.next();
			Literal literal = ((Literal) thisRow.get("C"));
			numPDR = literal.getInt();
		}
		qExecution.close();
		qString = 
				"SELECT (COUNT(DISTINCT ?O) AS ?C) "+
				"WHERE "+
				"{ "+
				"?O rdf:type <"+ran+"> . "+
				//"?S ?P ?O . "+
				"} ";
		q = QueryFactory.create(PREFIX+qString);
		qExecution = QueryExecutionFactory.sparqlService(ENDPOINT, q, g);
		qResults = qExecution.execSelect();
		Integer numDR = 1;
		if(qResults.hasNext()){
			QuerySolution thisRow = qResults.next();
			Literal literal = ((Literal) thisRow.get("C"));
			numDR = literal.getInt();
		}
		qExecution.close();
		return (numPDR*1.0)/numDR;
	}
	
	Double valueTypePropScore(String p, String graph){
		String qString = 
				"SELECT (COUNT(?O) AS ?C) "+
				"WHERE "+
				"{ "+
				"?S <"+p+"> ?O . "+
				"FILTER(isLiteral(?O)) . "+
				"FILTER(datatype(?O) != xsd:string) . "+
				"} ";
		Query q = QueryFactory.create(PREFIX+qString);
		QueryExecution qExecution = QueryExecutionFactory.sparqlService(ENDPOINT, q, graph);
		ResultSet qResults = qExecution.execSelect();
		Integer numVals = 0; 
		if(qResults.hasNext()){
			QuerySolution thisRow = qResults.next();
			Literal literal = ((Literal) thisRow.get("C"));
			numVals = literal.getInt();
		}
		qString = 
				"SELECT (COUNT(?O) AS ?C) "+
				"WHERE "+
				"{ "+
				"?S <"+p+"> ?O . "+
				"} ";
		q = QueryFactory.create(PREFIX+qString);
		qExecution = QueryExecutionFactory.sparqlService(ENDPOINT, q, graph);
		qResults = qExecution.execSelect();
		Integer numAll = 1;
		if(qResults.hasNext()){
			QuerySolution thisRow = qResults.next();
			Literal literal = ((Literal) thisRow.get("C"));
			numAll = literal.getInt();
		}
		qExecution.close();
		return (numVals*1.0)/numAll;
	}
	
	List<String> getKoreanObjectPropertyListFromFile(String file, Integer limit) throws IOException{
		BufferedReader br = new BufferedReader(new FileReader(file));
		String thisProperty;
		List<String> out = new ArrayList<String>();
		int c = 0;
		while((thisProperty = br.readLine())!=null && c != limit){
			if(thisProperty.split("/")[thisProperty.split("/").length-1].replaceAll("[A-Za-z0-9]+", "").length()==0) continue;
			if(valueTypePropScore(thisProperty, "http://ko.dbpedia.org") > 0.25) continue;
			out.add(thisProperty);
			c++;
		}
		return out;
	}
	
	boolean isKoreanProperty(String thisProperty){
		if(thisProperty.split("/")[thisProperty.split("/").length-1].replaceAll("[A-Za-z0-9]+", "").length()==0) return false;
		else return true;
	}
	
}
