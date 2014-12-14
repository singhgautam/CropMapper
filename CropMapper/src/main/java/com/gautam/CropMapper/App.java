package com.gautam.CropMapper;

import java.util.Map;
import java.util.Map.Entry;

public class App 
{
    public static void main( String[] args )
    {
        Sparql enDBpedia = new Sparql("http://ko.dbpedia.org/sparql");
        String predicateURI = "http://ko.dbpedia.org/property/작곡";
        Map<String, String> outMap = enDBpedia.predicateToSubjectObject(predicateURI);
        for(Entry<String, String> thisEntry : outMap.entrySet()){
        	System.out.println(thisEntry.getKey()+" :: "+thisEntry.getValue());
        }
    }
}
