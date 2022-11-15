package edu.mcw.scge.load;

import edu.mcw.scge.Manager;

// study reloaded in Nov 08, 2022 - Lam2.xlsx
// study reloaded in Nov 09, 2022 - Lam3.xlsx
public class Lam {

    public static void main(String[] args) {

        Manager manager = Manager.getManagerInstance();

        manager.studyId = 1062;
        manager.fileName = "data/Lam4.xlsx";
        manager.tier = 0;

        try {
            String name = "Condition 1"; //exp record name to be loaded, if not present

            boolean loadInVivo1 = false;
            boolean loadInVivo2 = true;
            boolean qualitativeData; // if true, it overrides units for experiment data to 'Signal'

            // load "In Vivo" sheet
            if(loadInVivo1) {
                manager.experimentId = 18000000056L;
                manager.expType = "In Vivo";

                qualitativeData = true;
                int column = 3;// 0-based column in the excel sheet
                manager.loadMetaData(column, name, qualitativeData);

                // quantitative data
                qualitativeData = false;
                column = 4;
                manager.loadMetaData(column, name, qualitativeData);

                // TODO: manually compute means: mixed qualitative and quantitative data
            }

            // load "In Vivo (2)" sheet
            if(loadInVivo2) {
                manager.experimentId = 18000000057L;
                manager.expType = "In Vivo (2)";

                qualitativeData = true;
                for( int col=3; col<3+6; col++ ) {
                    manager.loadMetaData(col, name, qualitativeData);
                }
                manager.loadQualitativeMean(manager.experimentId);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
