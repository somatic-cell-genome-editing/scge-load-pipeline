package edu.mcw.scge.load;

import edu.mcw.rgd.process.Utils;
import edu.mcw.scge.Manager;
import edu.mcw.scge.Mean;

// study loaded on DEV on Nov 28, 2022
// study loaded on DEV on Dec 13, 2022

public class Murray_GER14 {

    public static void main(String[] args) throws Exception {

        Manager manager = Manager.getManagerInstance();

        manager.studyId = 1078;
        manager.fileName = "data/Murray_GER14-1078-1.xlsx";
        manager.tier = 0;

        try {
            manager.experimentId = 18000000075L;
            manager.expType = "In Vitro";
            manager.info("LOAD FROM FILE "+manager.fileName+" "+manager.expType);

            int rowsDeleted = manager.getDao().deleteExperimentData(manager.experimentId, manager.studyId);
            manager.info("=== deleted rows : "+rowsDeleted);

            // 4 columns of numeric data
            for( int column=3; column<3+4; column++ ) { // 0-based column in the excel sheet
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
