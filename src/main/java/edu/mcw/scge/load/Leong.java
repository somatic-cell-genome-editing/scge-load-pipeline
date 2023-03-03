package edu.mcw.scge.load;

import edu.mcw.rgd.process.Utils;
import edu.mcw.scge.Manager;
import edu.mcw.scge.Mean;

// study loaded on DEV on Nov 16, 2022
// study loaded on DEV on Nov 22, 2022
// study loaded on DEV/PROD on Dec 19, 2022
// study loaded on DEV/PROD on Dec 20, 2022
// study loaded on DEV/PROD on Jan 16, 2023
// reloaded on DEV/STAGE Mar 01, 2023

public class Leong {

    public static void main(String[] args) throws Exception {

        Manager manager = Manager.getManagerInstance();

        manager.studyId = 1064;
        manager.fileName = "data/Leong-1064-6.xlsx";
        manager.tier = 0;

        try {
            manager.loadExperimentNumericData(18000000059L, "In Vivo", 4);

        } catch (Exception e) {
            Utils.printStackTrace(e, manager.getLog());
        }
    }
}
