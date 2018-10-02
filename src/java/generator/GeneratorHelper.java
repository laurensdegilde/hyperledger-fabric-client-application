package generator;

import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

public class GeneratorHelper {
    private SortedMap<String, Integer[]> steps;
    
    public GeneratorHelper(){
        steps = new TreeMap<>();
//        steps.put("Step1", new Integer[]{1, 10});// 500 search operations;
//        steps.put("Step2", new Integer[]{79, 100});
//        steps.put("Step3-1", new Integer[]{190, 200});
//        steps.put("Step3-2", new Integer[]{201, 300});
//        steps.put("Step3-3", new Integer[]{379, 400});
//        steps.put("Step3-4", new Integer[]{467, 500});
//        steps.put("Step3-5", new Integer[]{501, 600});
//        steps.put("Step3-6", new Integer[]{690, 700});
//        steps.put("Step3-7", new Integer[]{775, 800});
        steps.put("Step3-8", new Integer[]{880, 900});
//        steps.put("Step3-9", new Integer[]{901, 1000});
        steps.put("read-keys", new Integer[]{1, 500});
//        steps.put("Step4", new Integer[]{1001, 10000});
//        steps.put("Step5", new Integer[]{10001, 1000000});
    }
    
    public Map<String, Integer[]> getSteps() {
        return steps;
    }
    
    public Integer [] getStepOffsets(String step){
        return steps.get(step);
    }
}
