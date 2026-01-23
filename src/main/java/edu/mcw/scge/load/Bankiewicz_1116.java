package edu.mcw.scge.load;

import edu.mcw.rgd.process.Utils;
import edu.mcw.scge.Manager;

// loaded on DEV on Dec 09, 2025


public class Bankiewicz_1116 {

    public static void main(String[] args) {

        Manager manager = Manager.getManagerInstance();

        manager.studyId = 1116;
        manager.fileName = "data/Bankiewicz-1116-2.xlsx";
        manager.tier = 0;

        try {
            manager.loadExperimentData(18000000166L, "In Vivo", 4, 5);

        } catch (Exception e) {
            Utils.printStackTrace(e, manager.getLog());
        }
    }
}
