package edu.mcw.scge.load;

import edu.mcw.rgd.process.Utils;
import edu.mcw.scge.Manager;
import edu.mcw.scge.Mean;

// loaded on DEV on Jan 20, 2023

public class Murray_GER10 {

    public static void main(String[] args) throws Exception {

        Manager manager = Manager.getManagerInstance();

        manager.studyId = 1075;
        manager.fileName = "data/Murray_GER10-1075-1.xlsx";
        manager.tier = 0;

        try {
            manager.experimentId = 18000000078L;
            manager.expType = "In Vitro";
            manager.info("LOAD FROM FILE "+manager.fileName+" "+manager.expType);

            int rowsDeleted = manager.getDao().deleteExperimentData(manager.experimentId, manager.studyId);
            manager.info("=== deleted rows : "+rowsDeleted);

            // 1 column of numeric data
            for( int column=3; column<3+1; column++ ) { // 0-based column in the excel sheet
                String name = "Condition 1"; //exp record name to be loaded, if not present
                manager.loadMetaData(column, name, false);
            }
            manager.info("=== numeric metadata loaded");

            Mean.loadMean(manager.experimentId, manager);

        } catch (Exception e) {
            Utils.printStackTrace(e, manager.getLog());
        }

        manager.finish();
    }
}
