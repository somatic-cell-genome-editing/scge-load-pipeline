package edu.mcw.scge.load;

import edu.mcw.rgd.process.Utils;
import edu.mcw.scge.Manager;

// loaded on DEV on Aug 14, 2024

public class Murray2_TLR2 {

    public static void main(String[] args) throws Exception {

        Manager manager = Manager.getManagerInstance();

        manager.studyId = 1081;
        manager.fileName = "data/Murray2_TLR2-1081-1.xlsx";
        manager.tier = 0;

        try {
            manager.loadExperimentData(18000000119L, "In Vitro", 6, 5);
            manager.loadExperimentData(18000000120L, "In Vitro (2)", 3, 5);
            manager.loadExperimentData(18000000121L, "In Vitro (3)", 3, 5);
            manager.loadExperimentData(18000000122L, "In Vivo", 2, 5);

        } catch (Exception e) {
            Utils.printStackTrace(e, manager.getLog());
        }
    }
}
