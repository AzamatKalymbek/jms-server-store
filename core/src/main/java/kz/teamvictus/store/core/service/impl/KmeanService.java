package kz.teamvictus.store.core.service.impl;

import kz.teamvictus.store.core.service.IKmeanService;
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
import java.util.Scanner;

import static kz.teamvictus.store.core.util.constance.CoreConstance.clusterCenterKey;
import static kz.teamvictus.store.core.util.constance.CoreConstance.clusterObjectsKey;


@Service
public class KmeanService implements IKmeanService {
    private static final Logger logger = LoggerFactory.getLogger(KmeanService.class);

    // Исходные данные
    private static List<Data> dataList;
    private static List<Data> zeroLists;
    // Массив центров классов (содержить в себе индексы внесенный пользователем)
    private static Integer[] centerIndexes;
    // Кол-во классов
    private static Integer L = 3;
    // Кол-во итераторов
    private static Integer ITR = 50;

    private static List<HashMap<String, Object>> clusters = new ArrayList<>();


    @Override
    public List<HashMap<String, Object>> start(List<Data> zeroList, Boolean viaNearestNeighbor) {
        zeroLists = zeroList;
        Scanner input = new Scanner(System.in);
        clusters = new ArrayList<>();

        // fill params
        dataList = FillFunctions.fillDataListFromTxtFile();

//        System.out.println("Введите кол-во итераций:");
//        ITR = input.nextInt();

        if (zeroList == null) {
            System.out.println("Type Class count:");
            L = input.nextInt();
            centerIndexes = new Integer[L];
        } else {
            L = zeroList.size();
        }

//        System.out.println("=========================================");
//        System.out.println("Информация о программе::");
//        System.out.printf("Количество исходных обьектов: %d \n", dataList.size());
//        System.out.printf("Кол-во итераций: %d \n", ITR);
//        System.out.printf("Кол-во кластеров: %d \n", L);
//        System.out.println("=========================================");
//
//        // Display sign list
//        DisplayFunctions.printData(dataList);

        if (zeroLists == null) {
            // fill centers index
            int iter = 0;
            while (true) {
                System.out.println("Type center index, left " + (L - iter));
                int centerIndex = input.nextInt();
                if (centerIndex >= dataList.size() || CommonFunctions.check(centerIndexes, centerIndex)) {
                    System.out.println("Invalid index or center already exist, type again");
                } else {
                    centerIndexes[iter] = centerIndex;
                    iter++;
                    if (L - iter <= 0) break;
                }
            }
        }


        System.out.println("=============== RESULT ==================");
        // Формируем классы
        clusters = fillCluster(true, clusters, new ArrayList<>());

//        DisplayFunctions.printResult14D(clusters);

        for (int itr = 0; itr < ITR; itr++) {
            // step 3 Формируем классы
            // формула для нахождение расстояние между объектами
            // sqrt(pow((x1 - x2), 2) + pow((y1 - y2), 2))
            for (Data data : dataList) {
                Double[] distanceList = new Double[L];

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
                if (checkingForEqualityOfCenters(newZeros) || itr == ITR - 1) {
                    System.out.println("Finish ITR: " + itr);
                    break;
                } else {
                    clusters = fillCluster(false, clusters, newZeros);
                }
                System.out.println("ITR " + itr + " finished ...");
            }
        }

        DisplayFunctions.printResult14D(clusters);
        return clusters;
    }

    @Override
    public List<HashMap<String, Object>> startWithRandomCenter(Integer classSize) {
        clusters = new ArrayList<>();

        // fill params
        dataList = FillFunctions.fillDataListFromTxtFile();
        L = classSize;
        centerIndexes = new Integer[L];

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
        } while (iter < L);

        // Формируем классы
        clusters = fillCluster(true, clusters, new ArrayList<>());

        for (int itr = 0; itr < ITR; itr++) {
            // step 3 Формируем классы
            // формула для нахождение расстояние между объектами
            // sqrt(pow((x1 - x2), 2) + pow((y1 - y2), 2))
            for (Data data : dataList) {
                Double[] distanceList = new Double[L];

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

            if (checkingForEqualityOfCenters(newZeros) || itr == ITR - 1) {
                break;
            } else {
                clusters = fillCluster(false, clusters, newZeros);
            }
        }

        DisplayFunctions.printResult14D(clusters);
        return clusters;
    }

    public void clear(){
        clusters = new ArrayList<>();
        ITR = 50;
        L = 3;
        centerIndexes = null;
        dataList = null;
        zeroLists = null;
    }


    private static boolean checkingForEqualityOfCenters(List<Data> newZeros) {
        int checkingIter = 0;
        for (int zeroIndex = 0; zeroIndex < newZeros.size(); zeroIndex++) {
            if (CommonFunctions.checkForEquality(
                    (Data) clusters.get(zeroIndex).get(clusterCenterKey), newZeros.get(zeroIndex)
            )) {
                checkingIter++;
            }
        }
        return checkingIter == L;
    }

    private static List<HashMap<String, Object>> fillCluster(boolean isDefault,
                                                             List<HashMap<String, Object>> clusters,
                                                             List<Data> newValues) {
        if (isDefault) {
            if (zeroLists == null) {
                for (int l = 0; l < L; l++) {
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
