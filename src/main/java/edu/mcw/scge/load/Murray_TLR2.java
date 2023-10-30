package edu.mcw.scge.load;

import edu.mcw.rgd.process.Utils;
import edu.mcw.scge.Manager;

// loaded on DEV on Nov 22, 2022
// reloaded on DEV on Jan 19, 2023
// reloaded on DEV on Jan 23, 2023
// reloaded on DEV/STAGE on Feb 07, 2023
// reloaded on DEV/STAGE on Sep 27, 2023

public class Murray_TLR2 {

    public static void main(String[] args) throws Exception {

        Manager manager = Manager.getManagerInstance();

        manager.studyId = 1068;
        manager.fileName = "data/Murray_TLR2-1068-8.xlsx";
        manager.tier = 0;

        try {
            manager.loadExperimentData(18000000070L, "In Vitro", 6, 5);
            manager.loadExperimentData(18000000071L, "In Vitro (2)", 3, 5);
            manager.loadExperimentData(18000000072L, "In Vitro (3)", 3, 5);

        } catch (Exception e) {
            Utils.printStackTrace(e, manager.getLog());
        }
    }
}
