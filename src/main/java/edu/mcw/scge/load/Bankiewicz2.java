package edu.mcw.scge.load;

import edu.mcw.rgd.process.Utils;
import edu.mcw.scge.Manager;

// loaded on DEV on Aug 14, 2024


public class Bankiewicz2 {

    public static void main(String[] args) {

        Manager manager = Manager.getManagerInstance();

        manager.studyId = 1085;
        manager.fileName = "data/Bankiewicz2-1085-1.xlsx";
        manager.tier = 0;

        try {
            manager.loadExperimentData(18000000117L, "In Vivo", 4, 5);

        } catch (Exception e) {
            Utils.printStackTrace(e, manager.getLog());
        }
    }
}
