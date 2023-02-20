package edu.mcw.scge.load;

import edu.mcw.rgd.process.Utils;
import edu.mcw.scge.Manager;

// loaded on DEV on Jan 20, 2023
// loaded on DEV/STAGE on Feb 07, 2023
// loaded on DEV/STAGE on Feb 13, 2023

public class Murray_GER10 {

    public static void main(String[] args) throws Exception {

        Manager manager = Manager.getManagerInstance();

        manager.studyId = 1075;
        manager.fileName = "data/Murray_GER10-1075-2.xlsx";
        manager.tier = 0;

        try {
            manager.loadExperimentNumericData(18000000078L, "In Vitro", 1);

        } catch (Exception e) {
            Utils.printStackTrace(e, manager.getLog());
        }
    }
}
