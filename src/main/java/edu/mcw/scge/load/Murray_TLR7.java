package edu.mcw.scge.load;

import edu.mcw.rgd.process.Utils;
import edu.mcw.scge.Manager;
import edu.mcw.scge.Mean;

// study loaded on DEV on Nov 22, 2022
// study loaded on DEV on Nov 28, 2022
// study loaded on DEV on Dec 13, 2022

public class Murray_TLR7 {

    public static void main(String[] args) {

        Manager manager = Manager.getManagerInstance();

        manager.studyId = 1077;
        manager.fileName = "data/Murray_TLR7-1077-2.xlsx";
        manager.tier = 0;

        try {
            manager.experimentId = 18000000074L;
            manager.expType = "In Vitro";
            manager.info("LOAD FROM FILE "+manager.fileName+" "+manager.expType);

            int rowsDeleted = manager.getDao().deleteExperimentData(manager.experimentId, manager.studyId);
            manager.info("=== deleted rows : "+rowsDeleted);

            // 2 columns of numeric data
            for( int column=3; column<3+2; column++ ) { // 0-based column in the excel sheet
                String name = "Condition 1"; //exp record name to be loaded, if not present
                manager.loadMetaData(column, name, false, true);
            }
            manager.info("=== numeric metadata loaded");

            Mean.loadMean(manager.experimentId, manager);

        } catch (Exception e) {
            Utils.printStackTrace(e, manager.getLog());
        }

        manager.finish();
    }
}
