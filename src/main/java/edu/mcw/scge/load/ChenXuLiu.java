package edu.mcw.scge.load;

import edu.mcw.rgd.process.Utils;
import edu.mcw.scge.Manager;
import edu.mcw.scge.Mean;

// study loaded on Nov 16, 2022
// revised data loaded on Nov 21, 2022
// revised data loaded on Nov 23, 2022
public class ChenXuLiu {

    public static void main(String[] args) {

        Manager manager = Manager.getManagerInstance();

        manager.studyId = 1072;
        manager.fileName = "data/ChenXuLiu-1072-1.xlsx";
        manager.tier = 0;
        manager.experimentId = 18000000065L;
        manager.expType = "In Vivo - Fig1-3 ";

        try {

            int rowsDeleted = manager.getDao().deleteExperimentData(manager.experimentId, manager.studyId);
            manager.info("=== deleted rows for experiment "+manager.experimentId+": "+rowsDeleted);
            boolean mergeExpRecs = true;

            // 5 columns of numeric data
            for( int column=3; column<3+5; column++ ) { // 0-based column in the excel sheet
                String name = "Condition 1"; //exp record name to be loaded, if not present
                manager.loadMetaData(column, name, false, mergeExpRecs);
            }
            manager.info("=== numeric metadata loaded");

            Mean.loadMean(manager.experimentId, manager);

        } catch (Exception e) {
            Utils.printStackTrace(e, manager.getLog());
        }
    }
}
