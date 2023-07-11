package edu.mcw.scge.load;

import edu.mcw.rgd.process.Utils;
import edu.mcw.scge.Manager;

// loaded on DEV on Jan 30, 2023
// loaded on DEV on May 30, 2023
// loaded on DEV on Jun 08, 2023

public class COF26 {

    public static void main(String[] args) {

        Manager manager = Manager.getManagerInstance();

        manager.studyId = 1073;
        manager.fileName = "data/COF26-1073-6.xlsx";
        manager.tier = 0;

        try {

            manager.loadExperimentRecordsWithNoDataSeries = false;
            manager.loadExperimentNumericData(18000000086L, "In Vitro", 3);

        } catch (Exception e) {
            Utils.printStackTrace(e, manager.getLog());
        }
    }
}
