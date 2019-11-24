package kz.teamvictus.store.core.util.models;

import java.util.List;

// for 2 version (many dimensional)
public class Data {
    private List<Double> attributes;
    private Boolean center;

    public Data(List<Double> attributes){
        this.attributes = attributes;
        this.center = false;
    }

    public String toString() {
        StringBuilder attributes = new StringBuilder();
        String prefix = "";
        for(Double attribute: this.attributes){
            attributes.append(prefix);
            prefix = ", ";
            attributes.append(attribute);
        }
        return "Object(" + attributes + ")";
    }

    public String toStringForCoord() {
        StringBuilder attributes = new StringBuilder();
        String prefix = "";
        for(Double attribute: this.attributes){
            attributes.append(prefix);
            prefix = ", ";
            attributes.append(attribute);
        }
        return "(" + attributes + ")";
    }

    public List<Double> getAttributes() {
        return attributes;
    }

    public void setAttributes(List<Double> attributes) {
        this.attributes = attributes;
    }

    public Boolean getCenter() {
        return center;
    }

    public void setCenter(Boolean center) {
        this.center = center;
    }
}
