package kz.teamvictus.store.core.service;

import kz.teamvictus.store.core.util.models.Data;

import java.util.HashMap;
import java.util.List;

public interface IMaxMinService {

    List<Data> start(Boolean viaMatrixDistance, String sourceFileName);
}
