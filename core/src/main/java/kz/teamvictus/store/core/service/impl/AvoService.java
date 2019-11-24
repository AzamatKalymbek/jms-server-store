package kz.teamvictus.store.core.service.impl;

import kz.teamvictus.store.core.service.IAvoService;
import kz.teamvictus.store.core.util.functions.CommonFunctions;
import kz.teamvictus.store.core.util.functions.DisplayFunctions;
import kz.teamvictus.store.core.util.functions.FillFunctions;
import kz.teamvictus.store.core.util.models.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;

import static kz.teamvictus.store.core.util.constance.CoreConstance.clusterCenterKey;
import static kz.teamvictus.store.core.util.constance.CoreConstance.clusterObjectsKey;


@Service
public class AvoService implements IAvoService {
    private static final Logger logger = LoggerFactory.getLogger(AvoService.class);

    // Исходные данные
    private List<Data> dataList;

    // Массив центров классов (содержить в себе индексы внесенный пользователем)
    private List<Data> zeroLists;
    private Integer[] centerIndexes;

    // Кол-во классов
    private Integer L = 3;
    private Integer gammaParam = 0;

    private List<HashMap<String, Object>> clusters = new ArrayList<>();

    private List<Double> epsilonList = new ArrayList<>();

    @Override
    public List<HashMap<String, Object>> start(List<Data> zeroList, Integer classCount) {
        zeroLists = zeroList;
        Scanner input = new Scanner(System.in);
        clusters = new ArrayList<>();

        // fill params
        dataList = FillFunctions.fillDataListFromTxtFile();

        calcEpsilon();

        do {
            System.out.println("Gamma parameter");
            gammaParam = input.nextInt();
        } while (gammaParam > epsilonList.size() || gammaParam < 0);

//        System.out.println("Введите кол-во итераций:"); ITR = input.nextInt();

        if (zeroList == null) {
            System.out.println("Type Class count:");
            if(classCount == 0){
                L = input.nextInt();
            }else{
                L = classCount;
            }
            centerIndexes = new Integer[L];
        } else {
            L = zeroList.size();
        }

        System.out.println("=========================================");
        System.out.println("Информация о программе::");
        System.out.printf("Количество исходных обьектов: %d \n", dataList.size());
        System.out.printf("Кол-во кластеров: %d \n", L);
        System.out.println("=========================================");

        // Display sign list
        DisplayFunctions.printData(dataList);

        if (zeroLists == null) {
            // fill centers index
            int iter = 0;
            do {
                int centerIndex = CommonFunctions.getRandomIntegerBetweenRange(0, dataList.size() - 1);
                if (!CommonFunctions.check(centerIndexes, centerIndex)) {
                    centerIndexes[iter] = centerIndex;
                    iter++;
                }
            } while (iter < L);
        }

        System.out.println("=============== RESULT ==================");

        // Формируем классы
        clusters = fillCluster(true, clusters, new ArrayList<>());

//        DisplayFunctions.printResult14D(clusters);

        for (Data data : dataList) {
            Double[] distanceList = new Double[L];

            System.out.println("===================start==========================");
            for (HashMap<String, Object> classEntity : clusters) {
                List<Data> classSigns = (List<Data>) classEntity.get(clusterObjectsKey);
                Data center = (Data) classEntity.get(clusterCenterKey);
                if(CommonFunctions.checkForEquality(center, data)){
                    distanceList = null;
                    break;
                }else{
                    double distanceToAll = 0.0;
                    for (Data sign : classSigns) {
                        int epsCount = 0;
                        for (int i = 0; i < data.getAttributes().size(); i++) {
                            if (Math.abs(sign.getAttributes().get(i) - data.getAttributes().get(i)) <= epsilonList.get(i)) {
                                epsCount++;
                            }
                        }
                        distanceToAll += epsCount;
                    }

//                    System.out.println("distanceToAll: " + distanceToAll);
//                    System.out.println("classSigns.size()" + classSigns.size());
                    distanceList[clusters.indexOf(classEntity)] = distanceToAll / classSigns.size();
                }
            }

            System.out.println("===================end==========================");
            if (distanceList == null) continue;

//                DisplayFunctions.printDistance(distanceList);
            int minDistance = CommonFunctions.getMaxValueIndex(distanceList);
            List<Data> tempObject =
                    (List<Data>) clusters.get(minDistance).get(clusterObjectsKey);

            // checkForAVO(tempObject, data);
            tempObject.add(data);
            clusters.get(minDistance).put(clusterObjectsKey, tempObject);
        }

//        DisplayFunctions.printResult14D(clusters);

        // calculate quality functional
//        double QF = ReduceCentroid.getQualityFunctional(clusters);
//        System.out.println("QUALITY FUNCTIONAL: " + QF);
        return clusters;
    }

    private boolean checkForAVO(Data obj1, Data obj2) {
        // e = 2 | (m / (m - 1))
        // m = число объектов
        // n - число признаков
        int n = obj1.getAttributes().size();
        int beta = 0;
        for (int i = 0; i < n; i++) {
            if (obj1.getAttributes().get(i) - obj2.getAttributes().get(i) <= epsilonList.get(i)) {
                beta++;
            }
        }

        return beta >= gammaParam;
    }

    // epsList
    private void calcEpsilon() {
        // object count
        int m = dataList.size();
        int n = dataList.get(0).getAttributes().size();
        double combinationNumber = (2 / (double) (m * (m - 1)));

        for (int i = 0; i < n; i++) {
            double epsilon = 0.0;
            for (int j = 0; j < m - 1; j++) {
                epsilon += Math.abs((m - j) * calcReverse(i, j) - j * calcReverse(i, j + 1));
            }
            epsilonList.add(epsilon * combinationNumber);
        }

        System.out.println("EPSILON LIST");
        for (Double epsilon : epsilonList) {
            System.out.println((epsilonList.indexOf(epsilon) + 1) + ") " + epsilon);
        }

    }

    private double calcReverse(int i, int j) {
        return dataList.get(j).getAttributes().get(i);
    }

    private double calcReverse(int m, int i, int j) {
        double dividend = dataList.get(j).getAttributes().get(i);
        double divider = 0.0;
        for (int j1 = 0; j1 < m; j1++) {
            divider += Math.pow(dataList.get(j1).getAttributes().get(i), 2);
        }

        return dividend / Math.sqrt(divider);
    }

    private boolean checkingForEqualityOfCenters(List<Data> newZeros) {
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

    private List<HashMap<String, Object>> fillCluster(boolean isDefault,
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
                HashMap<String, Object> objectMap = new HashMap<String, Object>();
                objectMap.put(clusterCenterKey, newValue);

                boolean centerExistInObjects = false;
                for (HashMap<String, Object> cluster : clusters) {
                    for (Data data : (List<Data>) cluster.get(clusterObjectsKey)) {
                        if (CommonFunctions.checkForEquality(data, newValue)) {
                            centerExistInObjects = true;
                            break;
                        }
                    }
                }

                if (centerExistInObjects)
                    objectMap.put(clusterObjectsKey, new ArrayList<Data>() {{
                        add(newValue);
                    }});
                else
                    objectMap.put(clusterObjectsKey, new ArrayList<Data>());

                clusters.set(newValues.indexOf(newValue), objectMap);
            }
        }
        return clusters;
    }
}
