package edu.mcw.scge.load;

import edu.mcw.rgd.process.Utils;
import edu.mcw.scge.Manager;

// loaded on DEV on Nov 20, 2023
// loaded on DEV on Jan 05, 2024
// loaded on DEV on Jan 09, 2024 -- 2 sheets
// loaded on DEV on Jan 16, 2024 -- 2 sheets

public class AAV {

    public static void main(String[] args) {

        Manager manager = Manager.getManagerInstance();

        manager.studyId = 1093;
        manager.fileName = "data/AAV-1093-6.xlsx";
        manager.tier = 0;

        try {

            manager.loadExperimentData(18000000108L, "In Vivo", 5, 5);

            //manager.loadExperimentData(18000000109L, "In Vivo (2)", 5, 5); // --TEST
            manager.loadExperimentData(18000000110L, "In Vivo (2)", 3, 5);

        } catch (Exception e) {
            Utils.printStackTrace(e, manager.getLog());
        }
    }
}
