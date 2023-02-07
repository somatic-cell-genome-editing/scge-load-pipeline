package edu.mcw.scge.load;

import edu.mcw.rgd.process.Utils;
import edu.mcw.scge.Manager;

// loaded on DEV on Jan 30, 2023
// reloaded on DEV on Feb 06, 2023

public class AAV_Tropism {

    public static void main(String[] args) throws Exception {

        Manager manager = Manager.getManagerInstance();

        manager.studyId = 1001;
        manager.fileName = "data/AAV_tropism-1001-2.xlsx";
        manager.tier = 0;

        try {
            manager.loadExperimentNumericData(18000000087L, "In Vivo", 20);

        } catch (Exception e) {
            Utils.printStackTrace(e, manager.getLog());
        }
    }
}
