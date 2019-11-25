package kz.teamvictus.store.core.service;

import kz.teamvictus.store.core.util.models.Data;

import java.util.HashMap;
import java.util.List;

public interface IAvoService {

    List<HashMap<String, Object>> start(List<Data> zeroList, Integer clusterCount, Integer gammaParam);
}
