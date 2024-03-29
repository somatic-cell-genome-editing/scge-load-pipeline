package edu.mcw.scge.load;

import edu.mcw.rgd.process.Utils;
import edu.mcw.scge.Manager;

// study loaded on Nov 21, 2022
// study loaded on DEV Jan 30, 2023
public class Curiel {

    public static void main(String[] args) throws Exception {

        Manager manager = Manager.getManagerInstance();

        manager.studyId = 1067;
        manager.experimentId = 18000000009L;
        manager.fileName = "data/Curiel-1067-1.xlsx";
        manager.expType = "In Vivo";
        manager.tier = 0;

        try {
            manager.loadExperimentSignalData(18000000009L, "In Vivo", 4);

        } catch (Exception e) {
            Utils.printStackTrace(e, manager.getLog());
        }
    }
}
