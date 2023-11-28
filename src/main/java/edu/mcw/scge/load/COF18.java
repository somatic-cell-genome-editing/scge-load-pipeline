package edu.mcw.scge.load;

import edu.mcw.rgd.process.Utils;
import edu.mcw.scge.Manager;

// loaded on DEV on Nov 17, 2023
// loaded on STAGE on Nov 28, 2023

public class COF18 {

    public static void main(String[] args) {

        Manager manager = Manager.getManagerInstance();

        manager.studyId = 1071;
        manager.fileName = "data/COF18-1071-1.xlsx";
        manager.tier = 0;

        try {

            manager.loadExperimentData(18000000103L, "In Vitro", 4, 5);
            manager.loadExperimentData(18000000104L, "In Vitro (2)", 12, 5);
            manager.loadExperimentData(18000000105L, "In Vitro (3)", 5, 5);

        } catch (Exception e) {
            Utils.printStackTrace(e, manager.getLog());
        }
    }
}
