package edu.mcw.scge.load;

import edu.mcw.rgd.process.Utils;
import edu.mcw.scge.Manager;

// loaded on DEV on Oct 14, 2025

public class Asokan_1108 {

    public static void main(String[] args) {

        Manager manager = Manager.getManagerInstance();

        manager.studyId = 1108;
        manager.fileName = "data/Asokan-1108-1.xlsx";
        manager.tier = 0;

        try {

            manager.loadExperimentData(18000000147L, "In Vivo-IV", 2, 5);
            manager.loadExperimentData(18000000148L, "In Vivo-IM", 2, 5);

        } catch (Exception e) {
            Utils.printStackTrace(e, manager.getLog());
        }
    }
}
