package kz.teamvictus.store.core.service.impl;

import kz.teamvictus.store.core.service.IMaxMinService;
import kz.teamvictus.store.core.util.functions.CommonFunctions;
import kz.teamvictus.store.core.util.functions.FillFunctions;
import kz.teamvictus.store.core.util.models.Data;
import kz.teamvictus.store.core.util.models.DistanceParameter;
import kz.teamvictus.store.core.util.models.DistanceParameterPair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.*;


@Service
public class MaxMinService implements IMaxMinService {
    private static final Logger logger = LoggerFactory.getLogger(MaxMinService.class);

    @Override
    public List<Data> start(Boolean viaMatrixDistance, String sourceFileName) {
        // fill params
        List<Data> dataList = FillFunctions.fillDataListFromTxtFile(sourceFileName);

        // Display parameter list
//        DisplayFunctions.printData(dataList);

        List<Data> zeroList = new ArrayList<>();
        if (viaMatrixDistance) {
            //  Шаг 1-4 выбрать 1 и 2 центры с помощью матрицы растояние
            double[][] distanceMatrix = new double[dataList.size()][dataList.size()];
            for (int d1 = 0; d1 < dataList.size(); d1++) {
                for (int d2 = 0; d2 < dataList.size(); d2++) {
                    distanceMatrix[d1][d2] = CommonFunctions.getDistance(dataList.get(d1), dataList.get(d2));
                }
            }

            Map<String, Integer> map = CommonFunctions.findMax(distanceMatrix);
            int f = map.get(CommonFunctions.firstCentroid);
            int s = map.get(CommonFunctions.secondCentroid);
            dataList.get(f).setCenter(true);
            zeroList.add(dataList.get(f));
            dataList.get(s).setCenter(true);
            zeroList.add(dataList.get(s));
        } else {

            // Шаг 1 выбрать любой обьект как центр
            dataList.get(0).setCenter(true);
            zeroList.add(dataList.get(0));

            // Шаг 2 Вычисляем расс до всех обьектов
            List<DistanceParameter> distanceToAll = new ArrayList<>();
            for (Data data : dataList) {
                if (!data.getCenter()) {
                    double distance = 0.0;
                    for (int i = 0; i < data.getAttributes().size(); i++) {
                        distance += Math.pow((zeroList.get(0).getAttributes().get(i) - data.getAttributes().get(i)), 2);
                    }
                    distanceToAll.add(new DistanceParameter(Math.sqrt(distance), dataList.indexOf(data)));
                }
            }

            // Шаг 3 Находим max из расстояний
            DistanceParameter maxByDistance = distanceToAll
                    .stream()
                    .max(Comparator.comparing(DistanceParameter::getDistance))
                    .orElseThrow(NoSuchElementException::new);

            // Шаг 4 формируем второй центр
            dataList.get(maxByDistance.getIndex()).setCenter(true);
            zeroList.add(dataList.get(maxByDistance.getIndex()));
        }

        while (true) {
            // Шаг 5 создаем пары
            List<DistanceParameterPair> distancePairsToAll = new ArrayList<>();
            for (Data data : dataList) {
                if (!data.getCenter()) {
                    DistanceParameterPair templ = new DistanceParameterPair();
                    for (Data zero : zeroList) {
                        double distance = 0.0;
                        for (int i = 0; i < data.getAttributes().size(); i++) {
                            distance += Math.pow((zero.getAttributes().get(i) - data.getAttributes().get(i)), 2);
                        }

                        templ.getPairs().add(
                                new DistanceParameter(Math.sqrt(distance), dataList.indexOf(data))
                        );
                    }
                    distancePairsToAll.add(templ);
                }
            }

            // Шаг 6 фиксируем min
            List<DistanceParameter> minsFromPair = new ArrayList<>();
            for (DistanceParameterPair distanceParameterPair : distancePairsToAll) {
                minsFromPair.add(
                        distanceParameterPair.getPairs()
                                .stream()
                                .min(Comparator.comparing(DistanceParameter::getDistance))
                                .orElseThrow(NoSuchElementException::new)
                );
            }

            // Шаг 7 фиксируем max из min-ов
            DistanceParameter maxFromPair = minsFromPair
                    .stream()
                    .max(Comparator.comparing(DistanceParameter::getDistance))
                    .orElseThrow(NoSuchElementException::new);

            // Шаг 8 Если последнее составляет значительную часть “ типичных” предыдущих максимальных расстояний,
            // то соответствующий объект назначается центром
            double average = 0.0;
            for (int i = 0; i < zeroList.size() - 1; i++) {
                double distance = 0.0;
                for (int attrIndex = 0; attrIndex < zeroList.get(i).getAttributes().size(); attrIndex++) {
                    distance += Math.pow((zeroList.get(i).getAttributes().get(attrIndex) -
                            zeroList.get(i + 1).getAttributes().get(attrIndex)), 2);
                }
                average += Math.sqrt(distance);
            }

            if (average / zeroList.size() < maxFromPair.getDistance()) {
                dataList.get(maxFromPair.getIndex()).setCenter(true);
                zeroList.add(dataList.get(maxFromPair.getIndex()));
            } else {
                break;
            }
        }

//        System.out.println("Вывод:: >>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
//        System.out.println("Найденные центры: ");
//        DisplayFunctions.printData(zeroList);
        return zeroList;
    }
}
