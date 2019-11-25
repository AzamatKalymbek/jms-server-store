package kz.teamvictus.store.core.service.impl;

import kz.teamvictus.store.core.service.IKmeanService;
import kz.teamvictus.store.core.service.IReduceService;
import kz.teamvictus.store.core.util.functions.CommonFunctions;
import kz.teamvictus.store.core.util.functions.DisplayFunctions;
import kz.teamvictus.store.core.util.functions.FillFunctions;
import kz.teamvictus.store.core.util.models.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static kz.teamvictus.store.core.util.constance.CoreConstance.clusterCenterKey;
import static kz.teamvictus.store.core.util.constance.CoreConstance.clusterObjectsKey;


@Service
public class KmeanService implements IKmeanService {
    private static final Logger logger = LoggerFactory.getLogger(KmeanService.class);

    // Исходные данные
    private List<Data> dataList;
    private List<Data> zeroLists;
    // Массив центров классов (содержить в себе индексы внесенный пользователем)
    private Integer[] centerIndexes;
    private List<HashMap<String, Object>> clusters = new ArrayList<>();

    @Autowired
    private IReduceService iReduceService;

    @Override
    public List<HashMap<String, Object>> start(List<Data> zeroList, Boolean viaNearestNeighbor,
                                               Integer clusterCount, Integer iterCount) {
        zeroLists = zeroList;

        clusters = new ArrayList<>();

        // fill params
        dataList = FillFunctions.fillDataListFromTxtFile();

//        System.out.println("Введите кол-во итераций:");
//        ITR = input.nextInt();

        if (zeroList == null) {
            centerIndexes = new Integer[clusterCount];
        } else {
            clusterCount = zeroList.size();
        }

        if (zeroLists == null) {
            // fill centers index
            for(int iter = 0; iter < clusterCount; iter++){
                centerIndexes[iter] = iter;
            }
        }


        System.out.println("=============== RESULT ==================");
        // Формируем классы
        clusters = fillCluster(true, clusters, new ArrayList<>(), clusterCount);

//        DisplayFunctions.printResult14D(clusters);

        for (int itr = 0; itr < iterCount; itr++) {
            // step 3 Формируем классы
            // формула для нахождение расстояние между объектами
            // sqrt(pow((x1 - x2), 2) + pow((y1 - y2), 2))
            for (Data data : dataList) {
                Double[] distanceList = new Double[clusterCount];

                for (HashMap<String, Object> classEntity : clusters) {
                    if (!CommonFunctions.checkForEquality(data, (Data) classEntity.get(clusterCenterKey))) {
                        Data classCenter = (Data) classEntity.get(clusterCenterKey);
                        double distance = 0.0;
                        for (int i = 0; i < data.getAttributes().size(); i++) {
                            distance += Math.pow((classCenter.getAttributes().get(i) - data.getAttributes().get(i)), 2);
                        }
                        distanceList[clusters.indexOf(classEntity)] = Math.sqrt(distance);
                    } else {
                        distanceList = null;
                        break;
                    }
                }
//                DisplayFunctions.printResult14D(clusters);
                if (distanceList == null) continue;

//                DisplayFunctions.printDistance(distanceList);

                int minDistance = CommonFunctions.getMinValueIndex(distanceList);

                List<Data> tempObject = (List<Data>) clusters.get(minDistance).get(clusterObjectsKey);
                tempObject.add(data);
                clusters.get(minDistance).put(clusterObjectsKey, tempObject);
            }

            if(viaNearestNeighbor){
                break;
            }else{
                // step 4
                // Корректировка центров
                List<Data> newZeros = new ArrayList<>();
                for (HashMap<String, Object> cluster : clusters) {
                    List<Data> clusterObjects = (List<Data>) cluster.get(clusterObjectsKey);
                    int totalCount = clusterObjects.size();
                    List<Double> attributes = FillFunctions.fillAttributes(clusterObjects.get(0).getAttributes().size());

                    for (Data data : clusterObjects) {
                        for (int attrIndex = 0; attrIndex < data.getAttributes().size(); attrIndex++) {
                            double temp = attributes.get(attrIndex) + data.getAttributes().get(attrIndex);
                            attributes.set(attrIndex, temp);
                        }
                    }

                    for (int attrIndex = 0; attrIndex < attributes.size(); attrIndex++) {
                        attributes.set(attrIndex, attributes.get(attrIndex) / totalCount);
                    }

                    newZeros.add(new Data(attributes));
                }
                if (checkingForEqualityOfCenters(newZeros, clusterCount) || itr == iterCount - 1) {
                    System.out.println("Finish ITR: " + itr);
                    break;
                } else {
                    clusters = fillCluster(false, clusters, newZeros, clusterCount);
                }
                System.out.println("ITR " + itr + " finished ...");
            }
        }

        return clusters;
    }

    @Override
    public List<HashMap<String, Object>> startWithRandomCenter(Integer classSize,
                                                               Integer clusterCount, Integer iterCount) {
        clusters = new ArrayList<>();

        // fill params
        dataList = FillFunctions.fillDataListFromTxtFile();
        clusterCount = classSize;
        centerIndexes = new Integer[clusterCount];

        // Display sign list
//        DisplayFunctions.printData(dataList);

        // fill centers index
        int iter = 0;
        do {
            int centerIndex = CommonFunctions.getRandomIntegerBetweenRange(0, dataList.size() - 1);
            if (!CommonFunctions.check(centerIndexes, centerIndex)) {
                centerIndexes[iter] = centerIndex;
                iter++;
            }
        } while (iter < clusterCount);

        // Формируем классы
        clusters = fillCluster(true, clusters, new ArrayList<>(), clusterCount);

        for (int itr = 0; itr < iterCount; itr++) {
            // step 3 Формируем классы
            // формула для нахождение расстояние между объектами
            // sqrt(pow((x1 - x2), 2) + pow((y1 - y2), 2))
            for (Data data : dataList) {
                Double[] distanceList = new Double[clusterCount];

                for (HashMap<String, Object> classEntity : clusters) {
                    if (!CommonFunctions.checkForEquality(data, (Data) classEntity.get(clusterCenterKey))) {
                        Data classCenter = (Data) classEntity.get(clusterCenterKey);
                        double distance = 0.0;
                        for (int i = 0; i < data.getAttributes().size(); i++) {
                            distance += Math.pow((classCenter.getAttributes().get(i) - data.getAttributes().get(i)), 2);
                        }
                        distanceList[clusters.indexOf(classEntity)] = Math.sqrt(distance);
                    } else {
                        distanceList = null;
                        break;
                    }
                }
//                DisplayFunctions.printResult14D(clusters);
                if (distanceList == null) continue;

//                DisplayFunctions.printDistance(distanceList);

                int minDistance = CommonFunctions.getMinValueIndex(distanceList);

                List<Data> tempObject =
                        (List<Data>) clusters.get(minDistance).get(clusterObjectsKey);

                tempObject.add(data);
                clusters.get(minDistance).put(clusterObjectsKey, tempObject);
            }

            // step 4
            // Корректировка центров
            List<Data> newZeros = new ArrayList<>();
            for (HashMap<String, Object> cluster : clusters) {
                List<Data> clusterObjects = (List<Data>) cluster.get(clusterObjectsKey);
                int totalCount = clusterObjects.size();
                List<Double> attributes = FillFunctions.fillAttributes(clusterObjects.get(0).getAttributes().size());

                for (Data data : clusterObjects) {
                    for (int attrIndex = 0; attrIndex < data.getAttributes().size(); attrIndex++) {
                        double temp = attributes.get(attrIndex) + data.getAttributes().get(attrIndex);
                        attributes.set(attrIndex, temp);
                    }
                }

                for (int attrIndex = 0; attrIndex < attributes.size(); attrIndex++) {
                    attributes.set(attrIndex, attributes.get(attrIndex) / totalCount);
                }

                newZeros.add(new Data(attributes));
            }

            if (checkingForEqualityOfCenters(newZeros, clusterCount) || itr == iterCount - 1) {
                break;
            } else {
                clusters = fillCluster(false, clusters, newZeros, clusterCount);
            }
        }

        DisplayFunctions.printResult14D(clusters);
        return clusters;
    }

    public void clear(){
        clusters = new ArrayList<>();
        centerIndexes = null;
        dataList = null;
        zeroLists = null;
    }


    private boolean checkingForEqualityOfCenters(List<Data> newZeros, Integer clusterCount) {
        int checkingIter = 0;
        for (int zeroIndex = 0; zeroIndex < newZeros.size(); zeroIndex++) {
            if (CommonFunctions.checkForEquality(
                    (Data) clusters.get(zeroIndex).get(clusterCenterKey), newZeros.get(zeroIndex)
            )) {
                checkingIter++;
            }
        }
        return checkingIter == clusterCount;
    }

    private List<HashMap<String, Object>> fillCluster(boolean isDefault,
                                                             List<HashMap<String, Object>> clusters,
                                                             List<Data> newValues,
                                                             Integer clusterCount) {
        if (isDefault) {
            if (zeroLists == null) {
                for (int l = 0; l < clusterCount; l++) {
                    Data centerData = dataList.get(centerIndexes[l]);
                    HashMap<String, Object> objectMap = new HashMap<String, Object>();
                    objectMap.put(clusterCenterKey, centerData);
                    objectMap.put(clusterObjectsKey, new ArrayList<Data>() {{
                        add(centerData);
                    }});
                    clusters.add(l, objectMap);
                }
            } else {
                for (Data zero : zeroLists) {
                    HashMap<String, Object> objectMap = new HashMap<String, Object>();
                    objectMap.put(clusterCenterKey, zero);
                    objectMap.put(clusterObjectsKey, new ArrayList<Data>() {{
                        add(zero);
                    }});
                    clusters.add(zeroLists.indexOf(zero), objectMap);
                }
            }
        } else {

            for (Data newValue : newValues) {
                Data nearest = CommonFunctions.nearestObject(clusters.get(newValues.indexOf(newValue)), newValue);

                HashMap<String, Object> objectMap = new HashMap<String, Object>();
                objectMap.put(clusterCenterKey, nearest);
                objectMap.put(clusterObjectsKey, new ArrayList<Data>() {{
                    add(nearest);
                }});
                clusters.set(newValues.indexOf(newValue), objectMap);
            }
        }
        return clusters;
    }
}
