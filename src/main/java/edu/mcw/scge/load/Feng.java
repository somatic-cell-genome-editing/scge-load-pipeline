package edu.mcw.scge.load;

import edu.mcw.rgd.process.Utils;
import edu.mcw.scge.Manager;

// loaded on DEV on May 27, 2025
// loaded on DEV,STAGE on Jun 10, 2025

public class Feng {

    public static void main(String[] args) {

        Manager manager = Manager.getManagerInstance();

        manager.studyId = 1111;
        manager.fileName = "data/Feng-1111-2.xlsx";
        manager.tier = 0;

        try {

            manager.loadExperimentData(18000000140L, "In Vitro", 1, 5);

        } catch (Exception e) {
            Utils.printStackTrace(e, manager.getLog());
        }
    }
}
