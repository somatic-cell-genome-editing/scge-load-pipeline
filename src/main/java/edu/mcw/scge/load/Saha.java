package edu.mcw.scge.load;

import edu.mcw.rgd.process.Utils;
import edu.mcw.scge.Manager;

// loaded on DEV on Nov 27, 2024
// loaded on STAGE on Aug 19, 2025

public class Saha {

    public static void main(String[] args) throws Exception {

        Manager manager = Manager.getManagerInstance();

        manager.studyId = 1055;
        manager.fileName = "data/Saha-1055-1.xlsx";
        manager.tier = 0;

        try {
            manager.loadExperimentData(18000000125L, "In Vitro - Fig1", 8, 5);
            manager.loadExperimentData(18000000126L, "In Vitro - Fig2", 1, 5);
            manager.loadExperimentData(18000000127L, "In Vitro - Fig3", 6, 5);
            manager.loadExperimentData(18000000128L, "In Vitro - Fig4", 6, 5);
            manager.loadExperimentData(18000000129L, "In Vitro - Fig5", 10, 5);
            manager.loadExperimentData(18000000130L, "In Vitro - Fig6", 10, 5);

        } catch (Exception e) {
            Utils.printStackTrace(e, manager.getLog());
        }
    }
}
