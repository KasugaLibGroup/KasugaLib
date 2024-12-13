package kasuga.lib.core.create.graph;

import com.simibubi.create.content.trains.entity.Train;

import java.util.WeakHashMap;

public class TrainDistanceIntegrator {
    protected double distance = 0.0F;
    protected WeakHashMap<Object, Double> objectDistances = new WeakHashMap<>();

    public TrainDistanceIntegrator() {}

    public void addDistance(double deltaDistance){
        this.distance += deltaDistance;
    }

    public boolean isNewDistance(Object object){
        return objectDistances.containsKey(object);
    }

    public double getDistance(Object object){
        return objectDistances.containsKey(object) ?
                distance - objectDistances.get(object)
                : 0.0F;
    }

    public double getAndResetDistance(Object object) {
        return objectDistances.computeIfAbsent(object, (i)->distance) - distance;
    }

    public void resetDistance(Object object){
        objectDistances.remove(object);
    }

    public void resetDistance(){
        objectDistances.clear();
        distance = 0.0F;
    }
}
