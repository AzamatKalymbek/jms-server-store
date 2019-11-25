package kz.teamvictus.store.core.service.impl;

import kz.teamvictus.store.core.service.IKmeanService;
import kz.teamvictus.store.core.service.IReduceService;
import kz.teamvictus.store.core.util.functions.CommonFunctions;
import kz.teamvictus.store.core.util.functions.FillFunctions;
import kz.teamvictus.store.core.util.models.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

import static kz.teamvictus.store.core.util.constance.CoreConstance.clusterCenterKey;
import static kz.teamvictus.store.core.util.constance.CoreConstance.clusterObjectsKey;


@Service
public class ReduceService implements IReduceService {
    private static final Logger logger = LoggerFactory.getLogger(ReduceService.class);
    // object list
    private List<Data> dataList = FillFunctions.fillDataListFromTxtFile();
    private List<HashMap<String, Object>> clusters;
    // Кол-во классов
    private Integer L_CURRENT = 6, L_REQUIRED = 4;
    private Map<Integer, Double> qualityFunctional = new HashMap<Integer, Double>();
    private Double initialQF = 0.0;

    @Autowired
    private IKmeanService kmeanService;

    @Override
    public List<HashMap<String, Object>> start(Boolean customInput, List<Data> zeroList, Integer initialValue, Integer reduceValue){
        Scanner input = new Scanner(System.in);
        // fill params by default value
        if(customInput){
            clusters = kmeanService.start(null, false, 3, 50);
            System.out.println("Required cluster size: ");
            L_REQUIRED = input.nextInt();
            while(L_REQUIRED >= clusters.size()){
                System.out.println("Invalid cluster size. Must be greater than existing cluster size. Type again: ");
                L_REQUIRED = input.nextInt();
            }
        }else{
            if(zeroList != null){
                clusters = kmeanService.start(zeroList, false, 3, 50);
            }else{
                clusters = kmeanService.startWithRandomCenter(initialValue, 3, 50);
            }
            L_REQUIRED = reduceValue;
        }

        while(L_REQUIRED < clusters.size()){
            initialQF = getQualityFunctional(clusters);
            for(int i = 0; i < clusters.size(); i++){
                List<HashMap<String, Object>> tempClusters = copy();
                tempClusters.remove(i);
                tempClusters = nearestNeighbor(tempClusters);
                qualityFunctional.put(i, Math.abs(initialQF - getQualityFunctional(tempClusters)));
                tempClusters.clear();
            }

//            System.out.println("qualityFunctional");
//            DisplayFunctions.printData(qualityFunctional);
            clusters.remove(CommonFunctions.getMinValueIndex(qualityFunctional));
            clusters = nearestNeighbor(clusters);
            qualityFunctional = new HashMap<>();
        }

        return clusters;
    }

    private List<HashMap<String, Object>> copy() {
        List<HashMap<String, Object>> tempClusters = new ArrayList<>();
        for(HashMap<String, Object> tCluster: clusters){
            List<Data> newData = new ArrayList<>();
            newData.addAll((List<Data>) tCluster.get(clusterObjectsKey));

            HashMap<String, Object> hashMap = new HashMap<>();
            hashMap.put(clusterCenterKey,  tCluster.get(clusterCenterKey));
            hashMap.put(clusterObjectsKey, newData);
            tempClusters.add(hashMap);
        }
        return tempClusters;
    }

    private List<HashMap<String, Object>>
    nearestNeighbor(List<HashMap<String, Object>> tempClusters){
        for(Data data: dataList){
            Double[] distance = new Double[tempClusters.size()];

            for(HashMap<String, Object> tCluster : tempClusters) {
                Data classCenter = (Data) tCluster.get(clusterCenterKey);
                List<Data> clusterObjects = (List<Data>) tCluster.get(clusterObjectsKey);

                if( !CommonFunctions.checkForEquality(data, classCenter) &&
                        !CommonFunctions.checkIfContain(clusterObjects, data)){
                    distance[tempClusters.indexOf(tCluster)] = CommonFunctions.getDistance(classCenter, data);
                }else{
                    distance = null;
                    break;
                }
            }

            if(distance == null) continue;

            ((List<Data>) tempClusters.get(CommonFunctions.getMinValueIndex(distance)).get(clusterObjectsKey)).add(data);
        }

        return tempClusters;
    }

    public double getQualityFunctional(List<HashMap<String, Object>> tempClusters){
        double qualityFunctional = 0.0;
        for(HashMap<String, Object> cluster : tempClusters){
            List<Data> clusterObjects = (List<Data>) cluster.get(clusterObjectsKey);

            for(int i = 0; i < clusterObjects.size(); i++){
                for (Data clusterObject : clusterObjects) {
                    qualityFunctional += CommonFunctions.getDistance(clusterObjects.get(i), clusterObject);
                }
            }
        }
        return qualityFunctional;
    }
}
