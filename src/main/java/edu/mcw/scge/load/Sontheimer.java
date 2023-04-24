package edu.mcw.scge.load;

import edu.mcw.rgd.process.Utils;
import edu.mcw.scge.Manager;

// study loaded on Nov 21, 2022
// reloaded on Apr 21, 2023

public class Sontheimer {

    public static void main(String[] args) {

        Manager manager = Manager.getManagerInstance();

        manager.studyId = 1048;
        manager.fileName = "data/Sontheimer-1048-2.xlsx";
        manager.tier = 0;

        try {

            manager.loadExperimentNumericData(18000000068L, "In Vivo", 4);

        } catch (Exception e) {
            Utils.printStackTrace(e, manager.getLog());
        }
    }
}
