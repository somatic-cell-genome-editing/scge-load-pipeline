package edu.mcw.scge.load;

import edu.mcw.rgd.process.Utils;
import edu.mcw.scge.Manager;

// loaded on DEV on Nov 27, 2024

public class COF34 {

    public static void main(String[] args) throws Exception {

        Manager manager = Manager.getManagerInstance();

        manager.studyId = 1097;
        manager.fileName = "data/COF34-1097-1.xlsx";
        manager.tier = 0;

        try {
            manager.loadExperimentData(18000000123L, "In Vivo", 4, 5);
            manager.loadExperimentData(18000000124L, "In Vivo (2)", 8, 5);

        } catch (Exception e) {
            Utils.printStackTrace(e, manager.getLog());
        }
    }
}
