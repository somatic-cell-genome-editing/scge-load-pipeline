package edu.mcw.scge.load;

import edu.mcw.rgd.process.Utils;
import edu.mcw.scge.Manager;

// loaded on Aug 14, 2024

public class Sontheimer2 {

    public static void main(String[] args) {

        Manager manager = Manager.getManagerInstance();

        manager.studyId = 1084;
        manager.fileName = "data/Sontheimer2-1084-1.xlsx";
        manager.tier = 0;

        try {

            manager.loadExperimentData(18000000118L, "In Vivo", 4, 5);

        } catch (Exception e) {
            Utils.printStackTrace(e, manager.getLog());
        }
    }
}
