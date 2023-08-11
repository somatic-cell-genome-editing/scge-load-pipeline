package edu.mcw.scge.load;

import edu.mcw.scge.Manager;
import edu.mcw.scge.Mean;

// study loaded on Nov 15, 2022
// study reloaded on Nov 18, 2022

public class Gao {

    public static void main(String[] args) {

        Manager manager = Manager.getManagerInstance();

        manager.fileName = "data/Gao3.xlsx";
        manager.tier = 0;

        try {
            manager.studyId = 1005;
            manager.experimentId = 18000000012L;

            int rowsDeleted = manager.getDao().deleteExperimentData(manager.experimentId, manager.studyId);
            manager.info("=== deleted rows for experiment "+manager.experimentId+": "+rowsDeleted);

            manager.expType = "In Vivo";
            for( int column = 3; column < 3+3; column++ ) { // 0-based column in the excel sheet
                String name = "Condition 1"; //exp record name to be loaded, if not present
                manager.loadMetaData(column, name);
            }
            Mean.loadMean(manager.experimentId, manager);

            ///////

            manager.studyId = 1051;
            manager.experimentId = 18000000069L;

            rowsDeleted = manager.getDao().deleteExperimentData(manager.experimentId, manager.studyId);
            manager.info("=== deleted rows for experiment "+manager.experimentId+": "+rowsDeleted);

            manager.expType = "In Vivo (2)";
            for (int column = 3; column < 3+2; column++) { // 0-based column in the excel sheet
                String name = "Condition 1"; //exp record name to be loaded, if not present
                manager.loadMetaData(column, name);
            }
            Mean.loadMean(manager.experimentId, manager);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
