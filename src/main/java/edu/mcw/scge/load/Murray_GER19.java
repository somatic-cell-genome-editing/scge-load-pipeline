package edu.mcw.scge.load;

import edu.mcw.rgd.process.Utils;
import edu.mcw.scge.Manager;

// loaded on DEV on Nov 28, 2022
// loaded on DEV on Dec 13, 2022
// loaded on DEV on Jan 19, 2023
// loaded on PROD on Jan 30, 2023
// loaded on DEV/STAGE on Feb 13, 2023
// loaded on DEV/STAGE on Sep 27, 2023

public class Murray_GER19 {

    public static void main(String[] args) throws Exception {

        Manager manager = Manager.getManagerInstance();

        manager.studyId = 1080;
        manager.fileName = "data/Murray_GER19-1080-3.xlsx";
        manager.tier = 0;

        try {
            manager.loadExperimentData(18000000077L, "In Vitro", 1, 5);

        } catch (Exception e) {
            Utils.printStackTrace(e, manager.getLog());
        }
    }
}
