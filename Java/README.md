# Java Examples
To run MAVSDK Java, please follow the instructions on [MAVSDK-Java repo](https://github.com/mavlink/MAVSDK-Java) and [Jonas Vautherin's blog](https://auterion.com/getting-started-with-mavsdk-java/).

## Mission Attack Demo
1. Refer to the `scripts/runpx4.sh`, start the jMAVSim simulator
2. Start the `RunMission.java` program using `make runmission` to start the mission to a destination specified in `RunMission.java`
3. During the mission execution, execute `ForceLanding.java` using `make forceland`. 

Note that this simulates the internal threats in which multiple processes (including malicious processes) are running in the MMC (Mission Management Computer, raspberry pi), which is sending malicious commands to the CubePilots