package edu.mcw.scge.load;

import edu.mcw.scge.Manager;
import edu.mcw.scge.Mean;

// study loaded on Nov 21, 2022
public class Curiel {

    public static void main(String[] args) {

        Manager manager = Manager.getManagerInstance();

        manager.studyId = 1067;
        manager.experimentId = 18000000009L;
        manager.fileName = "data/Curiel-1067-1.xlsx";
        manager.expType = "In Vivo";
        manager.tier = 0;

        try {
            int rowsDeleted = manager.getDao().deleteExperimentData(manager.experimentId);
            System.out.println("=== deleted rows for experiment "+manager.experimentId+": "+rowsDeleted);

            for( int column=3; column<3+4; column++ ) { // 0-based column in the excel sheet
                String name = "Condition 1"; //exp record name to be loaded, if not present
                manager.loadMetaData(column, name, false);
            }
            Mean.loadMean(manager.experimentId, manager.getDao());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
