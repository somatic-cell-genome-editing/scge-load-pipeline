package edu.mcw.scge.load;

import edu.mcw.scge.Manager;
import edu.mcw.scge.Mean;

// study loaded on DEV on Nov 28, 2022

public class Murray_GER19 {

    public static void main(String[] args) {

        Manager manager = Manager.getManagerInstance();

        manager.studyId = 1080;
        manager.fileName = "data/Murray_GER19-1080-1.xlsx";
        manager.tier = 0;

        try {
            manager.experimentId = 18000000077L;
            manager.expType = "In Vitro - AL";
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
            e.printStackTrace();
        }

        manager.finish();
    }
}
