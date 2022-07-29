package edu.cmu.demo;

import io.mavsdk.System;
import io.mavsdk.action.Action;
import io.mavsdk.mission.Mission;
import io.mavsdk.offboard.Offboard;

import io.reactivex.Completable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
// import org.slf4j.Logger;
// import org.slf4j.LoggerFactory;

import edu.cmu.demo.utils.DroneAction;
import edu.cmu.demo.utils.DroneTelemetry;

public class ForceLanding {
 
    public static void main(String [] args) {
        System drone = new System("127.0.0.1", 50051);

        // DroneAction.armAndTakeoff(drone);
        DroneAction.land(drone);
    }
}
