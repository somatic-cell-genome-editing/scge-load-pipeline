package edu.mcw.scge.load;

import edu.mcw.rgd.process.Utils;
import edu.mcw.scge.Manager;

// loaded on DEV on Nov 20, 2023


public class COF25 {

    public static void main(String[] args) {

        Manager manager = Manager.getManagerInstance();

        manager.studyId = 1090;
        manager.fileName = "data/COF25-1090-1.xlsx";
        manager.tier = 0;

        try {

            manager.loadExperimentData(18000000106L, "In Vitro", 14, 5);

        } catch (Exception e) {
            Utils.printStackTrace(e, manager.getLog());
        }
    }
}
