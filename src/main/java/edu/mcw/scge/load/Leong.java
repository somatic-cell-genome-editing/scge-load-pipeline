package edu.mcw.scge.load;

import edu.mcw.scge.Manager;
import edu.mcw.scge.Mean;

// study loaded on DEV on Nov 16, 2022
// study loaded on DEV on Nov 22, 2022
// study loaded on DEV/PROD on Dec 19, 2022
// study loaded on DEV/PROD on Dec 20, 2022

public class Leong {

    public static void main(String[] args) {

        Manager manager = Manager.getManagerInstance();

        manager.studyId = 1064;
        manager.fileName = "data/Leong-1064-3.xlsx";
        manager.tier = 0;
        manager.experimentId = 18000000059L;
        manager.expType = "In Vivo";

        try {
            manager.info("LOAD FROM FILE "+manager.fileName);

            int rowsDeleted = manager.getDao().deleteExperimentData(manager.experimentId, manager.studyId);
            manager.info("=== deleted rows : "+rowsDeleted);

            // 4 columns of numeric data
            for( int column=3; column<=6; column++ ) { // 0-based column in the excel sheet
                String name = "Condition 1"; //exp record name to be loaded, if not present
                manager.loadMetaData(column, name, false, true);
            }
            manager.info("=== numeric metadata loaded");

            // 2 last columns of absent/present data
            for( int column=7; column<=8; column++ ) { // 0-based column in the excel sheet
                String name = "Condition 1"; //exp record name to be loaded, if not present
                manager.loadMetaData(column, name, true, true);
            }
            manager.info(" === signal metadata loaded");

            Mean.loadMean(manager.experimentId, manager);

        } catch (Exception e) {
            e.printStackTrace();
        }

        manager.finish();
    }
}
