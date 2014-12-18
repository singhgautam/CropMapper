package com.gautam.CropMapper;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class App 
{
    public static void main( String[] args )
    {
        Sparql enDBpedia = new Sparql("http://dbpedia.org/sparql");
        
        //String predicateURI = "http://ko.dbpedia.org/property/작곡";
        //String resourceURI = "http://ko.dbpedia.org/resource/Stratovarius";
        
        /*
        //Test :: predicateToSubjectObject()
        Map<String, String> outMap = enDBpedia.predicateToSubjectObject(predicateURI);
        for(Entry<String, String> thisEntry : outMap.entrySet()){
        	System.out.println(thisEntry.getKey()+" :: "+thisEntry.getValue());
        }
        System.out.println("--");
        
        //Test :: getAllClassFor()
        List<String> classList = enDBpedia.getAllClassFor(resourceURI);
        for(String thisClass : classList){
        	System.out.println(thisClass);
        }
        System.out.println("--");
        
        //Test :: getClassFor()
        classList = enDBpedia.getClassFor(resourceURI);
        for(String thisClass : classList){
        	System.out.println(thisClass);
        }
        */
        
        //Test: probPropGProp()
        System.out.println(enDBpedia.probPropGProp("http://dbpedia.org/ontology/deathPlace", "http://dbpedia.org/ontology/birthPlace"));
    }
}
