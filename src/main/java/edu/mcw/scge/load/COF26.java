package edu.mcw.scge.load;

import edu.mcw.rgd.process.Utils;
import edu.mcw.scge.Manager;

// loaded on DEV on Jan 30, 2023
// loaded on DEV on May 30, 2023
// loaded on DEV on Jun 08, 2023
// loaded on DEV on Jan 12, 2024
//    was study 1073, exp id = 18000000086L
//    it is now study 1099, exp id = 18000000111L
// loaded on STAGE on Jan 16, 2024

public class COF26 {

    public static void main(String[] args) {

        Manager manager = Manager.getManagerInstance();

        manager.studyId = 1099; // was 1073
        manager.fileName = "data/COF26-1099-1.xlsx";
        manager.tier = 0;

        try {

            //manager.loadExperimentNumericData(18000000086L, "In Vitro", 3);

            manager.loadExperimentData(18000000111L, "In Vitro", 3, 5);

        } catch (Exception e) {
            Utils.printStackTrace(e, manager.getLog());
        }
    }
}
