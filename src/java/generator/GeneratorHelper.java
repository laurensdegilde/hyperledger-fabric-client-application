package generator;

import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

public class GeneratorHelper {
    private SortedMap<String, Integer[]> steps;
    
    public GeneratorHelper(){
        steps = new TreeMap<>();
        steps.put("Step1", new Integer[]{1, 10});
        steps.put("Step2", new Integer[]{36, 100});
        steps.put("Step3", new Integer[]{101, 1000});
        steps.put("Step4", new Integer[]{1001, 10000});
        steps.put("Step5", new Integer[]{10001, 1000000});
    }
    
    public Map<String, Integer[]> getSteps() {
        return steps;
    }
    
    public Integer [] getStepOffsets(String step){
        return steps.get(step);
    }
}
