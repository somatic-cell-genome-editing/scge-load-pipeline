package edu.mcw.scge.load;

import edu.mcw.scge.Manager;
import edu.mcw.scge.Mean;

// study loaded in Nov 15, 2022
public class Saltzman {

    public static void main(String[] args) {

        Manager manager = Manager.getManagerInstance();

        manager.studyId = 1047;
        manager.experimentId = 18000000058L;
        manager.fileName = "data/Saltzman1.xlsx";
        manager.expType = "In Vivo";
        manager.tier = 0;

        try {
            for( int column=3; column<=27; column++ ) { // 0-based column in the excel sheet
                String name = "Condition 1"; //exp record name to be loaded, if not present
                manager.loadMetaData(column, name, false);
            }
            Mean.loadMean(manager.experimentId, manager);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
