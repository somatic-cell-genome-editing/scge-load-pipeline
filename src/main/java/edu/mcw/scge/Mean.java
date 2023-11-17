package edu.mcw.scge;

import edu.mcw.rgd.process.Utils;
import edu.mcw.scge.datamodel.ExperimentRecord;
import edu.mcw.scge.datamodel.ExperimentResultDetail;

import java.text.DecimalFormat;
import java.text.NumberFormat;
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

    static public void loadMean(long expId, Manager manager, boolean skipSignalData) throws Exception {

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

        if( !skipSignalData ) {
            if (!signalRecords.isEmpty()) {
                loadSignalMean(signalRecords, manager);
                manager.debug("   signal mean ok");
            }
        }
    }

    ///
    static void loadNumericMean(List<ExperimentRecord> records, Manager manager) throws Exception {

        LoadDAO dao = manager.getDao();
        int rowsWithNrOfSamplesUpdated = 0;
        int expResultDetailInserted = 0;
        int expResultDetailUpdated = 0;
        int expResultDetailUnchanged = 0;

        // compute max nr of samples for experiment
        int maxSamples = 0;
        for (ExperimentRecord record : records) {
            List<ExperimentResultDetail> experimentResults = dao.getExperimentalResults(record.getExperimentRecordId());
            for (ExperimentResultDetail result : experimentResults) {
                int noOfSamples = result.getNumberOfSamples();
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

                ExperimentResultDetail resultDetail0 = null;
                // do we have experiment_result with replicate 0?
                // if yes, pull it out of the processing list
                for( int i=0; i<list.size(); i++ ) {
                    ExperimentResultDetail d = list.get(i);
                    if( d.getReplicate()==0 ) {
                        resultDetail0 = d;
                        list.remove(i);
                        break;
                    }
                }
                boolean insertResultDetail = resultDetail0==null;
                if( insertResultDetail ) {
                    resultDetail0 = new ExperimentResultDetail();
                    resultDetail0.setResultId(resultId);
                    resultDetail0.setReplicate(0);
                }

                double average = 0;
                int noOfSamples = 0;
                int valuesWithNrOfBlasts = 0;

                for (ExperimentResultDetail result: list) {

                    if (!result.getUnits().equalsIgnoreCase("signal")) {
                        String val = Utils.NVL(result.getResult(), "");
                        if( val.isEmpty()
                                || val.equalsIgnoreCase("Not determined")
                                || val.equalsIgnoreCase("Not measured")
                                || val.equalsIgnoreCase("Not provided")
                                || val.equalsIgnoreCase("NaN")
                                || val.equalsIgnoreCase("N/A") ) {
                            // do nothing -- NaN
                        } else {
                            String val2 = result.getResult();
                            if( val2.endsWith("%") ) {
                                val2 = val2.substring(0, val2.length()-1).trim();
                            }
                            average += Double.valueOf(val2);
                            noOfSamples++;
                        }
                    } else {
                        if( result.getResult().contains("blasts") ) {
                            valuesWithNrOfBlasts++;
                        }
                    }
                }
                if( noOfSamples>0 ) {
                    average = average / noOfSamples;
                }
                //1. average = Math.round(average * 100.0) / 100.0;
                //   String averageStr = String.valueOf(average);
                //2. String averageStr = String.format("%.2f", average);
                String averageStr = roundToTwoPlaces(average);
                if( average==0 && valuesWithNrOfBlasts>0 ) {
                    averageStr = "# blasts analyzed";
                }

                if( resultDetail0.getReplicate()!=0 ) {
                    System.out.println("*** MEAN: unexpected");
                }

                if( insertResultDetail ) {
                    resultDetail0.setResult(averageStr);
                    dao.insertExperimentResultDetail(resultDetail0);
                    expResultDetailInserted++;
                } else {
                    // update value (mean) for replicate 0
                    String oldValue = resultDetail0.getResult();
                    boolean valueChanged = !oldValue.equals(averageStr);
                    if( valueChanged ) {
                        resultDetail0.setResult(averageStr);
                        dao.updateExperimentResultDetail(resultDetail0);
                        expResultDetailUpdated++;
                    } else {
                        expResultDetailUnchanged++;
                    }
                }
                int rowsUpdated = dao.updateNumberOfSamplesForResult(resultId, noOfSamples);
                rowsWithNrOfSamplesUpdated += rowsUpdated;
            }
        }

        if( rowsWithNrOfSamplesUpdated!=0 ) {
            manager.info("    EXPERIMENT_RESULT rows with NUMBER_OF_SAMPLES updated: " + rowsWithNrOfSamplesUpdated);
        }
        if( expResultDetailInserted!=0 ) {
            manager.info("    EXPERIMENT_RESULT_DETAIL inserted: " + expResultDetailInserted);
        }
        if( expResultDetailUpdated!=0 ) {
            manager.info("    EXPERIMENT_RESULT_DETAIL updated: " + expResultDetailUpdated);
        }
        if( expResultDetailUnchanged!=0 ) {
            manager.info("    EXPERIMENT_RESULT_DETAIL unchanged: " + expResultDetailUnchanged);
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

                            String valInDb = dao.getResultForExperimentResultDetail(result.getResultId(), i);
                            if( valInDb==null ) {
                                resultDetail.setResult("NaN");
                                dao.insertExperimentResultDetail(resultDetail);
                            } else {
                                if( !valInDb.equals("NaN") ) {
                                    System.out.println("unexpected value in DB");
                                }
                            }
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
                int notProvidedCount = 0;

                for (ExperimentResultDetail result : experimentResults) {
                    if( result.getReplicate()!=0 ) {
                        if( result.getResult().equalsIgnoreCase("present") ) {
                            presentCount++;
                        }
                        if( result.getResult().equalsIgnoreCase("not reported") ) {
                            notReportedCount++;
                        }
                        if( result.getResult().equalsIgnoreCase("not provided") ) {
                            notProvidedCount++;
                        }
                        anyCount++;
                    }
                }

                ExperimentResultDetail resultMean = new ExperimentResultDetail();
                resultMean.setReplicate(0);
                resultMean.setResultId(resultId);
                if( notReportedCount==anyCount ) {
                    resultMean.setResult("not reported");
                } else if( notProvidedCount==anyCount ) {
                    resultMean.setResult("not provided");
                } else {
                    resultMean.setResult(presentCount + " of " + anyCount + " present");
                }

                dao.insertExperimentResultDetail(resultMean);
                insertedRows++;
            }
        }

        manager.info("  inserted signal rows with replicate 0: "+insertedRows);
    }

    static NumberFormat formatter = new DecimalFormat("############0.##");

    synchronized public static String roundToTwoPlaces( Double d ) {

        // to ensure proper HALF_UP rounding, i.e. 0.355 -> 0.36
        // first we round up the number, and then we format it
        double d2 =  ((long) (d < 0 ? d * 100 - 0.5 : d * 100 + 0.5)) / 100.0;
        return formatter.format(d2);
    }
}
