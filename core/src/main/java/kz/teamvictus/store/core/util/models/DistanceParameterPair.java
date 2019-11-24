package kz.teamvictus.store.core.util.models;

import java.util.ArrayList;
import java.util.List;

public class DistanceParameterPair {
    private List<DistanceParameter> pairs;

    public DistanceParameterPair() {
        pairs = new ArrayList<>();
    }

    public DistanceParameterPair(List<DistanceParameter> pairs) {
        this.pairs = pairs;
    }

    public List<DistanceParameter> getPairs() {
        return pairs;
    }

    public void setPairs(List<DistanceParameter> pairs) {
        this.pairs = pairs;
    }



}
