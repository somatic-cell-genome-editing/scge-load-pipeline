package edu.mcw.scge.load;

import edu.mcw.rgd.process.Utils;
import edu.mcw.scge.Manager;

// study loaded on DEV on Jul 22, 2025
// study loaded on STAGE on Sep 08, 2025

public class Curiel_1094 {

    public static void main(String[] args) throws Exception {

        Manager manager = Manager.getManagerInstance();

        manager.studyId = 1094;
        manager.experimentId = 18000000144L;
        manager.fileName = "data/Curiel-1094-1.xlsx";
        manager.expType = "In Vivo";
        manager.tier = 0;

        try {
            manager.loadExperimentData(18000000144L, "In Vivo", 3, 5);

        } catch (Exception e) {
            Utils.printStackTrace(e, manager.getLog());
        }
    }
}
