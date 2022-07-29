package edu.cmu.demo.utils;

import io.mavsdk.System;
import io.mavsdk.action.Action;
// import edu.cmu.stl.mavsdk.utils.Plan;
// import edu.cmu.stl.mavsdk.utils.Feature;
// import edu.cmu.stl.mavsdk.case_study.organ_delivery.features.DeliveryPlanning;
// import edu.cmu.stl.mavsdk.case_study.organ_delivery.features.SafeLanding;
import io.mavsdk.mission.Mission;
import io.mavsdk.offboard.Offboard;

import io.reactivex.Completable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
// import org.slf4j.Logger;
// import org.slf4j.LoggerFactory;


public class DroneAction {
    
    public static void armAndTakeoff(System drone) {
        Completable armAndTakeoff = drone.getAction().arm().andThen(drone.getAction().takeoff());
        java.lang.System.out.println("arming and taking off");
        armAndTakeoff.blockingAwait();
        sleep(5);
    }

    public static void land(System drone) {
        Completable land = drone.getAction().land();
        java.lang.System.out.println("landing");
        land.blockingAwait();
    }

    public static void startOffboardMode(System drone) {
        // initialize VelocityNedYaw struct
        Completable offboard = drone.getOffboard().setVelocityNed(new Offboard.VelocityNedYaw(0f, 0f, 0f, 0f))
                                    .andThen(drone.getOffboard().start());
        java.lang.System.out.println("starting offboard mode");
        offboard.blockingAwait();
    }

    /* @param seconds: number of seconds that drone needs to execute at the current posture */
    public static void sendVelocityNed(System drone, int seconds, float northSpeed, float eastSpeed, float downSpeed, float yaw) {
        Completable command = drone.getOffboard().setVelocityNed(new Offboard.VelocityNedYaw(northSpeed, eastSpeed, downSpeed, yaw));
        // North, East, Down, Yaw
        java.lang.System.out.println("starting commands: N:" + northSpeed + " E:" + eastSpeed + " D:" + downSpeed + " Yaw:" + yaw);
        command.blockingAwait();
        sleep(seconds);
    }

    public static void sleep(int seconds) {
        try {
            TimeUnit.SECONDS.sleep(seconds);
        } catch (InterruptedException e) {
        }
    }

}
