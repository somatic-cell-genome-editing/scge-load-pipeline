package edu.mcw.scge.load;

import edu.mcw.rgd.process.Utils;
import edu.mcw.scge.Manager;

// loaded on DEV on Nov 22, 2022
// loaded on DEV on Nov 28, 2022
// loaded on DEV on Dec 13, 2022
// reloaded on DEV on Jan 19, 2023
// loaded on PROD on Jan 30, 2023
// loaded on DEV/STAGE on Feb 13, 2023
// loaded on DEV/STAGE on Feb 21, 2024

public class Murray_GER12 {

    public static void main(String[] args) throws Exception {

        Manager manager = Manager.getManagerInstance();

        manager.studyId = 1076;
        manager.fileName = "data/Murray_GER12-1076-6.xlsx";
        manager.tier = 0;

        try {
            manager.loadExperimentData(18000000073L, "In Vitro", 2, 5);

        } catch (Exception e) {
            Utils.printStackTrace(e, manager.getLog());
        }
    }
}
