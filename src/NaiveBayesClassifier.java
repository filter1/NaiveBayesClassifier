import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;


public class NaiveBayesClassifier {
	
	HashMap<String, Float> priorProbability = new HashMap<String, Float>();
	
	HashMap<String, HashMap<String, Float>> buyingProbability = new HashMap<String, HashMap<String, Float>>();
	HashMap<String, HashMap<String, Float>> maintProbability = new HashMap<String, HashMap<String, Float>>();
	HashMap<String, HashMap<String, Float>> doorsProbability = new HashMap<String, HashMap<String, Float>>();
	HashMap<String, HashMap<String, Float>> personsProbability = new HashMap<String, HashMap<String, Float>>();
	HashMap<String, HashMap<String, Float>> lugboatProbability = new HashMap<String, HashMap<String, Float>>();
	HashMap<String, HashMap<String, Float>> safetyProbability = new HashMap<String, HashMap<String, Float>>();
	
	public void addAttribute(HashMap<String, HashMap<String, Float>> map, String targetClass, String attributeValue){
		HashMap<String, Float> pAttributeTarget = map.get(targetClass);
		if (pAttributeTarget == null) {
			pAttributeTarget = new HashMap<String, Float>();
			map.put(targetClass, pAttributeTarget);
		}
		Float pAttributeValue = pAttributeTarget.getOrDefault(attributeValue, 0f);
		pAttributeTarget.put(attributeValue, pAttributeValue + 1);
	}
	
	public void fixAttributeProbability(HashMap<String, HashMap<String, Float>> map, String targetClass, float n){
		HashMap<String, Float> pAttributeTarget = map.get(targetClass);
		
		if (pAttributeTarget != null) {
			for (String x: pAttributeTarget.keySet()) {
				Float f = pAttributeTarget.get(x);
				pAttributeTarget.put(x, f / n);
			}
		}
	}
	
	public NaiveBayesClassifier(ArrayList<TrainingDataItem> items){
		
		// 1. getting occurrences
		int n = 0;
		for (TrainingDataItem item:items){
			
			String targetClass = item.getTargetClass();
			Float t = priorProbability.getOrDefault(targetClass, 0f);
			priorProbability.put(targetClass, t + 1);
			n++;
			
			addAttribute(buyingProbability, targetClass, item.getBuying());
			addAttribute(maintProbability, targetClass, item.getMaint());
			addAttribute(doorsProbability, targetClass, item.getDoors());
			addAttribute(personsProbability, targetClass, item.getPersons());
			addAttribute(lugboatProbability, targetClass, item.getLug_boot());
			addAttribute(safetyProbability, targetClass, item.getSafety());
		}
		
		// 2. calculating the probabilities with the help of the different number of occurrences
		for (String targetClass: priorProbability.keySet()){
			fixAttributeProbability(buyingProbability, targetClass, priorProbability.get(targetClass));
			fixAttributeProbability(maintProbability, targetClass, priorProbability.get(targetClass));
			fixAttributeProbability(doorsProbability, targetClass, priorProbability.get(targetClass));
			fixAttributeProbability(personsProbability, targetClass, priorProbability.get(targetClass));
			fixAttributeProbability(lugboatProbability, targetClass, priorProbability.get(targetClass));
			fixAttributeProbability(safetyProbability, targetClass, priorProbability.get(targetClass));
			
			priorProbability.put(targetClass, priorProbability.get(targetClass) / n);
		}
	}
	
	public String classify(TrainingDataItem item) {
		String best = null;
		float bestF = -1;
		
		// all targets
		for(String key: priorProbability.keySet()){
			float p;
			try{
				Float defaultValue = 0f;
				
				Float pBuy = buyingProbability.get(key).getOrDefault(item.getBuying(), defaultValue);
				float pMaint = maintProbability.get(key).getOrDefault(item.getMaint(), defaultValue);
				float pDoors = doorsProbability.get(key).getOrDefault(item.getDoors(), defaultValue);
				float pPersons = personsProbability.get(key).getOrDefault(item.getPersons(), defaultValue);
				float pLugboat = lugboatProbability.get(key).getOrDefault(item.getLug_boot(), defaultValue);
				float pSafety = safetyProbability.get(key).getOrDefault(item.getSafety(), defaultValue);
				
				p = priorProbability.get(key) * pBuy * pMaint * pDoors * pPersons * pLugboat * pSafety;
			} catch (Exception e){
				p = 0;
				System.out.println("LALA");
			}
//			System.out.println(p);
			if (p > bestF) {
				bestF = p;
				best = key;
			}
		}
		return best;
	}
	
	public void print(){
		for (String key : priorProbability.keySet()){
			System.out.println(key + " " + priorProbability.get(key));
		}
		
		for (String k2 : buyingProbability.keySet()){
			for (String k3 : buyingProbability.get(k2).keySet()) {
				System.out.println(k3 + "|" + k2 + ":" + buyingProbability.get(k2).get(k3));
			}
		}
		
		for (String k2 : maintProbability.keySet()){
			for (String k3 : maintProbability.get(k2).keySet()) {
				System.out.println(k3 + "|" + k2 + ":" + maintProbability.get(k2).get(k3));
			}
		}

		for (String k2 : doorsProbability.keySet()){
			for (String k3 : doorsProbability.get(k2).keySet()) {
				System.out.println(k3 + "|" + k2 + ":" + doorsProbability.get(k2).get(k3));
			}
		}
		
		for (String k2 : personsProbability.keySet()){
			for (String k3 : personsProbability.get(k2).keySet()) {
				System.out.println(k3 + "|" + k2 + ":" + personsProbability.get(k2).get(k3));
			}
		}

		for (String k2 : lugboatProbability.keySet()){
			for (String k3 : lugboatProbability.get(k2).keySet()) {
				System.out.println(k3 + "|" + k2 + ":" + lugboatProbability.get(k2).get(k3));
			}
		}
		
		for (String k2 : safetyProbability.keySet()){
			for (String k3 : safetyProbability.get(k2).keySet()) {
				System.out.println(k3 + "|" + k2 + ":" + safetyProbability.get(k2).get(k3));
			}
		}
		
	}

	public static void main(String[] args) {
		
		// 1. reading data from file
		ArrayList<TrainingDataItem> items = new  ArrayList<TrainingDataItem>();

		File file = new File("car.data");
	    BufferedReader reader = null;
	       
	    try {
	        reader = new BufferedReader(new FileReader(file));
	        String s = null;

	        do{
	        	s = reader.readLine();
				if( s != null) {
					TrainingDataItem i = new TrainingDataItem(s);
					items.add(i);
				}
	        }while(s != null && s != "");
	        
	    } catch (FileNotFoundException e) {
	        e.printStackTrace();
	    } catch (IOException e) {
	        e.printStackTrace();
	    } finally {
	        try {
	            if (reader != null) {
	                reader.close();
	            }
	        } catch (IOException e) {
	            e.printStackTrace();
	        }
	    }
	    
	    NaiveBayesClassifier nbc = new NaiveBayesClassifier(items);
	    nbc.print();
	    
    	int s = 0;
    	int f = 0;
//    	System.out.println(nbc.classify(items.get(2)));
	    for(TrainingDataItem item: items) {
	    	String c = nbc.classify(item);
	    	String d = item.getTargetClass();	

	    	if( c.equals(d) ) {
//	    		System.out.println("success");
	    		s++;
	    	}
	    	else {
//	    		System.out.println("Fail");
		    	System.out.println(s+ " " +c + " " + d);
		    	f++;

	    	}
	    }
	    System.out.println((float) s / (s+f) * 100 + "% Accuracy");
 	    
	}
}
