package edu.mcw.scge.load;

import edu.mcw.rgd.process.Utils;
import edu.mcw.scge.Manager;

// study loaded on DEV on Sep 20, 2023
// v2 study loaded on DEV/STAGE on Sep 22, 2023

public class Morizane {

    public static void main(String[] args) throws Exception {

        Manager manager = Manager.getManagerInstance();

        manager.studyId = 1020;
        manager.fileName = "data/Morizane-1020-2.xlsx";
        manager.tier = 0;

        try {
            manager.loadExperimentData(18000000092L, "In Vitro", 4, 5);
            manager.loadExperimentData(18000000093L, "In Vitro (2)", 16, 5);
            manager.loadExperimentData(18000000094L, "In Vitro (3)", 5, 5);
            manager.loadExperimentData(18000000095L, "In Vitro (4)", 4, 5);
            manager.loadExperimentData(18000000096L, "In Vitro (5)", 4, 5);
            manager.loadExperimentData(18000000097L, "In Vitro (6)", 9, 5);
            manager.loadExperimentData(18000000098L, "In Vitro (7)", 3, 5);

        } catch (Exception e) {
            Utils.printStackTrace(e, manager.getLog());
        }
    }
}
