package edu.mcw.scge.load;

import edu.mcw.rgd.process.Utils;
import edu.mcw.scge.Manager;

// loaded on DEV on Sep 09, 2025
// loaded on DEV and STAGE on Nov 12, 2025

public class Bulte_1088 {

    public static void main(String[] args) {

        Manager manager = Manager.getManagerInstance();

        manager.studyId = 1088;
        manager.fileName = "data/Bulte-1088-1.xlsx";
        manager.tier = 0;

        try {

            manager.loadExperimentData(18000000146L, "In Vivo", 1, 3);

        } catch (Exception e) {
            Utils.printStackTrace(e, manager.getLog());
        }
    }
}
