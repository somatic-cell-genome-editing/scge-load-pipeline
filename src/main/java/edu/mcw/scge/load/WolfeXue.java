package edu.mcw.scge.load;

import edu.mcw.rgd.process.Utils;
import edu.mcw.scge.Manager;

// loaded on DEV on May 27, 2025

public class WolfeXue {

    public static void main(String[] args) {

        Manager manager = Manager.getManagerInstance();

        manager.studyId = 1070;
        manager.fileName = "data/WolfeXue-1070-1.xlsx";
        manager.tier = 0;

        try {

            manager.loadExperimentData(18000000142L, "In Vitro", 10, 5);

        } catch (Exception e) {
            Utils.printStackTrace(e, manager.getLog());
        }
    }
}
