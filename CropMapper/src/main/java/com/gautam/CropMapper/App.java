package com.gautam.CropMapper;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class App 
{
    public static void main( String[] args )
    {
        Sparql enDBpedia = new Sparql("http://127.0.0.1:8890/sparql");
        Experiments koDBpedia = new Experiments("http://127.0.0.1:8890/sparql");
//        String predicateURI = "http://ko.dbpedia.org/property/제작";
//        Map<String, Integer> domainRangeMap = koDBpedia.predicateToDomainRange(predicateURI);
//        for(Entry<String, Integer> thisEntry: domainRangeMap.entrySet()){
//        	System.out.println(thisEntry);
//        }
        
        String predicateURI = "http://ko.dbpedia.org/property/제작";
        Map<String, Integer> domainRangeTargetMap = koDBpedia.predicateToTargetDomainRange(predicateURI,"http://127.0.0.1:8890/sparql");
        for(Entry<String, Integer> thisEntry: domainRangeTargetMap.entrySet()){
        	System.out.println(thisEntry);
        }
//        
        /*
        //Test :: predicateToSubjectObject()
        Map<String, String> outMap = enDBpedia.predicateToSubjectObject(predicateURI);
        for(Entry<String, String> thisEntry : outMap.entrySet()){
        	System.out.println(thisEntry.getKey()+" :: "+thisEntry.getValue());
        }
        System.out.println("--");
        
        
        //Test :: getAllClassFor()
        String resourceURI = "http://ko.dbpedia.org/resource/아메아토";
        List<String> classList = koDBpedia.getAllClassFor(resourceURI);
        for(String thisClass : classList){
        	System.out.println(thisClass);
        }
        System.out.println("--");
        
       
        //Test :: getClassFor()
        List<String> classList = enDBpedia.getClassFor("http://dbpedia.org/ontology/Airline");
        for(String thisClass : classList){
        	//System.out.println(thisClass);
        }
        
        
        //Test: probPropGProp()
        //System.out.println(enDBpedia.probPropGProp("http://dbpedia.org/ontology/deathPlace", "http://dbpedia.org/ontology/birthPlace"));
        
        //Test: allPropGProp()
        for(Entry<String, Double> thisEntry : enDBpedia.allPropGProp("http://dbpedia.org/property/color").entrySet()){
        	//System.out.println(thisEntry.getValue()+", ");
        	System.out.println(thisEntry);
        }
        */
    }
}
