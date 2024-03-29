package kz.teamvictus.store.core.service.impl;

import kz.teamvictus.store.core.service.*;
import kz.teamvictus.store.core.util.functions.CommonFunctions;
import kz.teamvictus.store.core.util.functions.DisplayFunctions;
import kz.teamvictus.store.core.util.functions.FillFunctions;
import kz.teamvictus.store.core.util.models.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import static kz.teamvictus.store.core.util.constance.CoreConstance.clusterCenterKey;


@Service
public class SynthesisService implements ISynthesisService {
    private static final Logger logger = LoggerFactory.getLogger(SynthesisService.class);

    private String sourceFileName = "";
    private List<Data> dataList;
    @Autowired
    private IKmeanService iKmeanService;
    @Autowired
    private IAvoService iAvoService;
    @Autowired
    private IMaxMinService iMaxMinService;
    @Autowired
    private IReduceService iReduceService;


    public HashMap start(String sourceFileName, Integer delta){
        this.sourceFileName = sourceFileName;
        dataList = FillFunctions.fillDataListFromTxtFile(sourceFileName);

        System.out.println("dataList size : " + dataList.size());
        Integer[][] centerMatrix =  new Integer[10][dataList.size()];
        Integer[]   centerMatrixCount = new Integer[dataList.size()];
        Arrays.fill(centerMatrixCount, 0);

        for(int alg = 0; alg < 10; alg++){
            List<HashMap<String, Object>> clusters = selectAlgorithm(alg);
            List<Data> clusterCenters = getCenters(clusters);
            for(int obj = 0; obj < dataList.size(); obj++){
                if(CommonFunctions.checkIfContain(clusterCenters, dataList.get(obj))){
                    centerMatrix[alg][obj] = 1;
                    centerMatrixCount[obj] += 1;
                }else{
                    centerMatrix[alg][obj] = 0;
                }
            }
        }


        Integer[] centerMatrixCountBefore = centerMatrixCount.clone();

        for(int i = 0; i < centerMatrixCount.length; i++){
            if(centerMatrixCount[i] >= delta){
                centerMatrixCount[i] = 1;
            }else{
                centerMatrixCount[i] = 0;
            }
        }

        Double[] FQs = calculateFQ(centerMatrixCount);


        HashMap map = new HashMap<>();

        map.put("algNames", new ArrayList<String>(){{
            add(algorithm1Name());  add(algorithm2Name()); add(algorithm3Name()); add(algorithm4Name());
            add(algorithm5Name());  add(algorithm6Name()); add(algorithm7Name()); add(algorithm8Name());
            add(algorithm9Name());  add(algorithm10Name());
        }});
        map.put("centerMatrixCountBefore" , centerMatrixCountBefore);
        map.put("centerMatrix", centerMatrix);
        map.put("centerMatrixCount" , centerMatrixCount);
        map.put("fqMatrix", FQs);
        return map;
    }

    private List<HashMap<String, Object>> selectAlgorithm(int alg){
        switch (alg+1){
            case 1: return algorithm1();
            case 2: return algorithm2();
            case 3: return algorithm3();
            case 4: return algorithm4();
            case 5: return algorithm5();
            case 6: return algorithm6();
            case 7: return algorithm7();
            case 8: return algorithm8();
            case 9: return algorithm9();
            case 10: return algorithm10();
        }
        return null;
    }

    private Double selectAlgorithm(int alg, List<Data> zeroList){
        switch (alg+1){
            case 1: return algorithm1(zeroList);
            case 2: return algorithm2(zeroList);
            case 3: return algorithm3(zeroList);
            case 4: return algorithm4(zeroList);
            case 5: return algorithm5(zeroList);
            case 6: return algorithm6(zeroList);
            case 7: return algorithm7(zeroList);
            case 8: return algorithm8(zeroList);
            case 9: return algorithm9(zeroList);
            case 10: return algorithm10(zeroList);
        }
        return null;
    }
    private static List<Data> getCenters(List<HashMap<String, Object>> clusters){
        List<Data> dataList = new ArrayList<>();
        for (HashMap<String, Object> cluster: clusters){
            dataList.add((Data) cluster.get(clusterCenterKey));
        }
        return dataList;
    }

    private List<HashMap<String, Object>> algorithm1() {
        System.out.println("A-1 => K-meas 5 классов (центры рандомно)");
        List<HashMap<String, Object>> clusters = iKmeanService.startWithRandomCenter(5, 3, 50, sourceFileName);
        return clusters;
    }

    private List<HashMap<String, Object>> algorithm2() {
        System.out.println("A-2 => K-meas 6 классов (центры рандомно)");
        List<HashMap<String, Object>> clusters =  iKmeanService.startWithRandomCenter(6, 3, 50, sourceFileName);
        return clusters;
    }

    private List<HashMap<String, Object>> algorithm3() {
        System.out.println("A-3 => max-min + ближайший сосед");
        List<Data> zeroList = iMaxMinService.start(false, sourceFileName);
        List<HashMap<String, Object>> clusters = iKmeanService.start(zeroList, true, 3, 50, sourceFileName);
        return clusters;
    }

    private List<HashMap<String, Object>> algorithm4() {
        System.out.println("A-4 => max-min (через матрицы расстояние) + ближайший сосед");
        List<Data> zeroList = iMaxMinService.start(true, sourceFileName);
        List<HashMap<String, Object>> clusters = iKmeanService.start(zeroList, true, 3, 50, sourceFileName);
        return clusters;
    }

    private List<HashMap<String, Object>> algorithm5() {
        System.out.println("A-5 => алгоритм сокращение до 5 классов (начальное кол-во классов 7)");
        List<HashMap<String, Object>> clusters = iReduceService.start(false, null, 7, 5, sourceFileName);
        return clusters;
    }

    private List<HashMap<String, Object>> algorithm6() {
        System.out.println("A-6 => алгоритм сокращение до 6 классов (начальное кол-во классов 7)");
        List<HashMap<String, Object>> clusters = iReduceService.start(false, null, 7, 6, sourceFileName);
        return clusters;
    }

    private List<HashMap<String, Object>> algorithm7() {
        System.out.println("A-7 => max-min + K-means");
        List<Data> zeroList = iMaxMinService.start(true, sourceFileName);
        List<HashMap<String, Object>> clusters = iKmeanService.start(zeroList, false, 3, 50, sourceFileName);
        return clusters;
    }

    private List<HashMap<String, Object>> algorithm8() {
        System.out.println("A-8 => max-min + ABO");
        List<Data> zeroList = iMaxMinService.start(false, sourceFileName);
        List<HashMap<String, Object>> clusters = iAvoService.start(zeroList, 0, 50,2, sourceFileName);
        return clusters;
    }

    private List<HashMap<String, Object>> algorithm9() {
        System.out.println("A-9 => max-min(matrix distance) + ABO");
        List<Data> zeroList = iMaxMinService.start(true, sourceFileName);
        List<HashMap<String, Object>> clusters = iAvoService.start(zeroList, 0, 50,2, sourceFileName);
        return clusters;
    }

    private List<HashMap<String, Object>> algorithm10() {
        System.out.println("A-10 => руч центр + ABO");
        List<HashMap<String, Object>> clusters = iAvoService.start(null, 3, 50, 2, sourceFileName);
        return clusters;
    }

    private Double[] calculateFQ(Integer[] centerChoice){
        List<Data> zeroList = new ArrayList<>();
        for(int i = 0; i < centerChoice.length; i++){
            if(centerChoice[i] == 1){
                zeroList.add(dataList.get(i));
            }
        }

        Double[] FQs = new Double[10];
        for(int alg = 0; alg < 10; alg++){
            FQs[alg] = selectAlgorithm(alg, zeroList);
        }

        return  FQs;
    }
// calculate quality functional
//        double QF = ReduceCentroid.getQualityFunctional(clusters);
//        System.out.println("QUALITY FUNCTIONAL 1: " + QF);


    private double algorithm1(List<Data> zeroList) {
        System.out.println("A-1 => K-meas " + zeroList.size() + " классов (центры экспертно)");
        List<HashMap<String, Object>> clusters = iKmeanService.start(zeroList, false, 3, 50, sourceFileName);
        return iReduceService.getQualityFunctional(clusters);
    }

    private double algorithm2(List<Data> zeroList) {
        System.out.println("A-2 => K-meas " + zeroList.size() + " классов (центры экспертно)");
        List<HashMap<String, Object>> clusters = iKmeanService.start(zeroList, false, 3, 50, sourceFileName);
        return iReduceService.getQualityFunctional(clusters);
    }

    private double algorithm3(List<Data> zeroList) {
        System.out.println("A-3 => max-min + ближайший сосед");
//        List<Data> zeroList = Maxmin.startMaxminAlgorithm(false);
        List<HashMap<String, Object>> clusters = iKmeanService.start(zeroList, true, 3, 50, sourceFileName);
        return iReduceService.getQualityFunctional(clusters);
    }

    private double algorithm4(List<Data> zeroList) {
        System.out.println("A-4 => max-min (через матрицы расстояние) + ближайший сосед");
//        List<Data> zeroList = Maxmin.startMaxminAlgorithm(true);
        List<HashMap<String, Object>> clusters = iKmeanService.start(zeroList, true, 3, 50, sourceFileName);
        return iReduceService.getQualityFunctional(clusters);
    }

    private double algorithm5(List<Data> zeroList) {
        System.out.println("A-5 => алгоритм сокращение до " + (zeroList.size() - 2) + " классов (начальное кол-во классов "+ zeroList.size() + ")");
        List<HashMap<String, Object>> clusters = iReduceService.start(false, zeroList, zeroList.size(), zeroList.size()-2, sourceFileName);
        return iReduceService.getQualityFunctional(clusters);
    }

    private double algorithm6(List<Data> zeroList) {
        System.out.println("A-6 => алгоритм сокращение до " + (zeroList.size() - 1) + " классов (начальное кол-во классов "+ zeroList.size() + ")");
        List<HashMap<String, Object>> clusters = iReduceService.start(false, zeroList, zeroList.size(), zeroList.size()-1, sourceFileName);
        iKmeanService.clear();
        return iReduceService.getQualityFunctional(clusters);
    }

    private double algorithm7(List<Data> zeroList) {
        System.out.println("A-7 => max-min + K-means");
//        List<Data> zeroList = Maxmin.startMaxminAlgorithm(true);
        List<HashMap<String, Object>> clusters = iKmeanService.start(zeroList, false, 3, 50, sourceFileName);
        return iReduceService.getQualityFunctional(clusters);
    }

    private double algorithm8(List<Data> zeroList) {
        System.out.println("A-8 => max-min + ABO");
//        List<Data> zeroList = Maxmin.startMaxminAlgorithm(false);
        List<HashMap<String, Object>> clusters = iAvoService.start(zeroList, 0, 50, 2, sourceFileName);
        return iReduceService.getQualityFunctional(clusters);
    }

    private double algorithm9(List<Data> zeroList) {
        System.out.println("A-9 => max-min(matrix distance) + ABO");
//        List<Data> zeroList = Maxmin.startMaxminAlgorithm(true);
        List<HashMap<String, Object>> clusters = iAvoService.start(zeroList, 0, 50, 2, sourceFileName);
        return iReduceService.getQualityFunctional(clusters);
    }

    private double algorithm10(List<Data> zeroList) {
        System.out.println("A-10 => руч центр + ABO");
        List<HashMap<String, Object>> clusters = iAvoService.start(zeroList, 3, 50, 2, sourceFileName);
        return iReduceService.getQualityFunctional(clusters);
    }





    private String algorithm1Name() {
        return "A-1 => K-meas 5 классов (центры рандомно)";
    }

    private String algorithm2Name() {
        return "A-2 => K-meas 6 классов (центры рандомно)";
    }

    private String algorithm3Name() {
        return "A-3 => max-min + ближайший сосед";
    }

    private String algorithm4Name() {
        return "A-4 => max-min (через матрицы расстояние) + ближайший сосед";
    }

    private String algorithm5Name() {
        return "A-5 => алгоритм сокращение до 5 классов (начальное кол-во классов 7)";
    }

    private String algorithm6Name() {
        return "A-6 => алгоритм сокращение до 6 классов (начальное кол-во классов 7)";
    }

    private String algorithm7Name() {
        return "A-7 => max-min + K-means";
    }

    private String algorithm8Name() {
        return "A-8 => max-min + ABO";
    }

    private String algorithm9Name() {
        return "A-9 => max-min(matrix distance) + ABO";
    }

    private String algorithm10Name() {
        return "A-10 => руч центр + ABO";
    }
}
