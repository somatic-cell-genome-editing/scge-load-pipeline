package edu.mcw.scge.load;

import edu.mcw.rgd.process.Utils;
import edu.mcw.scge.Manager;

// loaded on DEV on Jan 23, 2026

public class Gong_1115 {

    public static void main(String[] args) {

        Manager manager = Manager.getManagerInstance();

        manager.studyId = 1115;
        manager.fileName = "data/Gong-1115-1.xlsx";
        manager.tier = 0;

        try {

            manager.loadExperimentData(18000000167L, "In Vivo", 3, 3);

        } catch (Exception e) {
            Utils.printStackTrace(e, manager.getLog());
        }

    }
}
