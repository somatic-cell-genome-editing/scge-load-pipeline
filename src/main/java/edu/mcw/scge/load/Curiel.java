package edu.mcw.scge.load;

import edu.mcw.scge.Manager;

// study loaded in Nov 08, 2022
public class Curiel {

    public static void main(String[] args) {

        Manager manager = Manager.getManagerInstance();

        manager.studyId = 1067;
        manager.experimentId = 18000000009L;
        manager.fileName = "data/Curiel.xlsx";
        manager.expType = "In Vivo";
        manager.tier = 0;

        try {
            for( int column=3; column<=6; column++ ) { // 0-based column in the excel sheet
                String name = "Condition 1"; //exp record name to be loaded, if not present
                manager.loadMetaData(column, name, false);
            }
            manager.loadMean(manager.experimentId);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
