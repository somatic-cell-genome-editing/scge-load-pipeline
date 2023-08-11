package edu.mcw.scge.load;

import edu.mcw.rgd.process.Utils;
import edu.mcw.scge.Manager;

// loaded on DEV on May 30, 2023

public class Gong {

    public static void main(String[] args) {

        Manager manager = Manager.getManagerInstance();

        manager.studyId = 1083;
        manager.fileName = "data/Gong-1083-4.xlsx";
        manager.tier = 0;

        try {

            manager.loadExperimentSignalData(18000000091L, "In Vivo", 6);

        } catch (Exception e) {
            Utils.printStackTrace(e, manager.getLog());
        }

    }
}
