package edu.mcw.scge;

import edu.mcw.rgd.process.Utils;
import edu.mcw.scge.datamodel.*;
import edu.mcw.scge.datamodel.Vector;
import org.apache.commons.lang3.text.WordUtils;
import org.apache.log4j.Logger;
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

    Logger log = Logger.getLogger("core");
    Logger logSummary = Logger.getLogger("status");
    LoadDAO dao = new LoadDAO();

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
        MODEL,
        EXPERIMENT_DETAILS,
    }
    public int studyId = 1066;
    public long experimentId = 18000000060L;
    public String fileName = "/test/Bankiewicz2.xlsx";
    public String expType = "In Vivo";
    public int tier = 0;

    Set<Long> vectors = new TreeSet<>();
    Set<Long> guides = new TreeSet<>();
    Set<Integer> antibodies = new TreeSet<>();

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
            String newData = data.replace("\u00A0", "").trim();
            if (newData.length() != data.length()) {
                data = newData;
            }
        }
        return data;
    }

    public void loadMetaData(int column, String name, boolean qualitativeData) throws Exception {

        // reset lists
        vectors.clear();
        guides.clear();
        antibodies.clear();


        FileInputStream fis = new FileInputStream(fileName);
        XSSFWorkbook wb = new XSSFWorkbook(fis);
        XSSFSheet sheet = wb.getSheet(expType);      //creating a Sheet object to retrieve object

        SECTION section = SECTION.NONE;
        boolean cellData = false;
        boolean traData = false;
        boolean antibodyData = false;
        Guide guide = new Guide();
        Editor editor = new Editor();
        Delivery delivery = new Delivery();
        Model model = new Model();
        Vector vector = new Vector();
        ApplicationMethod method = new ApplicationMethod();
        HRDonor hrDonor = new HRDonor();
        OffTarget offTarget = new OffTarget();
        List<OffTarget> offTargets = new ArrayList<>();

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
            String cell0Data = cell0==null ? "" : cell0.getStringCellValue();
            String cell1Data = cell1==null ? "" : cell1.getStringCellValue();
            boolean isEndOfSection = cell1Data.equalsIgnoreCase("Related publication description");


            if( cell0Data.equalsIgnoreCase("EDITOR")) {
                if (data != null && !data.equals(""))
                    experiment.setExperimentName(data);
                System.out.println("======");
                System.out.println("exprec: "+experiment.getExperimentName());
            }
            metadata.put(cell1Data, data);

            // EDITOR
            if( cell0Data.equalsIgnoreCase("EDITOR") ) {
                section = SECTION.EDITOR;
            }
            if( section==SECTION.EDITOR && isEndOfSection ) {
                experiment.setEditorId(loadEditor(metadata, editor));
                metadata.clear();
            }

            // GUIDE
            if( cell0Data.equalsIgnoreCase("GUIDE") ) {
                section = SECTION.GUIDE;
            }
            if (section==SECTION.GUIDE && isEndOfSection ) {
                loadGuide(metadata, guide);
                metadata.clear();
            }

            // MODEL
            if( cell0Data.equalsIgnoreCase("Animal Model (AM)") ) {
                section = SECTION.MODEL;
            }
            if (section==SECTION.MODEL && isEndOfSection ) {
                experiment.setModelId(loadAnimalModel(metadata, model));
                metadata.clear();
            }

            // HR DONOR
            if( cell0Data.equalsIgnoreCase("HR DONOR") ) {
                section = SECTION.HRDONOR;
            }
            if (section==SECTION.HRDONOR && isEndOfSection ) {
                experiment.setHrdonorId(loadHrDonor(metadata, hrDonor));
                metadata.clear();
            }

            // VECTOR/FORMAT
            if( cell0Data.equalsIgnoreCase("VECTOR/FORMAT") ) {
                section = SECTION.VECTOR;
            }
            if (section==SECTION.VECTOR && isEndOfSection ) {
                loadVector(metadata, vector);
                metadata.clear();
            }

            // DELIVERY SYSTEM: PROTEIN CONJUGATE
            if( cell0Data.equalsIgnoreCase("Protein Conjugate") ) {
                section = SECTION.DS_PROTEIN_CONJUGATE;
            }
            if (section==SECTION.DS_PROTEIN_CONJUGATE && isEndOfSection ) {
                long dsId = loadProtienConjugate(metadata, delivery);
                if( dsId!=0 ) {
                    System.out.println("Delivery System (Protein Conjugate) = "+dsId);
                    experiment.setDeliverySystemId(dsId);
                }
                metadata.clear();
            }

            // DELIVERY SYSTEM: VIRUS LIKE PARTICLE
            if( cell0Data.equalsIgnoreCase("Virus Like Particle") ) {
                section = SECTION.DS_VIRUS_LIKE_PARTICLE;
            }
            if (section==SECTION.DS_VIRUS_LIKE_PARTICLE && isEndOfSection ) {
                long dsId = loadVirusParticle(metadata, delivery);
                if( dsId!=0 ) {
                    System.out.println("Delivery System (Virus Like Particle) = "+dsId);
                    experiment.setDeliverySystemId(dsId);
                }
                metadata.clear();
            }

            // DELIVERY SYSTEM: COMMERCIAL REAGENT
            if( cell0Data.equalsIgnoreCase("Commercial Reagent") ) {
                section = SECTION.DS_COMMERCIAL_REAGENT;
            }
            if (section==SECTION.DS_COMMERCIAL_REAGENT && isEndOfSection ) {
                long dsId = loadCommercialReagent(metadata, delivery);
                if( dsId!=0 ) {
                    System.out.println("Delivery System (Commercial Reagent) = "+dsId);
                    experiment.setDeliverySystemId(dsId);
                }
                metadata.clear();
            }

            // DELIVERY SYSTEM: AMPHIPHILIC PEPTIDE
            if( cell0Data.equalsIgnoreCase("Amphiphilic peptide") ) {
                section = SECTION.DS_AMPHIPHILIC_PEPTIDE;
            }
            if (section==SECTION.DS_AMPHIPHILIC_PEPTIDE && isEndOfSection ) {
                long dsId = loadAmphiphilicPeptide(metadata, delivery);
                if( dsId!=0 ) {
                    System.out.println("Delivery System (Amphiphilic peptide) = "+dsId);
                    experiment.setDeliverySystemId(dsId);
                }
                metadata.clear();
            }

            // DELIVERY SYSTEM: NANOPARTICLE
            if( cell0Data.equalsIgnoreCase("Nanoparticle") ) {
                section = SECTION.DS_NANOPARTICLE;
            }
            if (section==SECTION.DS_NANOPARTICLE && isEndOfSection ) {
                long dsId = loadNanoparticle(metadata, delivery);
                if( dsId!=0 ) {
                    System.out.println("Delivery System (Nanoparticle) = "+dsId);
                    experiment.setDeliverySystemId(dsId);
                }
                metadata.clear();
            }


            if (cellData ||
                    (cell0 != null && cell0.getStringCellValue().equalsIgnoreCase("Cell model"))) {
                cellData = true;
                if (cell1.getStringCellValue().equalsIgnoreCase("Related publication description")) {
                    experiment.setModelId(loadCellModel(metadata, model));
                    cellData = false;
                    metadata.clear();
                }
            }

            // EXPERIMENT
            if( cell0Data.equalsIgnoreCase("Experiment Details") ) {
                section = SECTION.EXPERIMENT_DETAILS;
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
                            System.out.println("Inserted model: " + modelId);
                        } else System.out.println("Got model: " + modelId);
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
        }

        if (expType.contains("Vitro")) {
            long experimentRecId = dao.insertExperimentRecord(experiment);
            for (long guideId : guides) {
                dao.insertGuideAssoc(experimentRecId, guideId);
                for (OffTarget o : offTargets) {
                    o.setGuideId(guideId);
                    dao.insertOffTarget(o);
                }
            }
            for (long vectorId : vectors)
                dao.insertVectorAssoc(experimentRecId, vectorId);

            for (int antibodyId : antibodies)
                dao.insertAntibodyAssoc(experimentRecId,antibodyId);

            loadData(experimentRecId, column, qualitativeData);
            vectors.clear();
            guides.clear();
        } else {
            loadData(experiment, column, qualitativeData);
        }
    }

    /*
    public void loadStudy() throws Exception {
        FileInputStream fis = new FileInputStream(new File(fileName));
//creating workbook instance that refers to .xls file
        XSSFWorkbook wb = new XSSFWorkbook(fis);
        XSSFSheet sheet = wb.getSheetAt(0);     //creating a Sheet object to retrieve object
        Study s = new Study();

        for (Row row : sheet) {
            Cell cell0 = row.getCell(0);
            Cell cell1 = row.getCell(1);
            Cell cell = row.getCell(2);
            String data;
            if (cell.getCellType() == Cell.CELL_TYPE_STRING || cell.getCellType() == Cell.CELL_TYPE_BLANK)
                data = cell.getStringCellValue();
            else data = String.valueOf(cell.getNumericCellValue());
            if (cell1.getStringCellValue().equalsIgnoreCase("PI_email")) {
                Person p = dao.getPersonByEmail(data);
                s.setLabId(p.getInstitution());
                s.setPiId(p.getId());
                s.setTier(1);
                s.setSubmissionDate(new Date());
            } else if (cell1.getStringCellValue().equalsIgnoreCase("POC_email")) {
                Person p = dao.getPersonByEmail(data);
                s.setSubmitterId(p.getId());
            }

        }
        dao.insertStudy(s);
    }
    */

    public void loadData(long expRecId, int column, boolean qualitativeData) throws Exception {
        FileInputStream fis = new FileInputStream(fileName);
        XSSFWorkbook wb = new XSSFWorkbook(fis);
        XSSFSheet sheet = wb.getSheet(expType);
        ExperimentResultDetail result = new ExperimentResultDetail();
        result.setExperimentRecordId(expRecId);

        for (Row row : sheet) {
            Cell cell1 = row.getCell(1);
            Cell cell = row.getCell(column);
            String data;
            if (row.getRowNum() < 3 || cell1 == null)
                continue;

            if (cell == null)
                data = null;
            else if (cell.getCellType() == Cell.CELL_TYPE_STRING || cell.getCellType() == Cell.CELL_TYPE_BLANK)
                data = cell.getStringCellValue();
            else data = new Double(cell.getNumericCellValue()).toString();

            if (cell1.getStringCellValue().equalsIgnoreCase("Assay Description") || cell1.getStringCellValue().equalsIgnoreCase("Assay_Description")) {
                result.setAssayDescription(data);
            }
            if (cell1.getStringCellValue().equalsIgnoreCase("Edit Type")) {
                result.setEditType(data);
            }
            if (cell1.getStringCellValue().equalsIgnoreCase("Biological/Transfection/Delivery Replicates") ||
                    cell1.getStringCellValue().equalsIgnoreCase("Biological Replicates")) {

                if (data != null && !data.equals("")) {

                    if (data.startsWith("n=")) {
                        data = data.substring(2).trim();
                    }
                    result.setNumberOfSamples(Double.valueOf(data).intValue());
                }
            }
            if (cell1.getStringCellValue().equalsIgnoreCase("Units")) {
                if( qualitativeData ) {
                    result.setUnits("Signal");
                } else {
                    result.setUnits(data);
                }
            }
            if (cell1.getStringCellValue().equalsIgnoreCase("Editing Efficiency") ||
                    cell1.getStringCellValue().equalsIgnoreCase("Delivery Efficiency")) {

                if (data != null && !data.equals("")) {
                    if (cell1.getStringCellValue().equalsIgnoreCase("Editing Efficiency"))
                        result.setResultType("Editing Efficiency");
                    else result.setResultType("Delivery Efficiency");
                    long resultId = dao.insertExperimentResult(result);
                    String valueString = data;
                    String[] values = valueString.split(",");
                    for (int i = 0; i < values.length; i++) {
                        ExperimentResultDetail detail = new ExperimentResultDetail();
                        detail.setResultId(resultId);
                        detail.setReplicate(i + 1);
                        detail.setResult(values[i].trim().toLowerCase());
                        dao.insertExperimentResultDetail(detail);
                    }
                }
            }

        }
    }

    public void loadData(ExperimentRecord expRec, int column, boolean qualitativeData) throws Exception {
        FileInputStream fis = new FileInputStream(fileName);
        XSSFWorkbook wb = new XSSFWorkbook(fis);
        XSSFSheet sheet = wb.getSheet(expType);

        boolean editingData = false;
        boolean deliveryData = false;
        ExperimentResultDetail result = new ExperimentResultDetail();


        for (Row row : sheet) {

            Cell cell0 = row.getCell(0);
            Cell cell1 = row.getCell(1);
            Cell cell2 = row.getCell(2);
            Cell cell = row.getCell(column);

            if (row.getRowNum() < 3 || cell1 == null)
                continue;

            String data = getCellData(cell);


            if (cell0 != null && cell0.getStringCellValue().equalsIgnoreCase("Editing Efficiency"))
                editingData = true;

            if (cell0 != null && cell0.getStringCellValue().equalsIgnoreCase("Delivery Efficiency"))
                deliveryData = true;

            if (cell1.getStringCellValue().equalsIgnoreCase("Data_File_Name") || cell1.getStringCellValue().equalsIgnoreCase("Related Protocol") ) {
                editingData = false;
                deliveryData = false;

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
                    if( spacePos>0 ) {
                        data = data.substring(0, spacePos);
                    }
                    result.setNumberOfSamples(Double.valueOf(data).intValue());
                }
            } else if (cell1.getStringCellValue().equalsIgnoreCase("Units")) {
                if( qualitativeData ) {
                    result.setUnits("Signal");
                } else {
                    result.setUnits(data);
                }
            } else if (cell1.getStringCellValue().equalsIgnoreCase("Edit Type")) {
                result.setEditType(data);
            } else if (cell1.getStringCellValue().equalsIgnoreCase("Measure is Normalized")) {
                //System.out.println("ignore");
            } else if (editingData || deliveryData) {

                if (editingData)
                    result.setResultType("Editing Efficiency");
                else result.setResultType("Delivery Efficiency");

                if (data != null && !data.equals("") && !data.equalsIgnoreCase("ND")) {
                    System.out.println(cell1);
                    expRec.setTissueId(cell1.getStringCellValue());
                    if (cell2 != null)
                        expRec.setCellType(cell2.getStringCellValue());
                    expRec.setOrganSystemID(cell0.getStringCellValue());
                    long expRecId = dao.insertExperimentRecord(expRec);
                    for (long guideId : guides) {
                        if(guideId != 0)
                            dao.insertGuideAssoc(expRecId, guideId);
                    }
                    for (long vectorId : vectors) {
                        if(vectorId != 0)
                            dao.insertVectorAssoc(expRecId, vectorId);
                    }
                    for (int antibodyId : antibodies) {
                        if(antibodyId != 0)
                        dao.insertAntibodyAssoc(expRecId, antibodyId);
                    }
                    result.setExperimentRecordId(expRecId);
                    long resultId = dao.insertExperimentResult(result);
                    String valueString = data;
                    String[] values = valueString.split(",");
                    for (int i = 0; i < values.length; i++) {
                        ExperimentResultDetail detail = new ExperimentResultDetail();
                        detail.setResultId(resultId);
                        detail.setReplicate(i + 1);
                        detail.setResult(values[i].trim());
                        dao.insertExperimentResultDetail(detail);
                    }
                }
            }
        }
    }

    public void loadOffTargetSites() throws Exception {
        try {
            BufferedReader br = new BufferedReader(new FileReader("E:\\Tsai_Submission 1_ChangeSeq_Reads.csv"));
            String line = "";
            boolean start = false;
            List<Guide> guides = dao.getAllGuides();
            HashMap<String, Long> guideIds = new HashMap<>();
            for (Guide g : guides) {
                guideIds.put(g.getGuide(), g.getGuide_id());
            }
            while ((line = br.readLine()) != null) {
                String[] data = line.split(",");

                if (start) {
                    OffTargetSite o = new OffTargetSite();
                    o.setOfftargetSequence(data[1]);
                    o.setChromosome(data[2]);
                    o.setStart(data[3]);
                    o.setStop(data[4]);
                    o.setStrand(data[5]);
                    o.setMismatches(Integer.parseInt(data[6]));
                    o.setTarget(data[7]);
                    o.setSeqType("Change_seq");
                    o.setNoOfReads(Integer.parseInt(data[8]));
                    if (guideIds.containsKey(data[0])) {
                        o.setGuideId(guideIds.get(data[0]));
                        dao.insertOffTargetSite(o);
                    } else {
                        String guide = data[0].replace("site_", "site_0");
                        if (guideIds.containsKey(guide)) {
                            o.setGuideId(guideIds.get(guide));
                            dao.insertOffTargetSite(o);
                        } else System.out.println(data[0]);
                    }
                }
                start = true;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadGuide(HashMap<String, String> metadata, Guide guide) throws Exception {
        if (metadata.containsKey("SCGE ID") && metadata.get("SCGE ID") != null && !metadata.get("SCGE ID").isEmpty()) {
            guide.setGuide_id(new BigDecimal(metadata.get("SCGE ID").trim()).longValue());
            guides.add(guide.getGuide_id());
            System.out.println("  Got guide by SCGE ID: " + guide.getGuide_id());
        } else {
            guide.setGuideDescription(metadata.get("Guide Description"));
            guide.setSource(metadata.get("Source"));
            guide.setSpecies(metadata.get("Species"));
            guide.setGrnaLabId(metadata.get("Lab gRNA Name/ID"));
            guide.setGuide(metadata.get("Lab gRNA Name/ID"));
            guide.setTargetLocus(metadata.get("Target Locus"));
            guide.setTargetSequence(getUpperCase(metadata.get("Target Sequence")));
            guide.setPam(getUpperCase(metadata.get("Target Sequence+PAM")));
            guide.setAssembly(metadata.get("Genome Version"));
            guide.setChr(metadata.get("Chromosome"));
            guide.setStart(getInttoStringValue(metadata.get("Chromosome Start")));
            guide.setStop(getInttoStringValue(metadata.get("Chromosome End")));
            guide.setStrand(getStrand(metadata.get("Chromosome Strand")));
            guide.setSpacerSequence(getUpperCase(metadata.get("Spacer Sequence")));
            guide.setSpacerLength(metadata.get("Spacer Length"));
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
            if (guide.getGuide_id() == 0 && (guide.getGuide() == null || guide.getGuide().equals("")))
                System.out.println("  No guide");
            else {
                long guideId = dao.getGuideId(guide);
                if (guideId == 0) {
                    guide.setTier(tier);
                    guideId = dao.insertGuide(guide);
                    guide.setGuide_id(guideId);
                    dao.insertGuideGenomeInfo(guide);
                    System.out.println("  Inserted guide: " + guideId);
                } else {
                    guide.setGuide_id(guideId);
                    System.out.println("  Got guide by matching against DB: " + guideId);
                }
                guides.add(guide.getGuide_id());
            }
        }
    }

    private long loadEditor(HashMap<String, String> metadata, Editor editor) throws Exception {
        if (metadata.containsKey("SCGE ID") && metadata.get("SCGE ID") != null && !metadata.get("SCGE ID").isEmpty()) {
            editor.setId(new BigDecimal(metadata.get("SCGE ID")).longValue());
            System.out.println("Got editor: " + editor.getId());
            return editor.getId();
        }
        editor.setSource(metadata.get("Source"));
        //editor.setSymbol(metadata.get("Catalog#"));
        editor.setSpecies(metadata.get("Editor Species"));
        editor.setType(metadata.get("Editor Type"));
        editor.setSubType(metadata.get("Editor Subtype"));
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

        if (editor.getSymbol() == null || editor.getSymbol().equals(""))
            return 0;
        else {
                long editorId = dao.getEditorId(editor);
                if (editorId == 0) {
                    editor.setTier(tier);
                    editorId = dao.insertEditor(editor);
                    editor.setId(editorId);
                    dao.insertEditorGenomeInfo(editor);
                    System.out.println("Inserted editor: " + editorId);
                } else System.out.println("Got editor: " + editorId);
                return editorId;
        }
    }

    private void loadVector(HashMap<String, String> metadata, Vector vector) throws Exception {
        if (metadata.containsKey("SCGE ID") && metadata.get("SCGE ID") != null && !metadata.get("SCGE ID").isEmpty()) {
            vector.setVectorId(new BigDecimal(metadata.get("SCGE ID")).longValue());
            System.out.println("Got vector: " + vector.getVectorId());
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
                System.out.println("No vector");
            } else {
                    long vectorId = dao.getVectorId(vector);
                    if (vectorId == 0) {
                        vector.setTier(tier);
                        vectorId = dao.insertVector(vector);
                        System.out.println("Inserted vector: " + vectorId);
                    } else System.out.println("Got vector: " + vectorId);
                    vectors.add(vectorId);
            }
        }
    }

    private long loadProtienConjugate(HashMap<String, String> metadata, Delivery delivery) throws Exception {
        if (metadata.containsKey("SCGE ID") && metadata.get("SCGE ID") != null && !metadata.get("SCGE ID").isEmpty()) {
            delivery.setId(new BigDecimal(metadata.get("SCGE ID")).longValue());
        }

        delivery.setSource(metadata.get("Source"));
        delivery.setLabId(metadata.get("Lab Name/ID"));
        delivery.setDescription(metadata.get("PCJ Description"));
        delivery.setType("Protein Conjugate");
        if (delivery.getId() == 0 && (delivery.getLabId() == null || delivery.getLabId().equals("")))
            return 0;
        else {
            if (delivery.getId() == 0) {
                long deliveryId = dao.getDeliveryId(delivery);
                if (deliveryId == 0) {
                    delivery.setTier(tier);
                    deliveryId = dao.insertDelivery(delivery);
                }
                return deliveryId;
            }
            return 0;
        }
    }

    private long loadVirusParticle(HashMap<String, String> metadata, Delivery delivery) throws Exception {
        if (metadata.containsKey("SCGE ID") && metadata.get("SCGE ID") != null && !metadata.get("SCGE ID").isEmpty()) {
            delivery.setId(new BigDecimal(metadata.get("SCGE ID")).longValue());
        }
        delivery.setSource(metadata.get("Source"));
        delivery.setLabId(metadata.get("Lab Name/ID"));
        delivery.setName(metadata.get("Lab Name/ID"));
        delivery.setDescription(metadata.get("VLP Description"));
        delivery.setType("Virus Like Particle");
        if (delivery.getId() == 0 && (delivery.getLabId() == null || delivery.getLabId().equals("")))
            return 0;
        else {
            if (delivery.getId() == 0) {
                long deliveryId = dao.getDeliveryId(delivery);
                if (deliveryId == 0) {
                    delivery.setTier(tier);
                    deliveryId = dao.insertDelivery(delivery);
                }
                return deliveryId;
            }
            return 0;
        }
    }

    private long loadCommercialReagent(HashMap<String, String> metadata, Delivery delivery) throws Exception {
        if (metadata.containsKey("SCGE ID") && metadata.get("SCGE ID") != null && !metadata.get("SCGE ID").isEmpty()) {
            delivery.setId(new BigDecimal(metadata.get("SCGE ID")).longValue());
        }
        delivery.setSource(metadata.get("Source"));
        delivery.setLabId(metadata.get("Lab Name/ID"));
        delivery.setName(metadata.get("Reagent Name"));
        delivery.setDescription(metadata.get("Reagent Description"));
        if (delivery.getId() == 0 && (delivery.getLabId() == null || delivery.getLabId().equals("")))
            return 0;
        else {
            if (delivery.getId() == 0) {
                long deliveryId = dao.getDeliveryId(delivery);
                if (deliveryId == 0) {
                    delivery.setTier(tier);
                    deliveryId = dao.insertDelivery(delivery);
                }
                return deliveryId;
            }
            return 0;
        }
    }

    private long loadAmphiphilicPeptide(HashMap<String, String> metadata, Delivery delivery) throws Exception {
        String scgeId = metadata.get("SCGE ID");
        if( !Utils.isStringEmpty(scgeId) ) {
            delivery.setId(new BigDecimal(scgeId).longValue());
        }
        delivery.setSource(metadata.get("Source"));
        delivery.setLabId(metadata.get("Lab Name/ID"));
        delivery.setName(metadata.get("Lab Name/ID"));
        delivery.setDescription(metadata.get("AP Description"));
        delivery.setSequence(metadata.get("AP Sequence"));

        if( delivery.getId()!=0 ) {
            return delivery.getId();
        }
        if( Utils.isStringEmpty(delivery.getLabId()) ) {
            return 0;
        }
        long deliveryId = dao.getDeliveryId(delivery);
        if (deliveryId == 0) {
            delivery.setTier(tier);
            deliveryId = dao.insertDelivery(delivery);
        }
        return deliveryId;
    }

    private long loadNanoparticle(HashMap<String, String> metadata, Delivery delivery) throws Exception {
        if (metadata.containsKey("SCGE ID") && metadata.get("SCGE ID") != null && !metadata.get("SCGE ID").isEmpty()) {
            delivery.setId(new BigDecimal(metadata.get("SCGE ID")).longValue());
            return delivery.getId();
        }
        delivery.setSource(metadata.get("Source"));
        delivery.setLabId(metadata.get("Lab Name/ID"));
        delivery.setName(metadata.get("Lab Name/ID"));
        delivery.setDescription(metadata.get("NP Description"));
        delivery.setSubtype(metadata.get("NP Type"));
        delivery.setType("Nanoparticle");
        if (delivery.getLabId() == null || delivery.getLabId().equals(""))
            return 0;
        else {
            long deliveryId = dao.getDeliveryId(delivery);
            if (deliveryId == 0) {
                delivery.setTier(tier);
                deliveryId = dao.insertDelivery(delivery);
                System.out.println("Inserted Nanoparticle: " + deliveryId);
            } else System.out.println("Got Nanoparticle: " + deliveryId);
            return deliveryId;
        }
    }

    private long loadAnimalModel(HashMap<String, String> metadata, Model model) throws Exception {
        if (metadata.containsKey("SCGE ID") && metadata.get("SCGE ID") != null && !metadata.get("SCGE ID").isEmpty()) {
            model.setModelId(new BigDecimal(metadata.get("SCGE ID")).longValue());
            System.out.println("Got model: " + model.getModelId());
            return model.getModelId();
        }
        model.setRrid(metadata.get("RRID link"));
        model.setParentalOrigin(metadata.get("Parental Origin"));

        String val = metadata.get("Strain Symbol"); // old field name
        if( val==null ) val = metadata.get("Official strain symbol");
        model.setName(val);

        model.setStrainAlias(metadata.get("Strain Aliases"));
        //(metadata.get("Strain Code"));
        model.setOrganism(metadata.get("Species"));
        model.setSource(metadata.get("Source"));
        model.setDescription(metadata.get("Strain Description"));
        model.setTransgene(metadata.get("Integrated Transgene"));
        model.setAnnotatedMap(metadata.get("Annotated Map"));
        model.setTransgeneDescription(metadata.get("Transgene Description"));
        model.setTransgeneReporter(metadata.get("Transgene Reporter"));
        model.setType("Animal");
        if (model.getName() == null || model.getName().equals("")) {
            return 0;
        } else {
                long modelId = dao.getModelId(model);
                if (modelId == 0) {
                    model.setTier(tier);
                    modelId = dao.insertModel(model);
                    System.out.println("Inserted model: " + modelId);
                } else System.out.println("Got model: " + modelId);
                return modelId;
        }
    }

    private long loadCellModel(HashMap<String, String> metadata, Model model) throws Exception {
        if (metadata.containsKey("SCGE ID") && metadata.get("SCGE ID") != null && !metadata.get("SCGE ID").isEmpty()) {
            model.setModelId(new BigDecimal(metadata.get("SCGE ID")).longValue());
            System.out.println("Got model: " + model.getModelId());
            return model.getModelId();
        }
        model.setRrid(metadata.get("RRID link"));
        model.setSource(metadata.get("Source"));
        model.setParentalOrigin(metadata.get("Parental Origin"));
        model.setName(metadata.get("CM Name"));
        model.setDescription(metadata.get("CM Description"));
        model.setOrganism(metadata.get("Species"));
        model.setSex(getSex(metadata.get("Sex")));
        model.setSubtype(metadata.get("Type"));
        model.setTransgene(metadata.get("Integrated Transgene"));
        model.setAnnotatedMap(metadata.get("Annotated Map"));
        model.setTransgeneDescription(metadata.get("Transgene Description"));
        model.setTransgeneReporter(metadata.get("Transgene Reporter"));
        model.setType("Cell");

        if (model.getName() == null || model.getName().equals("")) {
            return 0;
        } else {
                long modelId = dao.getModelId(model);
                if (modelId == 0) {
                    model.setTier(tier);
                    modelId = dao.insertModel(model);
                    System.out.println("Inserted model: " + modelId);
                } else System.out.println("Got model: " + modelId);
                return modelId;
            }
    }

    private int loadApplicationMethod(HashMap<String, String> metadata, ApplicationMethod method) throws Exception {

        method.setSiteOfApplication(metadata.get("Target Tissue"));
        method.setApplicationType(metadata.get("Delivery route"));
        if(metadata.containsKey("Delivery method")) {
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
        }
        return methodId;
    }
    private void loadAntibody(HashMap<String, String> metadata) throws Exception {
        Antibody antibody = new Antibody();
        if (metadata.containsKey("SCGE ID") && metadata.get("SCGE ID") != null && !metadata.get("SCGE ID").isEmpty()) {
            antibody.setAntibodyId(new BigDecimal(metadata.get("SCGE ID")).intValue());
            System.out.println("Got Antibody " +antibody.getAntibodyId());
            antibodies.add(antibody.getAntibodyId());
        } else {
            antibody.setRrid(metadata.get("RRID"));
            antibody.setOtherId(metadata.get("Other ID"));
            antibody.setDescription(metadata.get("Antibody description"));

            if ((antibody.getRrid() == null || antibody.getRrid().equals("")) && (antibody.getOtherId() == null || antibody.getOtherId().equals("")) )
                System.out.println("No antibody");
            else {
                int antibodyId = dao.getAntibodyId(antibody);
                if (antibodyId == 0) {
                    antibodyId = dao.insertAntibody(antibody);
                    System.out.println("Inserted Antibody " +antibodyId);
                } else System.out.println("Got Antibody " +antibodyId);
                antibodies.add(antibodyId);
            }
        }
    }
    private long loadHrDonor(HashMap<String, String> metadata, HRDonor hrdonor) throws Exception {
        if (metadata.containsKey("SCGE ID") && metadata.get("SCGE ID") != null && !metadata.get("SCGE ID").isEmpty()) {
            hrdonor.setId(new BigDecimal(metadata.get("SCGE ID")).longValue());
            System.out.println("Got HrDonor " +hrdonor.getId());
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
                    System.out.println("Inserted HrDonor " +hrdonorId);
                } else System.out.println("Got HrDonor " +hrdonorId);
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
        if (value != null && !value.isEmpty()) {
            if (value.equalsIgnoreCase("M")) {
                return "Male";
            } else if (value.equalsIgnoreCase("F")) {
                return "Female";
            } else if (value.equalsIgnoreCase("Male")) {
                return "Male";
            } else if (value.equalsIgnoreCase("Female")) {
                return "Female";
            }
        }
        return null;
    }

    // obsolete: use Mean class instead
    public void loadMean(long expId) throws Exception {
        List<ExperimentRecord> records = dao.getExpRecords(expId);
        int maxSamples = 0;
        for (ExperimentRecord record : records) {
            ExperimentResultDetail resultDetail = new ExperimentResultDetail();
            List<ExperimentResultDetail> experimentResults = dao.getExperimentalResults(record.getExperimentRecordId());
            //BigDecimal average = new BigDecimal(0);
            double average = 0;
            int noOfSamples = 0;
            for (ExperimentResultDetail result : experimentResults) {
                noOfSamples = result.getNumberOfSamples();
                if (maxSamples < noOfSamples)
                    maxSamples = noOfSamples;

                if (!result.getUnits().equalsIgnoreCase("signal")) {
                    if (result.getResult() != null && !result.getResult().equals("")) {
                        average += Double.valueOf(result.getResult());
                        //average = average.add(new BigDecimal(result.getResult()));
                    }
                }
                resultDetail = result;
            }
            //average = average.divide(new BigDecimal(noOfSamples),2, RoundingMode.HALF_UP);
            average = average / noOfSamples;
            average = Math.round(average * 100.0) / 100.0;
            resultDetail.setReplicate(0);
            resultDetail.setResult(String.valueOf(average));
            // System.out.println(resultDetail.getResultId() + "," + resultDetail.getResult());
            dao.insertExperimentResultDetail(resultDetail);

        }
        System.out.println("Max = " + maxSamples);
        for (ExperimentRecord record : records) {
            ExperimentResultDetail resultDetail = new ExperimentResultDetail();
            List<ExperimentResultDetail> experimentResults = dao.getExperimentalResults(record.getExperimentRecordId());
            for (ExperimentResultDetail result : experimentResults) {
                if (resultDetail.getReplicate() == 0) {
                    if (maxSamples > result.getNumberOfSamples()) {
                        //System.out.println(maxSamples - result.getNumberOfSamples());
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

    // obsolete: use Mean class instead
    public void loadQualitativeMean(long expId) throws Exception {

        int insertedRows = 0;
        List<ExperimentRecord> records = dao.getExpRecords(expId);
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

        System.out.println("inserted rows with replicate 0: "+insertedRows);
    }

    public LoadDAO getDao() {
        return dao;
    }

    public void setDao(LoadDAO dao) {
        this.dao = dao;
    }
}


