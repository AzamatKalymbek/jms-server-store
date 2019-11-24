package kz.teamvictus.store.core.util.functions;


import kz.teamvictus.store.core.util.models.Data;
import kz.teamvictus.store.core.util.models.DistanceParameter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static kz.teamvictus.store.core.util.constance.CoreConstance.clusterCenterKey;
import static kz.teamvictus.store.core.util.constance.CoreConstance.clusterObjectsKey;


public class DisplayFunctions {

    public static void printResult14D(List<HashMap<String, Object>> clusters){
        for(HashMap<String, Object> classEntity : clusters) {
            System.out.println("Кластер ::" + (clusters.indexOf(classEntity) + 1));
            System.out.println("Центр   ::" + classEntity.get(clusterCenterKey).toString());
            List<Data> clusterObjects = (List<Data>) classEntity.get(clusterObjectsKey);
            for(int i = 0; i < clusterObjects.size(); i++){
//                System.out.println((i + 1) + ") " + clusterObjects.get(i).toString());
               System.out.println(clusterObjects.get(i).toStringForCoord());
            }
            System.out.println("=========================================");
        }
    }

    public static void printData(List<Data> data){
        for(Data clusterObject: data){
//            System.out.println((data.indexOf(clusterObject) + 1) + ") " + clusterObject.toString());
            System.out.println(clusterObject.toStringForCoord());
        }
        System.out.println("======================================");
    }

    public static void printData(Map<Integer, Double> qualityFunctional){
        for(int i = 0; i < qualityFunctional.size(); i++) {
            System.out.println(qualityFunctional.get(i));
        }
        System.out.println("======================================");
    }

    public static void printDistanceParameter(List<DistanceParameter> distanceParameters){
        for(DistanceParameter distanceParameter: distanceParameters){
//            System.out.println((data.indexOf(clusterObject) + 1) + ") " + clusterObject.toString());
            System.out.println(distanceParameter.toString());
        }
        System.out.println("======================================");
    }

    public static void printDistance(Double[] distanceList){
        for(Double distance: distanceList){
//            System.out.println((data.indexOf(clusterObject) + 1) + ") " + clusterObject.toString());
            System.out.println(distance);
        }
        System.out.println("======================================");
    }
}
