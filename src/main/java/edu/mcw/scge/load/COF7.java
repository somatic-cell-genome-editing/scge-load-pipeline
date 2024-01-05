package edu.mcw.scge.load;

import edu.mcw.rgd.process.Utils;
import edu.mcw.scge.Manager;

// loaded on DEV on Nov 17, 2023
// reloaded on DEV on Nov 20, 2023
// reloaded on DEV on Dec 12, 2023
// reloaded on DEV/STAGE on Dec 18, 2023


public class COF7 {

    public static void main(String[] args) {

        Manager manager = Manager.getManagerInstance();

        manager.studyId = 1074;
        manager.fileName = "data/COF7-1074-4.xlsx";
        manager.tier = 0;

        try {

            manager.loadExperimentData(18000000099L, "In Vitro 21-223", 5, 5);
            manager.loadExperimentData(18000000100L, "In Vitro 21-312", 5, 5);
            manager.loadExperimentData(18000000101L, "In Vitro 21-419", 8, 5);

            // the last sheet has been discontinued from loading as of COF7-1074-4 -- not enough data
            // this sheet does not have any data series, only 'not provided' -- force load it as signal
            //manager.forceLoadExperimentRecordsAsSignal = true;
            //manager.loadExperimentData(18000000102L, "In Vitro 21-826", 4, 5);

        } catch (Exception e) {
            Utils.printStackTrace(e, manager.getLog());
        }
    }
}
