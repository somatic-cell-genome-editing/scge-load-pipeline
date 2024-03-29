package edu.mcw.scge.load;

import edu.mcw.rgd.process.Utils;
import edu.mcw.scge.Manager;

// study loaded on DEV on Nov 22, 2022
// study loaded on DEV on Nov 28, 2022
// study loaded on DEV on Dec 13, 2022
// study loaded on DEV on Jan 19, 2023
// study loaded on DEV/STAGE on Feb 07, 2023
// study loaded on DEV/STAGE on Feb 13, 2023
// study loaded on DEV on Aug 09, 2023 -- schema v. 5.7, data in column 5, not 3
// study loaded on DEV/STAGE on Aug 11, 2023
// study loaded on DEV/STAGE on Sep 06, 2023

public class Murray_TLR7 {

    public static void main(String[] args) throws Exception {

        Manager manager = Manager.getManagerInstance();

        manager.studyId = 1077;
        manager.fileName = "data/Murray_TLR7-1077-6.xlsx";
        manager.tier = 0;

        try {
            manager.loadExperimentData(18000000074L, "In Vitro", 2, 5);

        } catch (Exception e) {
            Utils.printStackTrace(e, manager.getLog());
        }
    }
}
