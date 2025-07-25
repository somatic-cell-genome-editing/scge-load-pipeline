package edu.mcw.scge.load;

import edu.mcw.rgd.process.Utils;
import edu.mcw.scge.Manager;

//BCM SATC Lam
// loaded on DEV on June 2, 2025
// loaded on DEV,STAGE on June 10, 2025

public class Lam2 {

    public static void main(String[] args) {

        Manager manager = Manager.getManagerInstance();

        manager.studyId = 1095;
        manager.fileName = "data/Lam-1095-1.xlsx";
        manager.tier = 0;

        try {

            manager.loadExperimentData(18000000143L, "In Vivo", 4, 5);

        } catch (Exception e) {
            Utils.printStackTrace(e, manager.getLog());
        }
    }
}
