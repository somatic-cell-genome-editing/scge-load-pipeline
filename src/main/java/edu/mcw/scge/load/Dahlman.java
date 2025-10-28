package edu.mcw.scge.load;

import edu.mcw.rgd.process.Utils;
import edu.mcw.scge.Manager;

// loaded on DEV on Apr 03, 2024
// renamed study SCGE_ID from 1092 to 1058, on Jan 28, 2025
// loaded on STAGE on Aug 19, 2025
// reloaded on DEV on Oct 28, 2025, different file, 16 more experiments; total 19 experiments

public class Dahlman {

    public static void main(String[] args) {

        Manager manager = Manager.getManagerInstance();

        manager.studyId = 1058;
        manager.fileName = "data/Dahlman-1058-2.xlsx";
        manager.tier = 0;

        try {

            manager.loadExperimentData(18000000112L, "In Vivo 34616046_1", 5, 5);
            manager.loadExperimentData(18000000113L, "In Vivo 34616046_2", 1, 5);
            manager.loadExperimentData(18000000114L, "In Vivo 34616046_3", 2, 5);

            manager.loadExperimentData(18000000150L, "In Vivo 36701517_1", 1, 5);
            manager.loadExperimentData(18000000151L, "In Vivo 36701517_2", 1, 5);
            manager.loadExperimentData(18000000152L, "In Vivo 36701517_3", 1, 5);
            manager.loadExperimentData(18000000153L, "In Vivo 36701517_4", 1, 5);

            manager.loadExperimentData(18000000154L, "In Vivo 35970837_1", 1, 5);
            manager.loadExperimentData(18000000155L, "In Vivo 35970837_2", 1, 5);
            manager.loadExperimentData(18000000156L, "In Vivo 35970837_3", 3, 5);

            manager.loadExperimentData(18000000157L, "In Vivo 38437539_1", 2, 5);
            manager.loadExperimentData(18000000158L, "In Vivo 38437539_2", 2, 5);
            manager.loadExperimentData(18000000159L, "In Vivo 38437539_3", 2, 5);
            manager.loadExperimentData(18000000160L, "In Vivo 38437539_4", 2, 5);
            manager.loadExperimentData(18000000161L, "In Vivo 38437539_5", 2, 5);
            manager.loadExperimentData(18000000162L, "In Vivo 38437539_6", 1, 5);
            manager.loadExperimentData(18000000163L, "In Vivo 38437539_7", 1, 5);
            manager.loadExperimentData(18000000164L, "In Vivo 38437539_8", 1, 5);
            manager.loadExperimentData(18000000165L, "In Vivo 38437539_9", 1, 5);

        } catch (Exception e) {
            Utils.printStackTrace(e, manager.getLog());
        }
    }
}
