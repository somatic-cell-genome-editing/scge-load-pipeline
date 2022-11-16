package edu.mcw.scge.load;

import edu.mcw.scge.Manager;

// study loaded in Nov 15, 2022
public class Gao {

    public static void main(String[] args) {

        Manager manager = Manager.getManagerInstance();

        manager.studyId = 1051;
        manager.fileName = "data/Gao1.xlsx";
        manager.tier = 0;

        try {
            manager.experimentId = 18000000068L;
            manager.expType = "In Vivo";
            for( int column=3; column<=5; column++ ) { // 0-based column in the excel sheet
                String name = "Condition 1"; //exp record name to be loaded, if not present
                manager.loadMetaData(column, name, false);
            }
            manager.loadMean(manager.experimentId);

            manager.experimentId = 18000000069L;
            manager.expType = "In Vivo (2)";
            for (int column = 3; column <= 4; column++) { // 0-based column in the excel sheet
                String name = "Condition 1"; //exp record name to be loaded, if not present
                manager.loadMetaData(column, name, false);
            }
            manager.loadMean(manager.experimentId);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
