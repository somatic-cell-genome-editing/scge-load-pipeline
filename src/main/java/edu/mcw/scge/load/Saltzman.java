package edu.mcw.scge.load;

import edu.mcw.rgd.process.Utils;
import edu.mcw.scge.Manager;

// study loaded on Nov 15, 2022
// reloaded on Apr 21, 2023

public class Saltzman {

    public static void main(String[] args) {

        Manager manager = Manager.getManagerInstance();

        manager.studyId = 1047;
        manager.fileName = "data/Saltzman-1047-2.xlsx";
        manager.tier = 0;

        try {

            manager.loadExperimentRecordsWithNoDataSeries = false;
            manager.loadExperimentNumericData(18000000058L, "In Vivo", 26);

        } catch (Exception e) {
            Utils.printStackTrace(e, manager.getLog());
        }

    }
}
