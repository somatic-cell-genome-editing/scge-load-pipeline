package edu.mcw.scge;

import edu.mcw.scge.datamodel.ExperimentRecord;
import edu.mcw.scge.datamodel.ExperimentResultDetail;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Mean {

    static public void loadMean(long expId, Manager manager) throws Exception {

        List<ExperimentRecord> records = manager.getDao().getExpRecords(expId);


        List<ExperimentRecord> numericRecords = new ArrayList<>();
        List<ExperimentRecord> signalRecords = new ArrayList<>();

        // determine which records are numeric, which are signal
        for (ExperimentRecord record : records) {

            List<ExperimentResultDetail> experimentResults = manager.getDao().getExperimentalResults(record.getExperimentRecordId());

            int noOfSamples = 0;
            int signalSamples = 0;

            for (ExperimentResultDetail result : experimentResults) {
                noOfSamples++;
                if (result.getUnits().equalsIgnoreCase("signal")) {
                    signalSamples++;
                }
            }

            if (noOfSamples == signalSamples) {
                signalRecords.add(record);
            } else {
                numericRecords.add(record);
            }
        }

        manager.info("MEAN:  "+numericRecords.size()+" numeric records, "+signalRecords.size()+" signal records");

        if( !numericRecords.isEmpty() ) {
            loadNumericMean(numericRecords, manager);
            manager.debug("   numeric mean ok");
        }

        if( !signalRecords.isEmpty() ) {
            loadSignalMean(signalRecords, manager);
            manager.debug("   signal mean ok");
        }
    }

    static void loadNumericMean(List<ExperimentRecord> records, Manager manager) throws Exception {

        LoadDAO dao = manager.getDao();
        // compute max nr of samples for experiment
        int maxSamples = 0;
        for (ExperimentRecord record : records) {
            List<ExperimentResultDetail> experimentResults = dao.getExperimentalResults(record.getExperimentRecordId());
            int noOfSamples = 0;
            for (ExperimentResultDetail result : experimentResults) {
                noOfSamples = result.getNumberOfSamples();
                if (maxSamples < noOfSamples)
                    maxSamples = noOfSamples;
            }
        }

        // compute averages for every result_id
        for (ExperimentRecord record : records) {
            List<ExperimentResultDetail> experimentResults = dao.getExperimentalResults(record.getExperimentRecordId());
            Map<Long, List<ExperimentResultDetail>> resultIdMap = new HashMap<>();
            for (ExperimentResultDetail r: experimentResults) {
                List<ExperimentResultDetail> list = resultIdMap.get(r.getResultId());
                if( list==null ) {
                    list = new ArrayList<>();
                    resultIdMap.put(r.getResultId(), list);
                }
                list.add(r);
            }

            for( long resultId: resultIdMap.keySet() ) {
                List<ExperimentResultDetail> list = resultIdMap.get(resultId);

                ExperimentResultDetail resultDetail0 = new ExperimentResultDetail();
                double average = 0;
                int noOfSamples = 0;

                for (ExperimentResultDetail result: list) {
                    noOfSamples = result.getNumberOfSamples();

                    if (!result.getUnits().equalsIgnoreCase("signal")) {
                        if (result.getResult() != null && !result.getResult().equals("")) {
                            if( result.getResult().contains("Not measured") ) {
                                // do nothing -- NaN
                            } else {
                                average += Double.valueOf(result.getResult());
                            }
                        }
                    }
                    resultDetail0 = result;
                }
                average = average / noOfSamples;
                average = Math.round(average * 100.0) / 100.0;
                resultDetail0.setReplicate(0);
                resultDetail0.setResult(String.valueOf(average));
                dao.insertExperimentResultDetail(resultDetail0);
            }
        }

        manager.debug("    Max nr of numeric samples = " + maxSamples);

        for (ExperimentRecord record : records) {
            ExperimentResultDetail resultDetail = new ExperimentResultDetail();
            List<ExperimentResultDetail> experimentResults = dao.getExperimentalResults(record.getExperimentRecordId());
            for (ExperimentResultDetail result : experimentResults) {
                if (resultDetail.getReplicate() == 0) {
                    if (maxSamples > result.getNumberOfSamples()) {
                        for (int i = result.getNumberOfSamples() + 1; i <= maxSamples; i++) {
                            resultDetail.setResultId(result.getResultId());
                            resultDetail.setReplicate(i);
                            resultDetail.setResult("NaN");
                            dao.insertExperimentResultDetail(resultDetail);
                        }
                    }
                }
            }
        }
    }


    static void loadSignalMean(List<ExperimentRecord> records, Manager manager) throws Exception {

        LoadDAO dao = manager.getDao();
        int insertedRows = 0;

        // compute averages for every result_id
        for (ExperimentRecord record : records) {
            List<ExperimentResultDetail> experimentResults = dao.getExperimentalResults(record.getExperimentRecordId());
            Map<Long, List<ExperimentResultDetail>> resultIdMap = new HashMap<>();
            for (ExperimentResultDetail r : experimentResults) {
                List<ExperimentResultDetail> list = resultIdMap.get(r.getResultId());
                if (list == null) {
                    list = new ArrayList<>();
                    resultIdMap.put(r.getResultId(), list);
                }
                list.add(r);
            }

            for (long resultId : resultIdMap.keySet()) {
                experimentResults = resultIdMap.get(resultId);

                int anyCount = 0;
                int presentCount = 0;
                int notReportedCount = 0;

                for (ExperimentResultDetail result : experimentResults) {
                    if( result.getReplicate()!=0 ) {
                        if( result.getResult().equals("present") ) {
                            presentCount++;
                        }
                        if( result.getResult().equals("not reported") ) {
                            notReportedCount++;
                        }
                        anyCount++;
                    }
                }

                ExperimentResultDetail resultMean = new ExperimentResultDetail();
                resultMean.setReplicate(0);
                resultMean.setResultId(resultId);
                if( notReportedCount==anyCount ) {
                    resultMean.setResult("not reported");
                } else {
                    resultMean.setResult(presentCount + " of " + anyCount + " present");
                }

                dao.insertExperimentResultDetail(resultMean);
                insertedRows++;
            }
        }

        manager.info("  inserted signal rows with replicate 0: "+insertedRows);
    }

    static void loadSignalMeanOld(List<ExperimentRecord> records, Manager manager) throws Exception {

        LoadDAO dao = manager.getDao();
        int insertedRows = 0;
        for (ExperimentRecord record : records) {
            List<ExperimentResultDetail> experimentResults = dao.getExperimentalResults(record.getExperimentRecordId());

            long resultId = 0;
            int anyCount = 0;
            int presentCount = 0;
            int notReportedCount = 0;

            for (ExperimentResultDetail result : experimentResults) {
                if( result.getReplicate()!=0 ) {
                    if( result.getResult().equals("present") ) {
                        presentCount++;
                    }
                    if( result.getResult().equals("not reported") ) {
                        notReportedCount++;
                    }
                    anyCount++;

                    resultId = result.getResultId();
                }
            }

            ExperimentResultDetail resultMean = new ExperimentResultDetail();
            resultMean.setReplicate(0);
            resultMean.setResultId(resultId);
            if( notReportedCount==anyCount ) {
                resultMean.setResult("not reported");
            } else {
                resultMean.setResult(presentCount + " of " + anyCount + " present");
            }

            dao.insertExperimentResultDetail(resultMean);
            insertedRows++;
        }

        //System.out.println("inserted rows with replicate 0: "+insertedRows);
    }
}
