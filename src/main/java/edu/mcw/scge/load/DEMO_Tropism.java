package edu.mcw.scge.load;

import edu.mcw.rgd.process.Utils;
import edu.mcw.scge.Manager;

// loaded on DEV on Feb 10, 2023

public class DEMO_Tropism {

    public static void main(String[] args) throws Exception {

        Manager manager = Manager.getManagerInstance();

        manager.studyId = 1002;
        manager.fileName = "data/DEMO_tropism-1002-3.xlsx";
        manager.tier = 0;

        try {
            manager.loadExperimentNumericData(18000000088L, "In Vivo", 10);

        } catch (Exception e) {
            Utils.printStackTrace(e, manager.getLog());
        }
    }
}
