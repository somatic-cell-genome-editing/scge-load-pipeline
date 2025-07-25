package edu.mcw.scge.load;

import edu.mcw.rgd.process.Utils;
import edu.mcw.scge.Manager;

// loaded on DEV on May 27-28, 2025
// loaded on STAGE on May 28, 2025


public class Gersbach {

    public static void main(String[] args) {

        Manager manager = Manager.getManagerInstance();

        manager.studyId = 1112;
        manager.fileName = "data/Gersbach-1112-3.xlsx";
        manager.tier = 0;

        try {

            manager.loadExperimentData(18000000141L, "In Vitro", 18, 5);

        } catch (Exception e) {
            Utils.printStackTrace(e, manager.getLog());
        }
    }
}
