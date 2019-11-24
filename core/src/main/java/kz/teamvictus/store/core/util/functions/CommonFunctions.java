package kz.teamvictus.store.core.util.functions;


import kz.teamvictus.store.core.util.models.Data;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static kz.teamvictus.store.core.util.constance.CoreConstance.clusterObjectsKey;

public class CommonFunctions {

    public static int getRandomIntegerBetweenRange(double min, double max){
        return (int) ((int)(Math.random()*((max-min)+1))+min);
    }

    public static Boolean check(Integer[] arr, int toCheckValue) {
        for (Integer element : arr)
            if (element != null && element == toCheckValue)
                return true;
        return false;
    }

    public static double getDistance(Data data1, Data data2){
        // формула для нахождение расстояние между объектами
        // sqrt(pow((x1 - x2), 2) + pow((y1 - y2), 2))
        double distance = 0;
        for(int i = 0; i < data1.getAttributes().size(); i++){
            distance += Math.pow((data2.getAttributes().get(i) - data1.getAttributes().get(i)), 2);
        }
        return Math.sqrt(distance);
    }

    public static boolean checkForEquality(Data data1, Data data2){
        for(int attrIndex = 0; attrIndex < data1.getAttributes().size(); attrIndex++){
            if(!data1.getAttributes().get(attrIndex).equals(data2.getAttributes().get(attrIndex))){
                return false;
            }
        }
        return true;
    }

    public static boolean checkIfContain(final List<Data> clusterObjects, final Data data) {
        for(Data cluster: clusterObjects){
            if(CommonFunctions.checkForEquality(cluster, data)){
                return true;
            }
        }
        return false;
    }

    //Find minimum (lowest) value in array using loop
    public static int getMinValueIndex(Double[] numbers){
        double minValue = numbers[0];
        int index = 0;
        for(int i = 1; i < numbers.length; i++){
            if(numbers[i] < minValue){
                minValue = numbers[i];
                index = i;
            }
        }
        return index;
    }

    public static int getMaxValueIndex(Double[] numbers){
        double maxValue = numbers[0];
        int index = 0;
        for(int i = 1; i < numbers.length; i++){
            if(numbers[i] > maxValue){
                maxValue = numbers[i];
                index = i;
            }
        }
        return index;
    }

    public static int getMinValueIndex(Map<Integer, Double> numbers){
        double minValue = numbers.get(0);
        int index =  0;
        for(int i = 1; i < numbers.size(); i++){
            if(numbers.get(i) < minValue){
                minValue = numbers.get(i);
                index = i;
            }
        }
        return index;
    }

    public static String firstCentroid = "d1";
    public static String secondCentroid = "d2";
    public static Map<String, Integer> findMax(double[][] mat) {

        // Initializing max element as INT_MIN
        double maxElement = Integer.MIN_VALUE;
        int d1 = 0, d2 = 0;

        // checking each element of matrix
        // if it is greater than maxElement,
        // update maxElement
        for (int i = 0; i < mat.length; i++) {
            for (int j = 0; j < mat[i].length; j++) {
                if (mat[i][j] > maxElement) {
                    maxElement = mat[i][j];
                    d1 = i; d2 = j;
                }
            }
        }

        Map<String, Integer> map = new HashMap<>();
        map.put(firstCentroid,  d1);
        map.put(secondCentroid, d2);

        // finally return maxElement
        return map;
    }
    public static Data nearestObject(HashMap<String, Object> cluster, Data newValue){
        List<Data> clusterObjects = (List<Data>) cluster.get(clusterObjectsKey);
        Double[] distanceList = new Double[clusterObjects.size()];

        for(int lIter = 0; lIter < clusterObjects.size(); lIter++){
            double distance = 0.0;
            for (int i = 0; i < clusterObjects.get(lIter).getAttributes().size(); i++) {
                distance += Math.pow((newValue.getAttributes().get(i) - clusterObjects.get(lIter).getAttributes().get(i)), 2);
            }
            distanceList[lIter] = Math.sqrt(distance);
        }

        int minDistance = CommonFunctions.getMinValueIndex(distanceList);
        return clusterObjects.get(minDistance);
    }
}
