package kz.teamvictus.store.core.service;

import kz.teamvictus.store.core.util.models.Data;

import java.util.HashMap;
import java.util.List;

public interface IKmeanService {

    List<HashMap<String, Object>> start(List<Data> zeroList, Boolean viaNearestNeighbor,  Integer clusterCount, Integer iterCount);

    List<HashMap<String, Object>> startWithRandomCenter(Integer classSize, Integer clusterCount, Integer iterCount);

    void clear();
}
