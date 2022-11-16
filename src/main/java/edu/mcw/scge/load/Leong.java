package edu.mcw.scge.load;

import edu.mcw.scge.Manager;
import edu.mcw.scge.Mean;

// study loaded in Nov 16, 2022
public class Leong {

    public static void main(String[] args) {

        Manager manager = Manager.getManagerInstance();

        manager.studyId = 1064;
        manager.fileName = "data/Leong1.xlsx";
        manager.tier = 0;
        manager.experimentId = 18000000059L;
        manager.expType = "In Vivo";

        try {

            int rowsDeleted = manager.getDao().deleteExperimentData(manager.experimentId);
            System.out.println("=== deleted rows for experiment "+manager.experimentId+": "+rowsDeleted);

            // 4 columns of numeric data
            for( int column=3; column<=6; column++ ) { // 0-based column in the excel sheet
                String name = "Condition 1"; //exp record name to be loaded, if not present
                manager.loadMetaData(column, name, false);
            }
            System.out.println("=== numeric metadata loaded");

            // 2 last columns of absent/present data
            for( int column=7; column<=8; column++ ) { // 0-based column in the excel sheet
                String name = "Condition 1"; //exp record name to be loaded, if not present
                manager.loadMetaData(column, name, true);
            }
            System.out.println("=== signal metadata loaded");

            Mean.loadMean(manager.experimentId, manager.getDao());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
