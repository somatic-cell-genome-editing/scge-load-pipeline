package edu.mcw.scge.load;

import edu.mcw.rgd.process.Utils;
import edu.mcw.scge.Manager;

// study loaded on DEV on May 15, 2023

public class Chen {

    public static void main(String[] args) {

        Manager manager = Manager.getManagerInstance();

        manager.studyId = 1082;
        manager.fileName = "data/Chen-1082-2.xlsx";
        manager.tier = 0;

        try {

            manager.loadExperimentSignalData(18000000089L, "In Vivo", 3);
            manager.loadExperimentSignalData(18000000090L, "In Vivo (2)", 2);

        } catch (Exception e) {
            Utils.printStackTrace(e, manager.getLog());
        }

    }
}
