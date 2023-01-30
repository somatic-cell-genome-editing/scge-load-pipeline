package edu.mcw.scge.load;

import edu.mcw.rgd.process.Utils;
import edu.mcw.scge.Manager;

// loaded on DEV on Nov 28, 2022
// loaded on DEV on Dec 13, 2022
// loaded on DEV on Jan 19, 2023
// loaded on PROD on Jan 30, 2023

public class Murray_GER18 {

    public static void main(String[] args) throws Exception {

        Manager manager = Manager.getManagerInstance();

        manager.studyId = 1079;
        manager.fileName = "data/Murray_GER18-1079-2.xlsx";
        manager.tier = 0;

        try {
            manager.loadExperimentNumericData(18000000076L, "In Vitro", 1);

            manager.finish();
        } catch (Exception e) {
            Utils.printStackTrace(e, manager.getLog());
        }
    }
}
