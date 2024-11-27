package edu.mcw.scge.load;

import edu.mcw.rgd.process.Utils;
import edu.mcw.scge.Manager;

// loaded on DEV on Nov 27, 2024

public class Freedman {

    public static void main(String[] args) throws Exception {

        Manager manager = Manager.getManagerInstance();

        manager.studyId = 1049;
        manager.fileName = "data/Freedman-1049-1.xlsx";
        manager.tier = 0;

        try {
            manager.loadExperimentData(18000000131L, "In Vitro", 3, 5);

        } catch (Exception e) {
            Utils.printStackTrace(e, manager.getLog());
        }
    }
}
