package edu.mcw.scge.load;

import edu.mcw.rgd.process.Utils;
import edu.mcw.scge.LoadDAO;
import edu.mcw.scge.Manager;
import edu.mcw.scge.Mean;

// loaded in Nov 2022
// reloaded on DEV Jan-19, 2023
// reloaded on DEV/STAGE Feb 09, 2023
// reloaded on DEV/STAGE Mar 01, 2023

public class Lam {

    public static void main(String[] args) {

        Manager manager = Manager.getManagerInstance();
        LoadDAO dao = manager.getDao();

        manager.studyId = 1062;
        manager.fileName = "data/Lam-1062-7.xlsx";
        manager.tier = 0;

        try {
            String name = "Condition 1"; //exp record name to be loaded, if not present

            boolean loadInVivo1 = true;
            boolean loadInVivo2 = false;

            // load "In Vivo" sheet
            if(loadInVivo1) {

                manager.experimentId = 18000000056L;
                manager.expType = "In Vivo";

                boolean newExperimentCreated = dao.createExperimentIfMissing(manager.studyId, manager.experimentId, manager.expType);
                if (newExperimentCreated) {
                    manager.info("=== new experiment created");
                } else {
                    int rowsDeleted = dao.deleteExperimentData(manager.experimentId, manager.studyId);
                    manager.info("=== deleted rows for experiment " + manager.experimentId + ": " + rowsDeleted);
                }

                {
                    int column = 3;
                    manager.loadMetaData(column, name);
                }

                // quantitative data in column 4
                {
                    int column = 4;
                    manager.loadMetaData(column, name);
                }

                // TODO: manually compute means: mixed qualitative and quantitative data
                Mean.loadMean(18000000056L, manager);

                manager.finish();
            }

            // load "In Vivo (2)" sheet
            if(loadInVivo2) {
                manager.loadExperimentSignalData(18000000057L, "In Vivo (2)", 6);
            }
        } catch (Exception e) {
            Utils.printStackTrace(e, manager.getLog());
        }
    }
}
