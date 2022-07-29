package edu.cmu.demo;
import java.util.ArrayList;
import io.mavsdk.telemetry.Telemetry;
import io.mavsdk.System;
import io.mavsdk.action.Action;
import edu.cmu.demo.utils.Plan;
// import edu.cmu.stl.mavsdk.utils.Plan;
// import edu.cmu.stl.mavsdk.utils.Feature;
// import edu.cmu.stl.mavsdk.case_study.organ_delivery.features.DeliveryPlanning;
// import edu.cmu.stl.mavsdk.case_study.organ_delivery.features.SafeLanding;
import io.mavsdk.mission.Mission;
import io.mavsdk.offboard.Offboard;

import io.reactivex.Completable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.cmu.demo.utils.DroneAction;
import edu.cmu.demo.utils.DroneTelemetry;

// import edu.cmu.stl.mavsdk.utils.DroneAction;
// import edu.cmu.stl.mavsdk.utils.DroneTelemetry;

// import edu.cmu.stl.mavsdk.case_study.organ_delivery.features.Resolver;

public class RunMission {
    public static double SPEED = 5; // m/s
    public static int REFRESH_RATE = 2; // in seconds, the interval in which the telemetry is collected, and plans are
                                        // executed
    // low refresh rate contribute to RC loss

    // destination : the cut
    public static double dest_lat = 40.444139;
    public static double dest_lon = -79.942639;

    // tepper school of business
    // public static double lat = 40.444936;
    // public static double lon = -79.9511249;

    public static void main(String[] args) {
        java.lang.System.out.println("Starting Organ Delivery Case Study...");
        run();
    }

    public static void run() {
        /*
         * Action Sequence
         * 1. Take off
         * 2. Self-adaptation:
         * a. get telemetry, package it into signal
         * b. feature 1 (safe landing): land if battery is low, otherwise keep going
         * where it was going
         * c. feature 2 (delivery planning): land if arrive at the destination, or keep
         * calculating the heading and go to the corresponding direction
         * d. conflict detection and resolution
         */

        // initialize the system and features
        System drone = new System("127.0.0.1", 50051);
        DroneAction.armAndTakeoff(drone);
        DroneAction.startOffboardMode(drone);

        // cathedral of learning mission
        // double lat = 40.4409193;
        // double lon = -79.9822816;

        // craig st
        // double lat = 40.4449039;
        // double lon = -79.948352;

        // tepper school of business
        // double lat = 40.444936;
        // double lon = -79.9511249;

        // the cut

        // webster hall
        // double lat = 40.4469951;
        // double lon = -79.9533387;

        // Feature deliveryPlanning = new DeliveryPlanning(lat, lon);
        // Feature safeLanding = new SafeLanding();

        // SafeLanding.LANDING_THRESHOLD_WEAKENING = 20.0;

        // start the main loop
        while (!DroneTelemetry.isLanded(drone)) {

            Plan dpNext = nextPlan(drone);
            // Plan slNext = safeLanding.nextPlan(drone);

            // // safe landing returns null, no plan, only acutuate delivery planning
            // // both are attempting to land, consistent
            // if (slNext.name().equals("nop") || (slNext.name().equals("land") && dpNext.name().equals("land"))) {
            actuate(drone, dpNext);
            // }

            // conflict: sl = land, dp = next velocity vector
            // else {
            //     // resolve conflict by reconfigure
            //     java.lang.System.out.println("[main] conflict detected! resolver invoked!");

            //     Resolver.resolve(drone);

            //     // check consistency again
            //     Plan dpNextResolved = deliveryPlanning.nextPlan(drone);
            //     Plan slNextResolved = safeLanding.nextPlan(drone);

            //     if (dpNextResolved.name().equals("fly") && slNextResolved.name().equals("land")) {
            //         java.lang.System.out.println("[main] resolution failed!");
            //         safeLanding.actuate(drone, slNextResolved);
            //     } else {
            //         java.lang.System.out.println("[main] resolution success!");
            //         deliveryPlanning.actuate(drone, dpNext);
            //     }
            // }
            java.lang.System.out.println("[main] state: " + DroneTelemetry.getLandedState(drone));
        }

        java.lang.System.out.println("[main] drone has landed. Exiting the program.");

        if (DroneTelemetry.isLanded(drone)) {
            java.lang.System.exit(0);
        }
    }

    public static Plan nextPlan(System drone) {
        // 1. get the current location of the drone
        // 2. get the location of the target

        Telemetry.Position pos = DroneTelemetry.getPosition(drone);
        double curr_lat = pos.getLatitudeDeg();
        double curr_lon = pos.getLongitudeDeg();

        // java.lang.System.out.println(curr_lat);
        // java.lang.System.out.println(curr_lon);

        java.lang.System.out
                .println("[delivery planning] distance to destination: "
                        + DroneTelemetry.distance(curr_lat, curr_lon, dest_lat, dest_lon, 0, 0));

        java.lang.System.out.println("[delivery planning] curr lat: " + curr_lat + " curr lon" + curr_lon);

        // land the drone if it is close to the destination (50 m)
        if (DroneTelemetry.distance(curr_lat, curr_lon, dest_lat, dest_lon, 0, 0) <= 50) {
            java.lang.System.out.println("[delivery planning] destination has reached. Landing drone now!");
            // DroneAction.land(drone);
            return new Plan("land", null);
        } else {
            return genVelocityNed(SPEED, Double.valueOf(REFRESH_RATE), curr_lat,
                    curr_lon, dest_lat, dest_lon);
        }
    }

    public static void actuate(System drone, Plan plan) {
        // double curr_lat = DroneTelemetry.getLatitudeDeg(drone);
        // double curr_lon = DroneTelemetry.getLongitudeDeg(drone);

        ArrayList<Double> argList = plan.parameters();
        // plan<int seconds, float vx, float vy, float vz, float yaw>
        if (plan.name().equals("fly")) {
            DroneAction.sendVelocityNed(drone, argList.get(0).intValue(), argList.get(1).floatValue(),
                    argList.get(2).floatValue(), argList.get(3).floatValue(), argList.get(4).floatValue());
        }
        else if (plan.name().equals("land")) {
            DroneAction.land(drone);
        }

        // double curr_lat = argList.get(5);
        // double curr_lon = argList.get(6);

    }

    public static Plan genVelocityNed(double speed, double refresh_rate, double curr_lat, double curr_lon,
            double dest_lat, double dest_lon) {
        // given the speed, return speed vector in Plan
        ArrayList<Double> argList = new ArrayList<Double>();
        argList.add(refresh_rate);

        // calculate the bearing between the current location and the destination
        double bearing = DroneTelemetry.bearing(curr_lat, curr_lon, dest_lat, dest_lon);
        double northSpeed = speed * Math.cos(Math.toRadians(bearing));
        double eastSpeed = speed * Math.sin(Math.toRadians(bearing));

        argList.add(northSpeed);
        argList.add(eastSpeed);
        argList.add(0.0);
        argList.add(0.0);
        argList.add(curr_lat);
        argList.add(curr_lon);

        Plan plan = new Plan("fly", argList);
        return plan;
    }
}
