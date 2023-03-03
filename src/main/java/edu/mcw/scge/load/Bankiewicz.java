package edu.mcw.scge.load;

import edu.mcw.rgd.process.Utils;
import edu.mcw.scge.Manager;

// study loaded on Nov 08, 2022
// reloaded on DEV/STAGE on Feb 07, 2023
// reloaded on DEV/STAGE Mar 01, 2023

public class Bankiewicz {

    public static void main(String[] args) {

        Manager manager = Manager.getManagerInstance();

        manager.studyId = 1066;
        manager.fileName = "data/Bankiewicz-1066-6.xlsx";
        manager.tier = 0;

        try {
            manager.loadExperimentNumericData(18000000060L, "In Vivo", 4);

        } catch (Exception e) {
            Utils.printStackTrace(e, manager.getLog());
        }
    }
}
