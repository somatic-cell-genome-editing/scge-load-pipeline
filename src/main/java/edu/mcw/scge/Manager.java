package edu.mcw.scge;

import edu.mcw.rgd.process.Utils;
import edu.mcw.scge.datamodel.*;
import edu.mcw.scge.datamodel.Vector;
import org.apache.commons.lang3.text.WordUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.xml.XmlBeanDefinitionReader;
import org.springframework.core.io.FileSystemResource;

import java.io.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class Manager {

    Logger log = LogManager.getLogger("status");
    LoadDAO dao;

    enum SECTION {
        NONE,
        EDITOR,
        GUIDE,
        HRDONOR,
        VECTOR,
        DS_PROTEIN_CONJUGATE,
        DS_VIRUS_LIKE_PARTICLE,
        DS_NANOPARTICLE,
        DS_COMMERCIAL_REAGENT,
        DS_AMPHIPHILIC_PEPTIDE,
        ANIMAL_MODEL,
        CELL_MODEL,
        CELL_ANIMAL_MODEL,
        ORGANOID_MODEL,
        EXPERIMENT_DETAILS,
        OTHER_EXPERIMENT_DETAILS,
    }

    public int studyId = 0;
    public long experimentId = 18000000000L;
    public String fileName = "0.xlsx";
    public String expType = "In Vivo";
    public int tier = 0;
    public boolean forceLoadExperimentRecordsAsSignal = false;

    Set<Long> vectors = new TreeSet<>();
    Set<Long> guides = new TreeSet<>();
    Set<Integer> antibodies = new TreeSet<>();
    List<OffTarget> offTargets = new ArrayList<>();

    public static void main(String[] args) throws Exception {

        DefaultListableBeanFactory bf = new DefaultListableBeanFactory();
        new XmlBeanDefinitionReader(bf).loadBeanDefinitions(new FileSystemResource("properties/AppConfigure.xml"));
        Manager manager = (Manager) (bf.getBean("manager"));

        for (int i = 0; i < args.length; i++) {
            String arg = args[i];
            switch (arg) {
                case "--expId":
                    manager.experimentId = Integer.parseInt(args[++i]);
                    break;
                case "--studyId":
                    manager.studyId = Integer.parseInt(args[++i]);
                    break;
                case "--expType":
                    manager.expType = args[++i];
                    break;
                case "--tier":
                    manager.tier = Integer.parseInt(args[++i]);
                    break;
                case "--fileName":
                    manager.fileName = args[++i];
                    break;
            }
        }
    }

    public static Manager getManagerInstance() {
        DefaultListableBeanFactory bf = new DefaultListableBeanFactory();
        new XmlBeanDefinitionReader(bf).loadBeanDefinitions(new FileSystemResource("properties/AppConfigure.xml"));
        Manager manager = (Manager) (bf.getBean("manager"));
        return manager;
    }

    private String getCellData(Cell cell) {
        String data;

        if (cell == null)
            data = null;
        else if (cell.getCellType() == Cell.CELL_TYPE_STRING || cell.getCellType() == Cell.CELL_TYPE_BLANK)
            data = cell.getStringCellValue();
        else data = String.valueOf(cell.getNumericCellValue());

        if (data != null) {
            // replace nonbreakable space (&nbsp;) with regular space and then trim
            String newData = data.replace("\u00A0", " ").trim();
            if( !newData.equals(data) ){
                data = newData;
            }
        }
        return data;
    }

    public void loadMetaData(int column, String name) throws Exception {

        // reset lists
        vectors.clear();
        guides.clear();
        antibodies.clear();
        offTargets.clear();

        FileInputStream fis = new FileInputStream(fileName);
        XSSFWorkbook wb = new XSSFWorkbook(fis);
        //String s1 = wb.getSheetName(1);
        //debug("worksheet nr 1 name: "+s1);
        XSSFSheet sheet = wb.getSheet(expType);      //creating a Sheet object to retrieve object

        SECTION section = SECTION.NONE;
        boolean traData = false;
        boolean antibodyData = false;
        Guide guide = new Guide();
        Editor editor = new Editor();
        Set<Long> deliveryIds = new HashSet<>();
        Model model = new Model();
        Model animalModel = new Model();
        HashMap<String, String> metadataForCellModel = null;
        Vector vector = new Vector();
        ApplicationMethod method = new ApplicationMethod();
        HRDonor hrDonor = new HRDonor();
        Map<String,String> expRecDetails = new HashMap<>();

        HashMap<String, String> metadata = new HashMap<>();
        ExperimentRecord experiment = new ExperimentRecord();
        experiment.setExperimentName(name);
        experiment.setStudyId(studyId);
        experiment.setExperimentId(experimentId);

        for (Row row : sheet) {

            Cell cell0 = row.getCell(0);
            Cell cell1 = row.getCell(1);
            Cell cell = row.getCell(column);

            if (row.getRowNum() < 3 || cell1 == null)
                continue;

            String data = getCellData(cell);
            String modifiedData = WordUtils.capitalizeFully(data);
            String cell0Data = cell0 == null ? "" : cell0.getStringCellValue();
            String cell1Data = cell1 == null ? "" : cell1.getStringCellValue();
            boolean isEndOfSection = cell1Data.equalsIgnoreCase("Related publication description");
            // for schema 5.7, this line is not available: use 'Transgene Reporter' to detect the last line of cell model
            if( !isEndOfSection && section==SECTION.CELL_MODEL ) {
                if( cell1Data.equalsIgnoreCase("Transgene Reporter") ) {
                    //metadataForCellModel.put("Transgene Reporter", data);
                    isEndOfSection = true;
                }
            }

            // update experiment name and description if needed
            if( section==SECTION.NONE && cell1Data.equalsIgnoreCase("Experiment Title") && !Utils.isStringEmpty(data) ) {
                String expName = data.trim();
                Experiment e = getDao().getExperiment(experimentId);
                if( !Utils.stringsAreEqual(expName, e.getName()) ) {
                    e.setName(expName);
                    getDao().updateExperimentName(experimentId, expName);
                    info("   updated experiment name to ["+expName+"]");
                }
            }
            if( section==SECTION.NONE && cell1Data.equalsIgnoreCase("Experiment Description") && !Utils.isStringEmpty(data) ) {
                String expDesc = data.trim();
                Experiment e = getDao().getExperiment(experimentId);
                if( !Utils.stringsAreEqual(expDesc, e.getDescription()) ) {
                    e.setDescription(expDesc);
                    getDao().updateExperimentDesc(experimentId, expDesc);
                    info("   updated experiment description to ["+expDesc+"]");
                }
            }

            if (cell0Data.equalsIgnoreCase("EDITOR")
                    || cell0Data.equalsIgnoreCase("Editors & Targets")) { // format v5
                if (data != null && !data.equals(""))
                    experiment.setExperimentName(data);
                info("======");
                info("exprec: " + experiment.getExperimentName());
            }

            // CELL+ANIMAL_MODEL
            // note: this code must be before 'metadata.put(cell1Data, data)' to avoid override of SCGE ID
            if (section == SECTION.CELL_MODEL  &&  cell0Data.equalsIgnoreCase("Animal Model source of cells")) {
                metadataForCellModel = new HashMap<>(metadata);
                metadata.clear();
                section = SECTION.CELL_ANIMAL_MODEL;
            }

            metadata.put(cell1Data, data);

            // EDITOR
            if (cell0Data.equalsIgnoreCase("EDITOR")
                    || cell0Data.equalsIgnoreCase("EDITOR PROTEIN")) { // format v5
                section = SECTION.EDITOR;
            }
            if (section == SECTION.EDITOR && isEndOfSection) {
                experiment.setEditorId(loadEditor(metadata, editor));
                metadata.clear();
            }

            // GUIDE
            if (cell0Data.equalsIgnoreCase("GUIDE") || cell0Data.equalsIgnoreCase("Guide RNA (gRNA)")) {
                section = SECTION.GUIDE;
            }
            if (section == SECTION.GUIDE && isEndOfSection) {
                loadGuide(metadata, guide);
                metadata.clear();
            }

            // ANIMAL MODEL
            if (cell0Data.equalsIgnoreCase("Animal Model (AM)")) {
                section = SECTION.ANIMAL_MODEL;
            }
            if (section == SECTION.ANIMAL_MODEL && isEndOfSection) {
                long modelId = loadAnimalModel(metadata, model);
                if( modelId!=0 ) {
                    experiment.setModelId(modelId);
                }
                metadata.clear();
            }

            // CELL MODEL
            if (cell0Data.equalsIgnoreCase("Cell model")
             || cell0Data.equalsIgnoreCase("Cell/Organoid model")) {
                section = SECTION.CELL_MODEL;
            }
            if (section == SECTION.CELL_MODEL && isEndOfSection) {
                long modelId = loadCellModel(metadata, model);
                if( modelId!=0 ) {
                    experiment.setModelId(modelId);
                }
                metadata.clear();
            }

            // CELL+ANIMAL_MODEL
            if (section == SECTION.CELL_ANIMAL_MODEL && isEndOfSection) {
                long animalModelId = loadAnimalModel(metadata, animalModel);
                metadataForCellModel.put("Parental Origin", Long.toString(animalModelId));
                long cellModelId = loadCellModel(metadataForCellModel, model);
                if( cellModelId == 0 && model.getModelId()!=0 ) {
                    cellModelId = model.getModelId();

                    // validate parental origin
                    Model cellModelInRgd = dao.getModel(model.getModelId());
                    if( cellModelInRgd!=null ) {
                        if( Utils.isStringEmpty(cellModelInRgd.getParentalOrigin()) ) {
                            dao.setParentalOriginForModel(cellModelId, metadataForCellModel.get("Parental Origin"));
                        } else {
                            if( !metadataForCellModel.get("Parental Origin").equals(cellModelInRgd.getParentalOrigin()) ) {
                                info("*** CONFLICT *** parental origin for "+cellModelId);
                            }
                        }
                    }
                }
                if( cellModelId!=0 ) {
                    experiment.setModelId(cellModelId);
                }

                metadata.clear();
                metadataForCellModel = null;
            }

            // ORGANOID MODEL
            if (cell0Data.equalsIgnoreCase("Organoid (Tissue model)")) {
                section = SECTION.ORGANOID_MODEL;
            }
            if (section == SECTION.ORGANOID_MODEL && isEndOfSection) {
                long organoidModelId = loadOrganoidModel(metadata, model);
                if( organoidModelId!=0 ) {
                    experiment.setModelId(organoidModelId);
                }
                metadata.clear();
            }

            // HR DONOR
            if (cell0Data.equalsIgnoreCase("HR DONOR")) {
                section = SECTION.HRDONOR;
            }
            if (section == SECTION.HRDONOR && isEndOfSection) {
                experiment.setHrdonorId(loadHrDonor(metadata, hrDonor));
                metadata.clear();
            }

            // VECTOR/FORMAT
            if (cell0Data.equalsIgnoreCase("VECTOR/FORMAT")) {
                section = SECTION.VECTOR;
            }
            if (section == SECTION.VECTOR && isEndOfSection) {
                loadVector(metadata, vector);
                metadata.clear();
            }

            // DELIVERY SYSTEM: PROTEIN CONJUGATE
            if (cell0Data.equalsIgnoreCase("Protein Conjugate")) {
                section = SECTION.DS_PROTEIN_CONJUGATE;
            }
            if (section == SECTION.DS_PROTEIN_CONJUGATE && isEndOfSection) {
                long dsId = loadProtienConjugate(metadata);
                if (dsId != 0) {
                    deliveryIds.add(dsId);
                }
                metadata.clear();
            }

            // DELIVERY SYSTEM: VIRUS LIKE PARTICLE
            if (cell0Data.equalsIgnoreCase("Virus Like Particle")) {
                section = SECTION.DS_VIRUS_LIKE_PARTICLE;
            }
            if (section == SECTION.DS_VIRUS_LIKE_PARTICLE && isEndOfSection) {
                long dsId = loadVirusParticle(metadata);
                if (dsId != 0) {
                    deliveryIds.add(dsId);
                }
                metadata.clear();
            }

            // DELIVERY SYSTEM: COMMERCIAL REAGENT
            if (cell0Data.equalsIgnoreCase("Commercial Reagent")) {
                section = SECTION.DS_COMMERCIAL_REAGENT;
            }
            if (section == SECTION.DS_COMMERCIAL_REAGENT && isEndOfSection) {
                long dsId = loadCommercialReagent(metadata);
                if (dsId != 0) {
                    deliveryIds.add(dsId);
                }
                metadata.clear();
            }

            // DELIVERY SYSTEM: AMPHIPHILIC PEPTIDE
            if (cell0Data.equalsIgnoreCase("Amphiphilic peptide")) {
                section = SECTION.DS_AMPHIPHILIC_PEPTIDE;
            }
            if (section == SECTION.DS_AMPHIPHILIC_PEPTIDE && isEndOfSection) {
                long dsId = loadAmphiphilicPeptide(metadata);
                if (dsId != 0) {
                    deliveryIds.add(dsId);
                }
                metadata.clear();
            }

            // DELIVERY SYSTEM: NANOPARTICLE
            if (cell0Data.equalsIgnoreCase("Nanoparticle")) {
                section = SECTION.DS_NANOPARTICLE;
            }
            if (section == SECTION.DS_NANOPARTICLE && isEndOfSection) {
                long dsId = loadNanoparticle(metadata);
                if (dsId != 0) {
                    deliveryIds.add(dsId);
                }
                metadata.clear();
            }


            // EXPERIMENT
            if (cell0Data.equalsIgnoreCase("Experiment Details")
                    || cell0Data.equalsIgnoreCase("Other Experimental details")) { // format v5
                section = SECTION.EXPERIMENT_DETAILS;
            }

            if( cell1Data.equalsIgnoreCase("Other Experiment Details") && cell0Data.isEmpty() ) {
                section = SECTION.OTHER_EXPERIMENT_DETAILS;
            }

            if (traData ||
                    (cell0 != null && cell0.getStringCellValue().equalsIgnoreCase("Transient Reporter Assay"))) {
                traData = true;
                if (cell1.getStringCellValue().equalsIgnoreCase("RRID")) {
                    model.setRrid(data);
                } else if (cell1.getStringCellValue().equalsIgnoreCase("TRA Name")) {
                    model.setName(data);
                } else if (cell1.getStringCellValue().equalsIgnoreCase("TRA Description")) {
                    model.setDescription(data);
                } else if (cell1.getStringCellValue().equalsIgnoreCase("Reporter Type")) {
                    model.setSubtype(data);
                } else if (cell1.getStringCellValue().equalsIgnoreCase("Transgene")) {
                    model.setTransgene(data);
                } else if (cell1.getStringCellValue().equalsIgnoreCase("Annotated Map")) {
                    model.setAnnotatedMap(data);
                } else if (cell1.getStringCellValue().equalsIgnoreCase("Transgene Reporter")) {
                    model.setTransgeneReporter(data);
                    if (model.getName() == null || model.getName().equals("")) {
                        model = new Model();
                    } else {
                        model.setType("Transient Reporter Assay");
                        long modelId = dao.getModelId(model);
                        if (modelId == 0) {
                            modelId = dao.insertModel(model);
                            info("Inserted model: " + modelId);
                        } else info("Got model: " + modelId);
                    }
                    traData = false;
                }
            }

            if (experiment.getSex() == null && cell1.getStringCellValue().equalsIgnoreCase("Sex")) {
                if (data != null && !data.equals("")) {
                    if (data.equalsIgnoreCase("M")) {
                        experiment.setSex("Male");
                    } else if (data.equalsIgnoreCase("F")) {
                        experiment.setSex("Female");
                    } else if (data.equalsIgnoreCase("Male")) {
                        experiment.setSex("Male");
                    } else if (data.equalsIgnoreCase("Female")) {
                        experiment.setSex("Female");
                    }
                }
            }
            if (cell1.getStringCellValue().equalsIgnoreCase("Age") || cell1.getStringCellValue().equalsIgnoreCase("Passage")) {
                experiment.setAge(data);
            }
            if (cell1.getStringCellValue().equalsIgnoreCase("Zygosity")) {
                experiment.setGenotype(modifiedData);
            }
            if (cell1.getStringCellValue().equalsIgnoreCase("Format of editor/cargo")) {
                experiment.setApplicationMethodId(loadApplicationMethod(metadata, method));
            }

            if (antibodyData ||
                    (cell0 != null && cell0.getStringCellValue().equalsIgnoreCase("Antibody"))) {
                antibodyData = true;
                if (cell1.getStringCellValue().equalsIgnoreCase("Antibody description")) {
                    loadAntibody(metadata);
                    metadata.clear();
                }
                if (cell1.getStringCellValue().equalsIgnoreCase("Related publication description")) {
                    antibodyData = false;
                }
            }

            if( section == SECTION.OTHER_EXPERIMENT_DETAILS ) {
                String detailName = cell1Data.trim();
                String detailValue = data.trim();
                if( !Utils.isStringEmpty(detailName) && !Utils.isStringEmpty(detailValue) ) {
                    expRecDetails.put(detailName, detailValue);
                }
            }
        }

        // ensure there is at least one delivery_id
        if( deliveryIds.isEmpty() ) {
            deliveryIds.add(0l);
        }
        for( long deliveryId: deliveryIds ) {
            experiment.setDeliverySystemId(deliveryId);
            loadExperimentRecords(experiment, column, expRecDetails);
        }
    }


    public void loadExperimentRecords(ExperimentRecord expRec, int column, Map<String,String> expRecDetails) throws Exception {
        boolean mergeExpRecs = true;
        FileInputStream fis = new FileInputStream(fileName);
        XSSFWorkbook wb = new XSSFWorkbook(fis);
        XSSFSheet sheet = wb.getSheet(expType);

        String expDataType = null;
        ExperimentResultDetail result = new ExperimentResultDetail();

        for (Row row : sheet) {

            Cell cell0 = row.getCell(0);
            Cell cell1 = row.getCell(1);
            Cell cell2 = row.getCell(2);
            Cell cell3 = row.getCell(3);
            Cell cell4 = row.getCell(4);
            Cell cell = row.getCell(column);

            if (row.getRowNum() < 3 || cell1 == null)
                continue;
            String cell0ValU = cell0 == null ? "" : cell0.getStringCellValue().toUpperCase();

            String data = getCellData(cell);

            switch (cell0ValU) {
                case "EDITING EFFICIENCY": // data format up to 5.3.x
                case "EDITING DATA":       // data format 5.4
                    expDataType = "Editing Efficiency";
                    break;
                case "DELIVERY EFFICIENCY": // data format up to 5.3.x
                case "DELIVERY DATA":       // data format 5.4
                    expDataType = "Delivery Efficiency";
                    break;
                case "BIOMARKER DETECTION": // data format up to 5.3.x
                case "BIOMARKER DATA":      // data format 5.4
                    expDataType = "Biomarker Detection";
                    break;
                case "OTHER MEASUREMENT": // data format up to 5.3.x
                case "OTHER DATA":      // data format 5.4
                    expDataType = "Other Measurement";
                    break;
            }

            if (cell1.getStringCellValue().equalsIgnoreCase("Data_File_Name") || cell1.getStringCellValue().equalsIgnoreCase("Related Protocol")) {
                expDataType = null;
            }

            if (cell1.getStringCellValue().equalsIgnoreCase("Assay Description") || cell1.getStringCellValue().equalsIgnoreCase("Assay_Description")) {
                result.setAssayDescription(data);
            } else if (cell1.getStringCellValue().equalsIgnoreCase("Biological/Transfection/Delivery Replicates") ||
                    cell1.getStringCellValue().equalsIgnoreCase("Biological Replicates")) {

                if (data != null && !data.equals("")) {

                    if (data.startsWith("n=") || data.startsWith("N=")) {
                        data = data.substring(2).trim();
                    }
                    // remove any text after number
                    int spacePos = data.indexOf(' ');
                    if (spacePos > 0) {
                        data = data.substring(0, spacePos);
                    }
                    result.setNumberOfSamples(Double.valueOf(data).intValue());
                }
            } else if (cell1.getStringCellValue().equalsIgnoreCase("Units")) {
                result.setUnits(data);
            } else if (cell1.getStringCellValue().equalsIgnoreCase("Edit Type")) {
                result.setEditType(data);
            } else if (cell1.getStringCellValue().equalsIgnoreCase("Measure is Normalized")) {
                //System.out.println("ignore");
            } else if (expDataType != null) {

                result.setResultType(expDataType);

                if( data != null && !data.equals("") ) {

                    boolean validDataSeries =
                            !data.equalsIgnoreCase("ND") &&
                            !data.equalsIgnoreCase("N/A") &&
                            !data.equalsIgnoreCase("Not applicable") &&
                            !data.equalsIgnoreCase("Not determined") &&
                            !data.equalsIgnoreCase("Not provided") &&
                            !data.equalsIgnoreCase("Not measured");

                    if (validDataSeries || forceLoadExperimentRecordsAsSignal) {

                        log.debug(studyId + " " + experimentId + " " + cell1);

                        expRec.setTissueId("");
                        String tissueId = cell1.getStringCellValue();
                        if (tissueId.contains(":")) {
                            expRec.setTissueId(tissueId);
                        }

                        expRec.setCellType("");
                        if (cell2 != null) {
                            String cellTypeId = cell2.getStringCellValue();
                            if (cellTypeId.contains(":")) {
                                expRec.setCellType(cellTypeId);
                            }
                        }

                        expRec.setOrganSystemID("");
                        String organSystemID = cell0.getStringCellValue();
                        if (organSystemID != null && organSystemID.contains(":")) {
                            expRec.setOrganSystemID(organSystemID);
                        }

                        expRec.setQualifier("");
                        String qualifier = cell3.getStringCellValue();
                        if (qualifier != null && qualifier.length()>0 ) {
                            expRec.setQualifier(qualifier);
                        }

                        expRec.setTimePoint("");
                        String timePoint = cell4.getStringCellValue();
                        if (timePoint != null && timePoint.length()>0 ) {
                            expRec.setTimePoint(timePoint);
                        }

                        boolean dataSeriesIsSignal = areDataSeriesSignal(data);
                        if (dataSeriesIsSignal) {
                            //result.setUnits("Signal");
                        }

                        long expRecId = loadExperimentRecord(expRec, mergeExpRecs);
                        result.setExperimentRecordId(expRecId);
                        loadExperimentRecordDetails(expRecId, expRecDetails);

                        boolean detailsPresent = result.getNumberOfSamples() != 0
                                || !Utils.isStringEmpty(result.getUnits())
                                || !Utils.isStringEmpty(result.getAssayDescription())
                                || !Utils.isStringEmpty(result.getEditType());

                        if (detailsPresent) {
                            long resultId = dao.insertExperimentResult(result);
                            if (resultId != 0) {
                                loadDataSeries(data, resultId);
                            }
                        }
                    }
                }
            }
        }
    }

    boolean areDataSeriesSignal(String data) {

        if( forceLoadExperimentRecordsAsSignal ) {
            return true;
        }

        int signalDataCount = 0;
        int numericDataCount = 0;
        String valueString = data;
        String[] values = valueString.split(",");
        for (int i = 0; i < values.length; i++) {
            String val = values[i].trim().toLowerCase();
            if( val.isEmpty() || val.equals("n/a") ) {
                continue;
            }

            // strip trailing '%' if any
            if( val.endsWith("%") ) {
                val = val.substring(0, val.length()-1).trim();
            }

            try {
                Double.parseDouble(val);
                numericDataCount++;
            } catch( NumberFormatException e) {
                signalDataCount++;
            }
        }

        return signalDataCount>0;
    }

    void loadDataSeries(String data, long resultId) throws Exception {
        String valueString = data;
        String[] values = valueString.split(",");
        for (int i = 0; i < values.length; i++) {
            String val = values[i].trim();
            if( val.isEmpty() || val.equalsIgnoreCase("n/a") || val.equalsIgnoreCase("qns") ) {
                continue;
            }
            // strip trailing '%' if any
            if( val.endsWith("%") ) {
                val = val.substring(0, val.length()-1).trim();
            }

            ExperimentResultDetail detail = new ExperimentResultDetail();
            detail.setResultId(resultId);
            detail.setReplicate(i + 1);
            detail.setResult(val);
            dao.insertExperimentResultDetail(detail);
        }
    }

    long loadExperimentRecord(ExperimentRecord expRec, boolean mergeExpRecs) throws Exception {
        boolean newExpRec = false;
        long expRecId;
        if( mergeExpRecs ) {
            expRecId = dao.getExpRecId(expRec);
            if( expRecId==0 ) {
                expRecId = dao.insertExperimentRecord(expRec);
                log.debug("  new exp rec inserted: "+expRecId);
                newExpRec = true;
            } else {
                log.debug("  old exp rec reused: "+expRecId);
            }
        } else {
            // create a new experiment record *always* for every new section of data
            expRecId = dao.insertExperimentRecord(expRec);
            log.debug("  new exp rec inserted: "+expRecId);
            newExpRec = true;
        }

        if( newExpRec ) {
            for (long guideId : guides) {
                dao.insertGuideAssoc(expRecId, guideId);

                for (OffTarget o : offTargets) {
                    o.setGuideId(guideId);
                    dao.insertOffTarget(o);
                }
            }

            for (long vectorId : vectors) {
                dao.insertVectorAssoc(expRecId, vectorId);
            }

            for (int antibodyId : antibodies) {
                dao.insertAntibodyAssoc(expRecId, antibodyId);
            }
        }

        return expRecId;
    }

    private void loadGuide(HashMap<String, String> metadata, Guide guide) throws Exception {
        String scgeId = metadata.get("SCGE ID");
        if (!Utils.isStringEmpty(scgeId)) {
            guide.setGuide_id(new BigDecimal(scgeId).longValue());
            guides.add(guide.getGuide_id());
            info("  Got guide by SCGE ID: " + guide.getGuide_id());
            return;
        }
        guide.setGuide_id(0);

        guide.setGuideDescription(metadata.get("Guide Description"));
        guide.setSource(metadata.get("Source"));
        guide.setSpecies(metadata.get("Species"));
        guide.setGrnaLabId(metadata.get("Lab gRNA Name/ID"));
        guide.setGuide(metadata.get("Lab gRNA Name/ID"));

        String targetLocus = metadata.get("Target Locus");
        if( Utils.isStringEmpty(targetLocus) || targetLocus.equalsIgnoreCase("N/A") ) {
            guide.setTargetLocus(null);
        } else {
            //html-encode '<' characters to avoid problems with display in a browser
            String targetLocus2 = targetLocus.trim().replace("<", "&lt;");
            guide.setTargetLocus(targetLocus2);
        }

        guide.setTargetSequence(getUpperCase(metadata.get("Target Sequence")));
        guide.setPam(getUpperCase(metadata.get("Target Sequence+PAM")));
        guide.setAssembly(metadata.get("Genome Version"));
        guide.setChr(metadata.get("Chromosome"));
        guide.setStart(getInttoStringValue(metadata.get("Chromosome Start")));
        guide.setStop(getInttoStringValue(metadata.get("Chromosome End")));
        guide.setStrand(getStrand(metadata.get("Chromosome Strand")));
        guide.setSpacerSequence(getUpperCase(metadata.get("Spacer Sequence")));
        guide.setSpacerLength(getInttoStringValue(metadata.get("Spacer Length")));
        guide.setGuideFormat(metadata.get("Guide Format"));
        guide.setSpecificityRatio(getSpecificityRatio(metadata.get("Specificity Ratio")));
        guide.setStandardScaffoldSequence(metadata.get("Standard guide scaffold sequence?"));
        guide.setIvtConstructSource(metadata.get("IVT construct Source"));
        guide.setVectorId(metadata.get("Catalog#"));
        guide.setVectorName(metadata.get("Lab Name/ID"));
        guide.setVectorDescription(metadata.get("Vector Description"));
        guide.setVectorType(metadata.get("Vector Type"));
        guide.setAnnotatedMap(metadata.get("Annotated Map"));
        guide.setModifications(getModifications(metadata.get("Modifications")));
        guide.setForwardPrimer(metadata.get("Forward Primer"));
        guide.setReversePrimer(metadata.get("Reverse Primer"));
        guide.setRepeatSequence(getUpperCase(metadata.get("Repeat Sequence")));
        guide.setLinkerSequence(getUpperCase(metadata.get("Linker Sequence")));
        guide.setAntiRepeatSequence(getUpperCase(metadata.get("Anti Repeat Sequence")));
        guide.setStemloop1Sequence(getUpperCase(metadata.get("StemLoop 1 Sequence")));
        guide.setStemloop2Sequence(getUpperCase(metadata.get("StemLoop 2 Sequence")));
        guide.setStemloop3Sequence(getUpperCase(metadata.get("StemLoop 3 Sequence")));
        guide.setGuideCompatibility(metadata.get("Guide Compatibility"));
        guide.setFullGuide(metadata.get("Full Guide Sequence"));
  /* else if (cell1.getStringCellValue().equalsIgnoreCase("Off-target mutation detection method 1")) {
                        if(data != null && !data.equals(""))
                            offTarget.setDetectionMethod(data);
                    } else if (cell1.getStringCellValue().equalsIgnoreCase("Off-target sites detected (method 1)")) {
                        if(data != null && !data.equals("") && !data.equalsIgnoreCase("none detected")) {
                            offTarget.setNoOfSitesDetected(Double.valueOf(data).intValue());
                            offTargets.add(offTarget);
                        }
                    } else if (cell1.getStringCellValue().equalsIgnoreCase("Off-target mutation detection method 2")) {
                        offTarget = new OffTarget();
                        offTarget.setDetectionMethod(data);
                    } else if (cell1.getStringCellValue().equalsIgnoreCase("Off-target sites detected (method 2)")) {
                        if(data != null && !data.equals("") && !data.equalsIgnoreCase("none detected")) {
                            offTarget.setNoOfSitesDetected(Double.valueOf(data).intValue());
                            offTargets.add(offTarget);
                        }
                    }*/

        if (guide.getGuide_id() == 0 && Utils.isStringEmpty(guide.getGuide())) {
            info("  No guide");
        } else {
            long guideId = dao.getGuideId(guide);
            if (guideId == 0) {
                guide.setTier(tier);
                guideId = dao.insertGuide(guide);
                guide.setGuide_id(guideId);
                dao.insertGuideGenomeInfo(guide);
                info("  Inserted guide: " + guideId);
            } else {
                guide.setGuide_id(guideId);
                info("  Got guide by matching against DB: " + guideId);
                if( dao.updateGuideIfNeeded(guide) ) {
                    info("    guide updated in db");
                }
            }
            guides.add(guide.getGuide_id());
        }
    }

    private long loadEditor(HashMap<String, String> metadata, Editor editor) throws Exception {
        if (metadata.containsKey("SCGE ID") && metadata.get("SCGE ID") != null && !metadata.get("SCGE ID").isEmpty()) {
            editor.setId(new BigDecimal(metadata.get("SCGE ID")).longValue());
            info("Got editor: " + editor.getId());
            return editor.getId();
        }

        editor.setSource(metadata.get("Source"));
        //editor.setSymbol(metadata.get("Catalog#"));
        editor.setSpecies(metadata.get("Editor Species"));
        editor.setType(metadata.get("Editor Type"));
        editor.setSubtype(metadata.get("Editor Subtype"));
        editor.setSymbol(metadata.get("Editor Name"));
        editor.setEditorDescription(metadata.get("Editor Description"));
        editor.setAnnotatedMap(metadata.get("Annotated Map"));
        editor.setAlias(metadata.get("Editor Alias"));
        editor.setProteinSequence(getUpperCase(metadata.get("Protein Sequence")));
        editor.setSubstrateTarget(metadata.get("Editor Substrate"));
        editor.setPamPreference(metadata.get("PAM Preference"));
        editor.setEditorVariant(metadata.get("Editor Protein Variant"));
        editor.setFusion(metadata.get("Editor Fusion/Effector"));
        editor.setActivity(metadata.get("Editor Activity"));
        editor.setDsbCleavageType(metadata.get("Editor Cleavage Type"));
        editor.setTarget_sequence(getUpperCase(metadata.get("Target Sequence")));
        editor.setOrientation(metadata.get("Orientation (PNA)"));
        editor.setAssembly(metadata.get("Genome Version"));
        editor.setChr(metadata.get("Chromosome"));
        editor.setStart(getInttoStringValue(metadata.get("Chromosome Start")));
        editor.setStop(getInttoStringValue(metadata.get("Chromosome End")));
        editor.setStrand(getStrand(metadata.get("Chromosome Strand")));

        if (editor.getSymbol() == null || editor.getSymbol().equals("")) {
            return 0;
        } else {
            if( editor.getSymbol().contains("Alt-R")) {
                System.out.println("aaa");
            }
            long editorId = dao.getEditorId(editor);
            if (editorId == 0) {
                editor.setTier(tier);
                editorId = dao.insertEditor(editor);
                editor.setId(editorId);
                dao.insertEditorGenomeInfo(editor);
                info("Inserted editor: " + editorId);
            } else {
                editor.setId(editorId);
                info("Got editor by matching against DB: " + editorId);
                if( dao.updateEditorIfNeeded(editor) ) {
                    info("    #### editor updated in db");
                }
            }
            return editorId;
        }
    }

    private void loadVector(HashMap<String, String> metadata, Vector vector) throws Exception {
        if (metadata.containsKey("SCGE ID") && metadata.get("SCGE ID") != null && !metadata.get("SCGE ID").isEmpty()) {
            vector.setVectorId(new BigDecimal(metadata.get("SCGE ID")).longValue());
            info("Got vector: " + vector.getVectorId());
            vectors.add(vector.getVectorId());
        } else {
            vector.setLabId(metadata.get("RRID"));
            vector.setSource(metadata.get("Source"));
            vector.setName(metadata.get("Lab Name/ID"));
            vector.setSubtype(metadata.get("Vector Type"));
            vector.setGenomeSerotype(metadata.get("VV Genome Serotype"));
            vector.setCapsidSerotype(metadata.get("VV Capsid Serotype"));
            vector.setCapsidVariant(metadata.get("VV Capsid Variant"));
            vector.setAnnotatedMap(metadata.get("Annotated Map"));
            vector.setDescription(metadata.get("Vector Description"));
            vector.setTiterMethod(metadata.get("Titer Method"));
            vector.setType("Viral Vector");

            if (vector.getName() == null || vector.getName().equals("")) {
                info("No vector");
            } else {
                long vectorId = dao.getVectorId(vector);
                if (vectorId == 0) {
                    vector.setTier(tier);
                    vectorId = dao.insertVector(vector);
                    info(" Inserted vector: " + vectorId);
                } else info(" Got vector: " + vectorId);
                vectors.add(vectorId);
            }
        }
    }

    private long loadProtienConjugate(HashMap<String, String> metadata) throws Exception {

        if (metadata.containsKey("SCGE ID") && metadata.get("SCGE ID") != null && !metadata.get("SCGE ID").isEmpty()) {
            long id = new BigDecimal(metadata.get("SCGE ID")).longValue();
            return id;
        }

        Delivery delivery = new Delivery();
        delivery.setId(0);

        delivery.setSource(metadata.get("Source"));
        delivery.setLabId(metadata.get("Lab Name/ID"));
        delivery.setName(delivery.getLabId());
        delivery.setDescription(metadata.get("PCJ Description"));
        delivery.setType("Protein Conjugate");

        if( Utils.isStringEmpty(delivery.getLabId()) ) {
            return 0;
        }

        long deliveryId = dao.getDeliveryId(delivery);
        if (deliveryId == 0) {
            delivery.setTier(tier);
            deliveryId = dao.insertDelivery(delivery);
            delivery.setId(deliveryId);
        } else {
            info("  Got Protein Conjugate by matching against DB: " + deliveryId);

            delivery.setId(deliveryId);
            if( dao.updateDeliveryIfNeeded(delivery) ) {
                info("    Protein Conjugate data updated in db");
            }
        }
        return deliveryId;
    }

    private long loadVirusParticle(HashMap<String, String> metadata) throws Exception {
        if (metadata.containsKey("SCGE ID") && metadata.get("SCGE ID") != null && !metadata.get("SCGE ID").isEmpty()) {
            return new BigDecimal(metadata.get("SCGE ID")).longValue();
        }

        Delivery delivery = new Delivery();
        delivery.setId(0);
        delivery.setSource(metadata.get("Source"));
        delivery.setLabId(metadata.get("Lab Name/ID"));
        delivery.setName(metadata.get("Lab Name/ID"));
        delivery.setDescription(metadata.get("VLP Description"));

        delivery.setType("Virus Like Particle");
        if( Utils.isStringEmpty(delivery.getLabId()) ) {
            return 0;
        }

        long deliveryId = dao.getDeliveryId(delivery);
        if (deliveryId == 0) {
            delivery.setTier(tier);
            deliveryId = dao.insertDelivery(delivery);
            delivery.setId(deliveryId);
        } else {
            info("  Got Virus Particle by matching against DB: " + deliveryId);

            delivery.setId(deliveryId);
            if( dao.updateDeliveryIfNeeded(delivery) ) {
                info("    Virus Particle data updated in db");
            }
        }
        return deliveryId;
    }

    private long loadCommercialReagent(HashMap<String, String> metadata) throws Exception {
        if (metadata.containsKey("SCGE ID") && metadata.get("SCGE ID") != null && !metadata.get("SCGE ID").isEmpty()) {
            return new BigDecimal(metadata.get("SCGE ID")).longValue();
        }

        Delivery delivery = new Delivery();
        delivery.setId(0);
        delivery.setSource(metadata.get("Source"));
        delivery.setLabId(metadata.get("Lab Name/ID"));
        if( Utils.isStringEmpty(delivery.getLabId()) ) {
            delivery.setLabId(metadata.get("Catalog#"));
        }
        delivery.setName(metadata.get("Reagent Name"));
        delivery.setDescription(metadata.get("Reagent Description"));

        delivery.setType("Commercial Reagent");
        if( Utils.isStringEmpty(delivery.getLabId()) ) {
            return 0;
        }

        long deliveryId = dao.getDeliveryId(delivery);
        if (deliveryId == 0) {
            delivery.setTier(tier);
            deliveryId = dao.insertDelivery(delivery);
            delivery.setId(deliveryId);
        } else {
            info("  Got Commercial Reagent by matching against DB: " + deliveryId);

            delivery.setId(deliveryId);
            if( dao.updateDeliveryIfNeeded(delivery) ) {
                info("    Commercial Reagent data updated in db");
            }
        }
        return deliveryId;
    }

    private long loadAmphiphilicPeptide(HashMap<String, String> metadata) throws Exception {
        String scgeId = metadata.get("SCGE ID");
        if (!Utils.isStringEmpty(scgeId)) {
            return new BigDecimal(scgeId).longValue();
        }
        Delivery delivery = new Delivery();
        delivery.setId(0);

        delivery.setSource(metadata.get("Source"));
        delivery.setLabId(metadata.get("Lab Name/ID"));
        delivery.setName(metadata.get("Lab Name/ID"));
        delivery.setDescription(metadata.get("AP Description"));
        delivery.setSequence(metadata.get("AP Sequence"));

        delivery.setType("Amphiphilic peptide");
        if( Utils.isStringEmpty(delivery.getLabId()) ) {
            return 0;
        }

        long deliveryId = dao.getDeliveryId(delivery);
        if (deliveryId == 0) {
            delivery.setTier(tier);
            deliveryId = dao.insertDelivery(delivery);
            delivery.setId(deliveryId);
        } else {
            info("  Got AP by matching against DB: " + deliveryId);

            delivery.setId(deliveryId);
            if( dao.updateDeliveryIfNeeded(delivery) ) {
                info("    AP data updated in db");
            }
        }
        return deliveryId;
    }

    private long loadNanoparticle(HashMap<String, String> metadata) throws Exception {
        Delivery delivery = new Delivery();
        if (metadata.containsKey("SCGE ID") && metadata.get("SCGE ID") != null && !metadata.get("SCGE ID").isEmpty()) {
            delivery.setId(new BigDecimal(metadata.get("SCGE ID")).longValue());
            return delivery.getId();
        }
        delivery.setId(0);

        delivery.setSource(metadata.get("Source"));
        delivery.setLabId(metadata.get("Lab Name/ID"));
        delivery.setName(metadata.get("Lab Name/ID"));
        delivery.setDescription(metadata.get("NP Description"));
        delivery.setSubtype(metadata.get("NP Type"));
        delivery.setNpSize(metadata.get("NP size"));
        delivery.setNpPolydispersityIndex(metadata.get("NP Polydispersity index"));
        delivery.setZetaPotential(metadata.get("Zeta Potential"));

        delivery.setType("Nanoparticle");
        if( Utils.isStringEmpty(delivery.getLabId()) ) {
            return 0;
        }

        long deliveryId = dao.getDeliveryId(delivery);
        if (deliveryId == 0) {
            delivery.setTier(tier);
            deliveryId = dao.insertDelivery(delivery);
            delivery.setId(deliveryId);
            info("  inserted Nanoparticle: " + deliveryId);
        } else {
            info("  got Nanoparticle by matching against DB: " + deliveryId);

            delivery.setId(deliveryId);
            if( dao.updateDeliveryIfNeeded(delivery) ) {
                info("    ### nanoparticle data updated in db");
            }
        }
        return deliveryId;
    }

    private long loadAnimalModel(HashMap<String, String> metadata, Model model) throws Exception {
        String scgeId = metadata.get("SCGE ID");
        if (Utils.isStringEmpty(scgeId)) {
            scgeId = metadata.get("SCGE OD"); // typo in format v5
        }
        if (!Utils.isStringEmpty(scgeId)) {
            model.setModelId(new BigDecimal(scgeId).longValue());
            info("  Got model: " + model.getModelId());
            return model.getModelId();
        }

        model.setRrid(metadata.get("RRID link"));
        model.setParentalOrigin(metadata.get("Parental Origin"));

        model.setOrganism(metadata.get("Species"));
        model.setSource(metadata.get("Source"));
        model.setCatalog(metadata.get("Vendor Strain Code"));

        model.setName(metadata.get("Common strain name"));

        String val = metadata.get("Strain Symbol"); // old field name
        if (val == null) val = metadata.get("Official strain symbol");
        model.setOfficialName(val);

        //ensure display name is not null
        model.setDisplayName(metadata.get("Display Name"));
        if( Utils.isStringEmpty(model.getDisplayName()) ) {
            model.setDisplayName(model.getName());
        }
        if( Utils.isStringEmpty(model.getDisplayName()) ) {
            model.setDisplayName(model.getOfficialName());
        }

        model.setStrainAlias(metadata.get("Strain Aliases"));
        model.setDescription(metadata.get("Strain Description"));
        model.setTransgene(metadata.get("Integrated Transgene"));
        model.setAnnotatedMap(metadata.get("Annotated Map"));
        model.setTransgeneDescription(metadata.get("Transgene Description"));
        model.setTransgeneReporter(metadata.get("Transgene Reporter"));

        return loadModelInDb(model, "Animal");
    }

    private long loadCellModel(HashMap<String, String> metadata, Model model) throws Exception {
        if (metadata.containsKey("SCGE ID") && metadata.get("SCGE ID") != null && !metadata.get("SCGE ID").isEmpty()) {
            model.setModelId(new BigDecimal(metadata.get("SCGE ID")).longValue());
            info(" Got model: " + model.getModelId());
            return model.getModelId();
        }
        model.setRrid(metadata.get("RRID link"));
        model.setSource(metadata.get("Source"));
        model.setCatalog(metadata.get("Catalog#"));
        model.setParentalOrigin(metadata.get("Parental Origin"));
        model.setName(metadata.get("CM Name"));
        model.setDisplayName(metadata.get("CM Name"));
        model.setDescription(metadata.get("CM Description"));
        model.setOfficialName(metadata.get("Official Name"));
        model.setOrganism(metadata.get("Species"));
        model.setSex(getSex(metadata.get("Sex")));
        model.setSubtype(metadata.get("Type"));
        model.setTransgene(metadata.get("Integrated Transgene"));
        model.setAnnotatedMap(metadata.get("Annotated Map"));
        model.setTransgeneDescription(metadata.get("Transgene Description"));
        model.setTransgeneReporter(metadata.get("Transgene Reporter"));

        return loadModelInDb(model, "Cell");
    }

    private long loadOrganoidModel(HashMap<String, String> metadata, Model model) throws Exception {
        if (metadata.containsKey("SCGE ID") && metadata.get("SCGE ID") != null && !metadata.get("SCGE ID").isEmpty()) {
            model.setModelId(new BigDecimal(metadata.get("SCGE ID")).longValue());
            info(" Got model: " + model.getModelId());
            return model.getModelId();
        }
        model.setName(metadata.get("Organoid Name"));
        model.setSubtype(metadata.get("Organoid Type"));
        model.setDescription(metadata.get("Organoid description"));
        model.setSource(metadata.get("Source"));
        model.setCatalog(metadata.get("Catalog#"));
        model.setOrganism(metadata.get("Species"));
        model.setSex(getSex(metadata.get("Sex")));
        model.setTransgene(metadata.get("Transgene"));
        model.setTransgeneDescription(metadata.get("Transgene Description"));
        model.setTransgeneReporter(metadata.get("Transgene Reporter"));
        model.setAnnotatedMap(metadata.get("Annotated Map"));
        model.setParentalOrigin(metadata.get("Cell line origin"));

        model.setRrid(metadata.get("RRID link"));
        model.setOfficialName(metadata.get("Official Name"));

        // if 'display name' not provided, use 'name' as 'display name'
        model.setDisplayName( Utils.NVL( metadata.get("Display Name"), model.getName()) );

        return loadModelInDb(model, "Organoid");
    }

    private long loadModelInDb(Model model, String modelType) throws Exception {
        model.setType(modelType);
        if (model.getName() == null || model.getName().equals("")) {
            return 0;
        }

        long modelId = dao.getModelId(model);
        if (modelId == 0) {
            model.setTier(tier);
            modelId = dao.insertModel(model);
            model.setModelId(modelId);
            info(" Inserted model: " + modelId);
        } else {
            info("  got model by matching against DB: " + modelId);

            model.setModelId(modelId);
            if( dao.updateModelIfNeeded(model) ) {
                info("   ### model data updated in db");
            }
        }
        return modelId;
    }

    private int loadApplicationMethod(HashMap<String, String> metadata, ApplicationMethod method) throws Exception {

        method.setSiteOfApplication(metadata.get("Target Tissue"));
        method.setApplicationType(metadata.get("Delivery route"));
        if (metadata.containsKey("Delivery method")) {
            method.setApplicationType(metadata.get("Delivery method"));
        }
        method.setDaysPostInjection(metadata.get("Time post delivery sample collected"));
        method.setDosage(metadata.get("Dosage (incl units)"));
        method.setInjectionRate(metadata.get("Injection rate"));
        method.setInjectionFrequency(metadata.get("Injection frequency"));
        method.setInjectionVolume(metadata.get("Injection volume"));
        method.setEditorFormat(metadata.get("Format of editor/cargo"));

        int methodId = dao.getMethodId(method);
        if (methodId == 0) {
            methodId = dao.insertMethod(method);
            info("  inserted application method " + methodId);
        } else {
            info("  got application method " + methodId);
        }
        return methodId;
    }

    private void loadAntibody(HashMap<String, String> metadata) throws Exception {
        Antibody antibody = new Antibody();
        if (metadata.containsKey("SCGE ID") && metadata.get("SCGE ID") != null && !metadata.get("SCGE ID").isEmpty()) {
            antibody.setAntibodyId(new BigDecimal(metadata.get("SCGE ID")).intValue());
            info("  got antibody " + antibody.getAntibodyId());
            antibodies.add(antibody.getAntibodyId());
        } else {
            antibody.setRrid(metadata.get("RRID"));
            antibody.setOtherId(metadata.get("Other ID"));
            antibody.setDescription(metadata.get("Antibody description"));

            if ((antibody.getRrid() == null || antibody.getRrid().equals("")) && (antibody.getOtherId() == null || antibody.getOtherId().equals("")))
                info(" No antibody");
            else {
                int antibodyId = dao.getAntibodyId(antibody);
                if (antibodyId == 0) {
                    antibodyId = dao.insertAntibody(antibody);
                    info(" Inserted Antibody " + antibodyId);
                } else info("  got antibody " + antibodyId);
                antibodies.add(antibodyId);
            }
        }
    }

    private long loadHrDonor(HashMap<String, String> metadata, HRDonor hrdonor) throws Exception {
        if (metadata.containsKey("SCGE ID") && metadata.get("SCGE ID") != null && !metadata.get("SCGE ID").isEmpty()) {
            hrdonor.setId(new BigDecimal(metadata.get("SCGE ID")).longValue());
            info(" got HrDonor " + hrdonor.getId());
            return hrdonor.getId();
        }
        hrdonor.setSource(metadata.get("Source"));
        hrdonor.setLabId(metadata.get("HR Donor Lab Name/ID"));
        hrdonor.setDescription(metadata.get("Donor Description"));
        hrdonor.setType(metadata.get("HR Donor Type"));
        hrdonor.setSequence(getUpperCase(metadata.get("HR Donor Sequence")));
        hrdonor.setModification(getUpperCase(metadata.get("HR Donor Modification")));
        if (hrdonor.getId() == 0 && (hrdonor.getLabId() == null || hrdonor.getLabId().equals("")))
            return 0;
        else {
            long hrdonorId = dao.getHrdonorId(hrdonor);
            if (hrdonorId == 0) {
                hrdonor.setTier(tier);
                hrdonorId = dao.insertHrdonor(hrdonor);
                info(" inserted HrDonor " + hrdonorId);
            } else info(" got HrDonor " + hrdonorId);
            return hrdonorId;
        }
    }

    private String getUpperCase(String value) {
        if (value != null && !value.isEmpty()) {
            return value.toUpperCase();
        } else return null;
    }

    private String getInttoStringValue(String value) {
        if (value != null && !value.isEmpty()) {
            return String.valueOf(new BigDecimal(value).intValue());
        } else return null;
    }

    private String getStrand(String value) {
        if (value != null && !value.equals("")) {
            if (value.equalsIgnoreCase("plus"))
                return "+";
            else if (value.equalsIgnoreCase("minus"))
                return "-";
            else return value;
        }
        return null;
    }

    private String getModifications(String value) {
        if (value != null && !value.isEmpty()) {
            return value.toUpperCase();
        } else return "none";
    }

    private String getSpecificityRatio(String value) {
        if (value != null && !value.isEmpty()) {
            BigDecimal bd = new BigDecimal(value).setScale(3, RoundingMode.HALF_UP);
            return String.valueOf(bd.doubleValue());
        }
        return null;
    }

    private String getSex(String value) {
        if ( value != null ) {

            switch( value.toLowerCase() ) {
                case "m":
                case "male":
                    return "Male";
                case "f":
                case "female":
                    return "Female";
                case "m/f":
                case "f/m":
                case "mixed":
                    return "Mixed";
            }
        }
        return null;
    }


    public void info(String msg) {
        log.info(studyId + " " + experimentId + " " + msg);
    }

    public void debug(String msg) {
        log.debug(studyId + " " + experimentId + " " + msg);
    }

    public void finish() throws Exception {
        getDao().updateExperimentLastModifiedDate(experimentId);

        info("=== OK ===");
        log.info("");
        log.info("");
    }

    void loadExperimentRecordDetails(long expRecId, Map<String,String> expDetails) throws Exception {

        int inserted = 0;
        int updated = 0;
        int deleted = 0;
        int upToDate = 0;

        Map<String,String> detailsInDb = getDao().getExperimentRecordDetails(expRecId);

        Set<String> namesInDb = new HashSet<>(detailsInDb.keySet());
        Set<String> namesIncoming = expDetails.keySet();

        // determine details to be updated
        for( String name: namesIncoming ) {
            namesInDb.remove(name);

            String valueIncoming = expDetails.get(name);
            // if it is a number ending with ".0", then remove ".0"
            try {
                Double.parseDouble(valueIncoming);
                if( valueIncoming.endsWith(".0") ) {
                    valueIncoming = valueIncoming.substring(0, valueIncoming.length()-2);
                }
            } catch( NumberFormatException ignore) {}


            String valueInDb = detailsInDb.get(name);
            if( valueInDb==null ) {
                getDao().insertExperimentRecordDetails(expRecId, name, valueIncoming);
                inserted++;
            } else {
                if( valueIncoming.equals(valueInDb) ) {
                    upToDate++;
                } else {
                    getDao().updateExperimentRecordDetails(expRecId, name, valueIncoming);
                    updated++;
                }
            }
        }

        // whatever is left in namesInDb hashset, is to be deleted
        for( String name: namesInDb ) {
            getDao().deleteExperimentRecordDetails(expRecId, name);
        }

        if( inserted+deleted+updated+upToDate>0 ) {
            String msg = "  experiment details: ";
            if( inserted>0 ) {
                msg += "  INSERTED="+inserted;
            }
            if( deleted>0 ) {
                msg += "  DELETED="+deleted;
            }
            if( updated>0 ) {
                msg += "  UPDATED="+updated;
            }
            if( upToDate>0 ) {
                msg += "  UP_TO_DATE="+upToDate;
            }

            info(msg);
        }
    }

    public void loadExperimentNumericData(long expId, String worksheet, int dataCols) throws Exception {
        loadExperimentData(expId, worksheet, dataCols, 3);
    }

    public void loadExperimentSignalData(long expId, String worksheet, int dataCols) throws Exception {
        loadExperimentData(expId, worksheet, dataCols, 3);
    }

    public void loadExperimentData(long expId, String worksheet, int dataCols, int firstDataColNr) throws Exception {

        experimentId = expId;
        expType = worksheet;

        String expType2 = expType.contains("Vivo") ? "In Vivo" : "In Vitro";
        boolean newExperimentCreated = getDao().createExperimentIfMissing(studyId, experimentId, expType2);
        if( newExperimentCreated ) {
            info("=== new experiment created");
        } else {
            int rowsDeleted = getDao().deleteExperimentData(experimentId, studyId);
            info("=== deleted rows for experiment " + experimentId + ": " + rowsDeleted);
        }

        // columns of numeric data
        for (int column = firstDataColNr; column < firstDataColNr + dataCols; column++) { // 0-based column in the excel sheet
            String name = "Condition 1"; //exp record name to be loaded, if not present
            loadMetaData(column, name);
        }
        Mean.loadMean(experimentId, this);

        finish();
    }


    public LoadDAO getDao() {
        return dao;
    }

    public void setDao(LoadDAO dao) {
        this.dao = dao;
    }

    public Logger getLog() {
        return log;
    }
}


