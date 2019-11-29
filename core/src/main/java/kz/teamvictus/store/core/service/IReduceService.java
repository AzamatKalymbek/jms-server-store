package kz.teamvictus.store.core.service;

import kz.teamvictus.store.core.util.models.Data;

import java.util.HashMap;
import java.util.List;

public interface IReduceService {
    List<HashMap<String, Object>> start(Boolean customInput, List<Data> zeroList, Integer initialValue, Integer reduceValue, String sourceFileName);

    double getQualityFunctional(List<HashMap<String, Object>> tempClusters);
}
