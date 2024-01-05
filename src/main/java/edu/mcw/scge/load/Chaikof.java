package edu.mcw.scge.load;

import edu.mcw.rgd.process.Utils;
import edu.mcw.scge.Manager;

// loaded on DEV on Nov 20, 2023
// loaded on DEV on Nov 21, 2023
// loaded on DEV on Dec 12, 2023
// loaded on STAGE on Dec 18, 2023

public class Chaikof {

    public static void main(String[] args) {

        Manager manager = Manager.getManagerInstance();

        manager.studyId = 1091;
        manager.fileName = "data/Chaikof-1091-3.xlsx";
        manager.tier = 0;

        try {

            manager.loadExperimentData(18000000107L, "In Vivo", 4, 5);

        } catch (Exception e) {
            Utils.printStackTrace(e, manager.getLog());
        }
    }
}
