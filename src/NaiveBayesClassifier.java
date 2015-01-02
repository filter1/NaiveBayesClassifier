import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;


public class NaiveBayesClassifier {
	
	HashMap<String, Float> priorProbability = new HashMap<String, Float>();
	
	// save probabilities as mapping from String to mapping of String to Float
	// Target Class -> (Attribute -> nOccurrence)
	HashMap<String, HashMap<String, Float>> buyingProbability = new HashMap<String, HashMap<String, Float>>();
	HashMap<String, HashMap<String, Float>> maintProbability = new HashMap<String, HashMap<String, Float>>();
	HashMap<String, HashMap<String, Float>> doorsProbability = new HashMap<String, HashMap<String, Float>>();
	HashMap<String, HashMap<String, Float>> personsProbability = new HashMap<String, HashMap<String, Float>>();
	HashMap<String, HashMap<String, Float>> lugboatProbability = new HashMap<String, HashMap<String, Float>>();
	HashMap<String, HashMap<String, Float>> safetyProbability = new HashMap<String, HashMap<String, Float>>();
	
	// increment value of given <attribute,target>
	public void addAttribute(HashMap<String, HashMap<String, Float>> map, String targetClass, String attributeValue){
		HashMap<String, Float> pAttributeTarget = map.get(targetClass);
		if (pAttributeTarget == null) {
			pAttributeTarget = new HashMap<String, Float>();
			map.put(targetClass, pAttributeTarget);
		}
		Float pAttributeValue = pAttributeTarget.getOrDefault(attributeValue, 0f);
		pAttributeTarget.put(attributeValue, pAttributeValue + 1);
	}
	
	// transform occurrences to probabilities
	public void fixAttributeProbability(HashMap<String, HashMap<String, Float>> map, String targetClass, float n){
		HashMap<String, Float> pAttributeTarget = map.get(targetClass);
		
		if (pAttributeTarget != null) {
			for (String x: pAttributeTarget.keySet()) {
				Float f = pAttributeTarget.get(x);
				pAttributeTarget.put(x, f / n);
			}
		}
	}
	
	public NaiveBayesClassifier(List<TrainingDataItem> trainingData){
		
		// traverse all and calculate occurrences
		int n = 0;
		for (TrainingDataItem item:trainingData){	
			String targetClass = item.getTargetClass();
			Float t = priorProbability.getOrDefault(targetClass, 0f);
			priorProbability.put(targetClass, t + 1);
			n++;
			
			// for each attribute: increment
			addAttribute(buyingProbability, targetClass, item.getBuying());
			addAttribute(maintProbability, targetClass, item.getMaint());
			addAttribute(doorsProbability, targetClass, item.getDoors());
			addAttribute(personsProbability, targetClass, item.getPersons());
			addAttribute(lugboatProbability, targetClass, item.getLug_boot());
			addAttribute(safetyProbability, targetClass, item.getSafety());
		}
		
		// transform occurrences to probabilities
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
		
		// traverse over all targets
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
				// should never occur
				p = 0;
			}
			if (p > bestF) {
				bestF = p;
				best = key;
			}
		}
		return best;
	}
	
	// print some debug output
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

	// returns accuracy of classifier
	public double testAgainstTestItems(List<TrainingDataItem> testData){
		int fails = 0;
		for(TrainingDataItem i: testData) {
			if (!classify(i).equals(i.getTargetClass()))
				fails++;
		}
		return 1 - fails / (double) testData.size();
	}
	
	// read data from path
	public static ArrayList<TrainingDataItem> readData(String path){
		ArrayList<TrainingDataItem> items = new  ArrayList<TrainingDataItem>();

		File file = new File(path);
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
	    return items;
	}
	
	public static void main(String[] args) {
		
		// reading data from file
		ArrayList<TrainingDataItem> items = readData("car.data");
		
		int runs = 100;
		double summedError = 0;
		for(int i = 0; i < runs; i++) {

			// shuffle list
			Collections.shuffle(items);
			int split = (int) ( items.size() * 2/3f);
			
			// return first 2/3 to the training data 
			List<TrainingDataItem> trainingData =  items.subList(0, split);
			
			// last 1/3 to test data
			List<TrainingDataItem> testData =  items.subList(split, items.size() - 1);
			
		    // build classifier
		    NaiveBayesClassifier nbc = new NaiveBayesClassifier(trainingData);
		    
		    // test against test data
		    summedError += nbc.testAgainstTestItems(testData);
		}
		
		System.out.println("Summed Mean Error: " + summedError / runs * 100 + "%");
	}
}
