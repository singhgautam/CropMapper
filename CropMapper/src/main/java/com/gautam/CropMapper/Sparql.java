package com.gautam.CropMapper;

import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.query.ResultSetFormatter;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.RDFNode;

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
	
	
	
}

