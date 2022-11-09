package edu.mcw.scge.load;

import edu.mcw.scge.Manager;

// study reloaded in Nov 08, 2022
public class Lam {

    public static void main(String[] args) {

        Manager manager = Manager.getManagerInstance();

        manager.studyId = 1062;
        manager.fileName = "data/Lam2.xlsx";
        manager.tier = 0;

        try {
            String name = "Condition 1"; //exp record name to be loaded, if not present

            // load "In Vivo" sheet
            if( false) {
                manager.experimentId = 18000000056L;
                manager.expType = "In Vivo";

                // qualitative data (absent, ...)
                int column = 3;// 0-based column in the excel sheet
                manager.loadMetaData(column, name);

                // quantitative data
                column = 4;
                manager.loadMetaData(column, name);

                // TODO: manually compute means: mixed qualitative and quantitative data
            }

            // load "In Vivo (2)" sheet
            if( true) {
                manager.experimentId = 18000000057L;
                manager.expType = "In Vivo (2)";

                // qualitative data (absent, ...)
                for( int col=3; col<3+6; col++ ) {
                    manager.loadMetaData(col, name);
                }
                manager.loadQualitativeMean(manager.experimentId);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
