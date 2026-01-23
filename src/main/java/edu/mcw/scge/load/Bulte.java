package edu.mcw.scge.load;

import edu.mcw.rgd.process.Utils;
import edu.mcw.scge.Manager;

// loaded on DEV on May 27, 2025
// loaded on STAGE on Aug 19, 2025
// loaded on DEV on Oct 28, 2025: experiments 133..137 dropped

public class Bulte {

    public static void main(String[] args) {

        Manager manager = Manager.getManagerInstance();

        manager.studyId = 1107;
        manager.fileName = "data/Bulte-1107-2.xlsx";
        manager.tier = 0;

        try {

            //manager.loadExperimentData(18000000133L, "In Vitro (1)", 1, 5);
            //manager.loadExperimentData(18000000134L, "In Vitro (2)", 7, 5);
            //manager.loadExperimentData(18000000135L, "In Vitro (3)", 8, 5);
            //manager.loadExperimentData(18000000136L, "In Vitro (4)", 8, 5);
            //manager.loadExperimentData(18000000137L, "In Vitro (5)", 8, 5);

            manager.getDao().deleteExperimentData(18000000133L, manager.studyId);
            manager.getDao().update("delete from experiment where experiment_id=?",18000000133L);
            manager.getDao().deleteExperimentData(18000000134L, manager.studyId);
            manager.getDao().update("delete from experiment where experiment_id=?",18000000134L);
            manager.getDao().deleteExperimentData(18000000135L, manager.studyId);
            manager.getDao().update("delete from experiment where experiment_id=?",18000000135L);
            manager.getDao().deleteExperimentData(18000000136L, manager.studyId);
            manager.getDao().update("delete from experiment where experiment_id=?",18000000136L);
            manager.getDao().deleteExperimentData(18000000137L, manager.studyId);
            manager.getDao().update("delete from experiment where experiment_id=?",18000000137L);

            manager.loadExperimentData(18000000138L, "In Vitro (6)", 2, 5);
            manager.loadExperimentData(18000000139L, "In Vivo (1)", 4, 5);

        } catch (Exception e) {
            Utils.printStackTrace(e, manager.getLog());
        }
    }
}
