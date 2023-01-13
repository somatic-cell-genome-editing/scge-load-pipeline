package edu.mcw.scge;

import edu.mcw.scge.datamodel.Experiment;

import java.util.Collections;
import java.util.List;

public class FixMeans {

    public static void main(String[] args) throws Exception {

        Manager manager = Manager.getManagerInstance();
        LoadDAO dao = new LoadDAO();
        List<Experiment> experiments = dao.getAllExperiments();
        Collections.shuffle(experiments);

        int i = 0;
        for( Experiment e: experiments ) {
            i++;
            long expId = e.getExperimentId();
            if( expId==18000000046L ) {
                continue; // this expriment has values very close to 0
            }
            manager.studyId = e.getStudyId();
            manager.experimentId = expId;

            if( expId==18000000033L ) {
                System.out.println("problematic");
            }

            manager.info(i+". QC-ing experiment "+expId);
            Mean.loadMean(expId, manager, true);
        }
    }
}
