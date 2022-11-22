package edu.mcw.scge.load;

import edu.mcw.scge.Manager;
import edu.mcw.scge.Mean;

// study loaded on DEV on Nov 22, 2022

public class Murray_TLR2 {

    public static void main(String[] args) {

        Manager manager = Manager.getManagerInstance();

        manager.studyId = 1068;
        manager.fileName = "data/Murray_TLR2-1068-1.xlsx";
        manager.tier = 0;

        try {
            manager.info("LOAD FROM FILE "+manager.fileName+" "+manager.expType);
            manager.experimentId = 18000000070L;
            manager.expType = "In Vitro";

            int rowsDeleted = manager.getDao().deleteExperimentData(manager.experimentId);
            manager.info("=== deleted rows : "+rowsDeleted);

            // 4 columns of numeric data
            for( int column=3; column<3+6; column++ ) { // 0-based column in the excel sheet
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