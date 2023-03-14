package edu.mcw.scge.load;

import edu.mcw.rgd.process.Utils;
import edu.mcw.scge.Manager;

// loaded on DEV on Jan 20, 2023
// loaded on DEV/STAGE on Mar 03, 2023
// loaded on DEV/STAGE on Mar 13, 2023

public class COF10 {

    public static void main(String[] args) {

        Manager manager = Manager.getManagerInstance();

        manager.studyId = 1069;
        manager.fileName = "data/COF10-1069-3.xlsx";
        manager.tier = 0;

        try {

            manager.loadExperimentNumericData(18000000079L, "In Vitro - Fig1", 10);
            manager.loadExperimentNumericData(18000000080L, "In Vitro - Fig2", 11);
            manager.loadExperimentNumericData(18000000081L, "In Vitro - Fig3", 9);
            manager.loadExperimentNumericData(18000000082L, "In Vitro - Fig4", 7);
            manager.loadExperimentNumericData(18000000083L, "In Vitro - Fig5", 8);
            manager.loadExperimentNumericData(18000000084L, "In Vitro - Fig6", 6);

            manager.loadExperimentNumericData(18000000085L, "In Vivo - Fig8", 7);

        } catch (Exception e) {
            Utils.printStackTrace(e, manager.getLog());
        }
    }
}
