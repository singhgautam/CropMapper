package com.gautam.CropMapper;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class App 
{
    public static void main( String[] args )
    {
    	
        Experiments dbpedia = new Experiments("http://127.0.0.1:8890/sparql");
        /*
        System.out.println("KOREAN PROPERTY, LIKELY DOMAIN, LIKELY RANGE, LIKELY ENGLISH PROPERTY, SCORE");
        try {
			List<String> allKP = dbpedia.getKoreanObjectPropertyListFromFile("/home/gautam/Research/Predicate_Mapping/Data/property_list_clean.csv", 100);
			int count = 0;
			for(String thisKP : allKP){
				if(count < 15) {
					count++;
					continue;
				}
				System.out.println(thisKP+",-,-,-,-");
				Map<String, Double> domainRangeTargetMap = dbpedia.predicateToTargetDomainRangeWeightedTop10(thisKP,"http://127.0.0.1:8890/sparql");
		        for(Entry<String, Double> thisEntry: domainRangeTargetMap.entrySet()){
		        	System.out.println("-,"+thisEntry.getKey().replaceAll("<", "").replaceAll(">", ",")+thisEntry.getValue());
		        }
		        count++;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
        /*
        try {
			List<String> allKP = dbpedia.getKoreanObjectPropertyListFromFile("/home/gautam/Research/Predicate_Mapping/Data/property_list_clean.csv", 100);
			for(String thisKP : allKP){
				System.out.println(thisKP);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
        /*
        try {
			List<String> allKP = dbpedia.getKoreanPropertyListFromFile("/home/gautam/Research/Predicate_Mapping/Data/property_list_clean.csv", 100);
			for(String thisKP : allKP){
				System.out.println(thisKP+" :: "+dbpedia.valueTypePropScore(thisKP, "http://ko.dbpedia.org")*100);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
        
        
        /*
        String predicateURI = "http://ko.dbpedia.org/property/제작";
        Map<String, Integer> domainRangeMap = koDBpedia.predicateToDomainRange(predicateURI);
        for(Entry<String, Integer> thisEntry: domainRangeMap.entrySet()){
        	System.out.println(thisEntry);
        }
        
        */
        String predicateURI = "http://ko.dbpedia.org/property/출연자";
        Map<String, Double> domainRangeTargetMap = dbpedia.predicateToTargetDomainRangeWeighted(predicateURI,"http://127.0.0.1:8890/sparql");
        for(Entry<String, Double> thisEntry: domainRangeTargetMap.entrySet()){
        	System.out.println(thisEntry);
        }
        
        /*
        System.out.println(dbpedia.probPropGDomRan("http://ko.dbpedia.org/property/제작","http://dbpedia.org/ontology/Film" , "http://dbpedia.org/ontology/Agent", "http://ko.dbpedia.org"));
        
        
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
