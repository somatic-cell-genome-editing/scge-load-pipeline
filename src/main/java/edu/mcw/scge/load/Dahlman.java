package edu.mcw.scge.load;

import edu.mcw.rgd.process.Utils;
import edu.mcw.scge.Manager;

// loaded on DEV on Apr 03, 2024

public class Dahlman {

    public static void main(String[] args) {

        Manager manager = Manager.getManagerInstance();

        manager.studyId = 1092;
        manager.fileName = "data/Dahlman-1092-1.xlsx";
        manager.tier = 0;

        try {

            manager.loadExperimentData(18000000112L, "In Vivo", 1, 3);

            manager.loadExperimentData(18000000113L, "In Vivo (2)", 1, 3);
            manager.loadExperimentData(18000000114L, "In Vivo (3)", 6, 3);

        } catch (Exception e) {
            Utils.printStackTrace(e, manager.getLog());
        }
    }
}
