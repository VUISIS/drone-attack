package edu.cmu.demo.utils;

import io.mavsdk.System;
import io.mavsdk.action.Action;
// import edu.cmu.stl.mavsdk.utils.Plan;
// import edu.cmu.stl.mavsdk.utils.Feature;
// import edu.cmu.stl.mavsdk.case_study.organ_delivery.features.DeliveryPlanning;
// import edu.cmu.stl.mavsdk.case_study.organ_delivery.features.SafeLanding;
import io.mavsdk.mission.Mission;
import io.mavsdk.offboard.Offboard;
import io.mavsdk.telemetry.Telemetry;
import io.reactivex.Completable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
// import org.slf4j.Logger;
// import org.slf4j.LoggerFactory;

import java.util.ArrayList;

public class DroneTelemetry {
    public static Telemetry.LandedState getLandedState(System drone) {
        return drone.getTelemetry().getLandedState().blockingFirst();
    }

    // depends on getLandedState
    public static boolean isLanded(System drone) {
        return getLandedState(drone) == Telemetry.LandedState.ON_GROUND;
    }

    public static Telemetry.Position getPosition(System drone) {
        return drone.getTelemetry().getPosition().blockingFirst();
    }

    // depends on getPosition
    public static Double getLatitudeDeg(System drone) {
        return getPosition(drone).getLatitudeDeg();
    }

    // depends on getPosition
    public static Double getLongitudeDeg(System drone) {
        return getPosition(drone).getLongitudeDeg();
    }

    public static Telemetry.PositionVelocityNed getPositionVelocityNed(System drone) {
        return drone.getTelemetry().getPositionVelocityNed().blockingFirst();
    }

    public static Float getRemainingBatteryPercent(System drone) {
        return drone.getTelemetry().getBattery().blockingFirst().getRemainingPercent() * 100;
    }

    // https://stackoverflow.com/questions/3932502/calculate-angle-between-two-latitude-longitude-points
    // angle returned is in degrees (not radians)
    // absolute bearing: clockwise degree from north
    // i.e. 90 degrees bearing = due east
    // public static double angleFromCoordinate(double lat1, double long1, double
    // lat2,
    // double long2) {

    // double dLon = (long2 - long1);

    // double y = Math.sin(dLon) * Math.cos(lat2);
    // double x = Math.cos(lat1) * Math.sin(lat2) - Math.sin(lat1)
    // * Math.cos(lat2) * Math.cos(dLon);

    // double brng = Math.atan2(y, x);

    // brng = Math.toDegrees(brng);
    // brng = (brng + 360) % 360;
    // brng = 360 - brng; // count degrees counter-clockwise - remove to make
    // clockwise

    // return brng;
    // }
    // finding distance between two coordinates
    // https://dzone.com/articles/distance-calculation-using-3

    // calculate new coordinates with a given distance and bearing
    // https://stackoverflow.com/a/53329672

    // TODO: implement distanceBetween2coord and given bearing and distance, return
    // the new coordinates

    // https://stackoverflow.com/questions/9457988/bearing-from-one-coordinate-to-another
    // receive latitude/longitude in degrees, output in degrees, intermediate
    // representation in radians
    // bearing counts from North, clockwise
    public static double bearing(double lat1, double lon1, double lat2, double lon2) {
        double longitude1 = lon1;
        double longitude2 = lon2;
        double latitude1 = Math.toRadians(lat1);
        double latitude2 = Math.toRadians(lat2);
        double longDiff = Math.toRadians(longitude2 - longitude1);
        double y = Math.sin(longDiff) * Math.cos(latitude2);
        double x = Math.cos(latitude1) * Math.sin(latitude2)
                - Math.sin(latitude1) * Math.cos(latitude2) * Math.cos(longDiff);

        return (Math.toDegrees(Math.atan2(y, x)) + 360) % 360;
    }

    /**
     * Calculate distance between two points in latitude and longitude taking
     * into account height difference. If you are not interested in height
     * difference pass 0.0. Uses Haversine method as its base.
     * 
     * lat1, lon1 Start point lat2, lon2 End point el1 Start altitude in meters
     * el2 End altitude in meters
     * 
     * @returns Distance in Meters
     */
    // https://stackoverflow.com/questions/3694380/calculating-distance-between-two-points-using-latitude-longitude
    public static double distance(double lat1, double lon1, double lat2,
            double lon2, double el1, double el2) {

        final int R = 6371; // Radius of the earth

        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                        * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double distance = R * c * 1000; // convert to meters
        double height = el1 - el2;
        distance = Math.pow(distance, 2) + Math.pow(height, 2);
        return Math.sqrt(distance);
    }

    // given coord 1 and coord 2, find the relative position of coord 2 with respect to coord 1
    // in a cartesian plane
    public static ArrayList<Double> relativePosition(double lat1, double lon1, double lat2,
            double lon2) {
        double distanceValue = distance(lat1, lon1, lat2, lon2, 0, 0);
        double bearingValue = bearing(lat1, lon1, lat2, lon2);

        double northPos = distanceValue * Math.cos(Math.toRadians(bearingValue));
        double eastPos = distanceValue * Math.sin(Math.toRadians(bearingValue));

        ArrayList<Double> result = new ArrayList<Double>();
        result.add(eastPos);
        result.add(northPos);

        return result;
    }

}
