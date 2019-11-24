package kz.teamvictus.store.core.util.models;

public class DistanceParameter {
   private Double distance;
   private Integer index;

   public DistanceParameter(Double distance, Integer index) {
      this.distance = distance;
      this.index = index;
   }

   public Double getDistance() {
      return distance;
   }

   public void setDistance(Double distance) {
      this.distance = distance;
   }

   public Integer getIndex() { return index; }

   public void setIndex(Integer index) {
      this.index = index;
   }

   @Override
   public String toString() {
      return "DistanceParameter{" +
              "distance=" + distance +
              ", index=" + index +
              '}';
   }
}
