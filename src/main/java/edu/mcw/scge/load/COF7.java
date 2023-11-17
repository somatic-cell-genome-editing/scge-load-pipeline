package edu.mcw.scge.load;

import edu.mcw.rgd.process.Utils;
import edu.mcw.scge.Manager;

// loaded on DEV on Nov 16, 2023


public class COF7 {

    public static void main(String[] args) {

        Manager manager = Manager.getManagerInstance();

        manager.studyId = 1074;
        manager.fileName = "data/COF7-1074-1.xlsx";
        manager.tier = 0;

        try {

            manager.loadExperimentData(18000000099L, "In Vitro 21-223", 5, 5);
            manager.loadExperimentData(18000000100L, "In Vitro 21-312", 5, 5);
            manager.loadExperimentData(18000000101L, "In Vitro 21-419", 8, 5);

            // this sheet does not have any data series -- don't load it
            //manager.loadExperimentData(18000000102L, "In Vitro 21-826", 4, 5);

        } catch (Exception e) {
            Utils.printStackTrace(e, manager.getLog());
        }
    }
}
