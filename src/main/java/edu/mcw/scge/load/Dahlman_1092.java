package edu.mcw.scge.load;

import edu.mcw.rgd.process.Utils;
import edu.mcw.scge.Manager;

// loaded on DEV on Aug 12, 2025
// loaded on DEV on Sep 08, 2025

public class Dahlman_1092 {

    public static void main(String[] args) {

        Manager manager = Manager.getManagerInstance();

        manager.studyId = 1092;
        manager.fileName = "data/Dahlman-1092-2.xlsx";
        manager.tier = 0;

        try {

            manager.loadExperimentData(18000000145L, "In Vivo", 3, 5);

        } catch (Exception e) {
            Utils.printStackTrace(e, manager.getLog());
        }
    }
}
