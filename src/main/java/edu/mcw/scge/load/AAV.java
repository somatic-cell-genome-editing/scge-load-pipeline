package edu.mcw.scge.load;

import edu.mcw.rgd.process.Utils;
import edu.mcw.scge.Manager;

// loaded on DEV on Nov 20, 2023

public class AAV {

    public static void main(String[] args) {

        Manager manager = Manager.getManagerInstance();

        manager.studyId = 1093;
        manager.fileName = "data/AAV-1093-2.xlsx";
        manager.tier = 0;

        try {

            manager.loadExperimentData(18000000108L, "In Vivo", 5, 5);

        } catch (Exception e) {
            Utils.printStackTrace(e, manager.getLog());
        }
    }
}
