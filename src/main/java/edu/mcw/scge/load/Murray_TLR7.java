package edu.mcw.scge.load;

import edu.mcw.rgd.process.Utils;
import edu.mcw.scge.Manager;

/*
 loaded on DEV on Nov 22, 2022
 loaded on DEV on Nov 28, 2022
 loaded on DEV on Dec 13, 2022
 loaded on DEV on Jan 19, 2023
 loaded on DEV/STAGE on Feb 07, 2023
 loaded on DEV/STAGE on Feb 13, 2023
 loaded on DEV on Aug 09, 2023 -- schema v. 5.7, data in column 5, not 3
 loaded on DEV/STAGE on Aug 11, 2023
 loaded on DEV/STAGE on Sep 06, 2023
 loaded on DEV/STAGE on Apr 05, 2024 -- 2 sheets with 1 data col; previously 1 sheet with 2 data cols
                                     -- added new experiment 18000000115
*/

public class Murray_TLR7 {

    public static void main(String[] args) throws Exception {

        Manager manager = Manager.getManagerInstance();

        manager.studyId = 1077;
        manager.fileName = "data/Murray_TLR7-1077-7.xlsx";
        manager.tier = 0;

        try {
            manager.loadExperimentData(18000000074L, "In Vitro", 1, 5);
            manager.loadExperimentData(18000000115L, "In Vitro (2)", 1, 5);

        } catch (Exception e) {
            Utils.printStackTrace(e, manager.getLog());
        }
    }
}
