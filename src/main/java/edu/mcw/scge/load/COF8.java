package edu.mcw.scge.load;

import edu.mcw.rgd.process.Utils;
import edu.mcw.scge.Manager;

// study loaded on Nov 16, 2022
// revised data loaded on Nov 21, 2022
// revised data loaded on Nov 23, 2022
// revised data loaded on Jan 05, 2023
// revised data loaded on Jan 30, 2023
// loaded on DEV on Mar 3, 2023
// loaded on STAGE on Mar 7, 2023
// loaded on DEV/STAGE on Mar 13, 2023

public class COF8 {

    public static void main(String[] args) {

        Manager manager = Manager.getManagerInstance();

        manager.studyId = 1072;
        manager.fileName = "data/COF8-1072-5.xlsx";
        manager.tier = 0;

        try {
            manager.loadExperimentNumericData(18000000065L, "In Vivo - Fig1-3 ", 5);

        } catch (Exception e) {
            Utils.printStackTrace(e, manager.getLog());
        }
    }
}
