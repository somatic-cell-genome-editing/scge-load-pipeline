package edu.mcw.scge.load;

import edu.mcw.rgd.process.Utils;
import edu.mcw.scge.Manager;

// study loaded on DEV on Aug 14, 2024


public class Leong2 {

    public static void main(String[] args) throws Exception {

        Manager manager = Manager.getManagerInstance();

        manager.studyId = 1086;
        manager.fileName = "data/Leong2-1086-1.xlsx";
        manager.tier = 0;

        try {
            manager.loadExperimentData(18000000116L, "In Vivo", 6, 5);

        } catch (Exception e) {
            Utils.printStackTrace(e, manager.getLog());
        }
    }
}
