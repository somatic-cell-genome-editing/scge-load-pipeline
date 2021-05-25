package edu.mcw.scge;



import edu.mcw.scge.datamodel.*;
import edu.mcw.scge.datamodel.Vector;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.xml.XmlBeanDefinitionReader;
import org.springframework.core.io.FileSystemResource;
import java.io.File;
import java.io.FileInputStream;
import java.util.*;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
public class Manager {


    Logger log = Logger.getLogger("core");

    Logger logSummary = Logger.getLogger("status");

    LoadDAO dao = new LoadDAO();

    int experimentId = 11;
    int studyId = 1015;
    String fileName = "E:\\Data Submission.v3.2_Asokan Submission 1_final.xlsx";
    String expType="In Vivo";
    int tier = 4;

    List<Integer> vectors = new ArrayList<>();
    List<Integer> guides = new ArrayList<>();

    public static void main(String[] args) throws Exception {

        DefaultListableBeanFactory bf = new DefaultListableBeanFactory();
        new XmlBeanDefinitionReader(bf).loadBeanDefinitions(new FileSystemResource("properties/AppConfigure.xml"));
        Manager manager = (Manager) (bf.getBean("manager"));

        for( int i=0; i<args.length; i++ ) {
            String arg = args[i];
            switch (arg) {
                case "--expId":
                    manager.experimentId=Integer.parseInt(args[++i]);
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
        int column = 3; //column in the excel sheet
        String name = "Condition 1"; //exp record name to be loaded
         manager.loadMetaData(column,name);

      /* for(int i = 11;i <= 14;i++) {
            manager.loadMetaData(i, "Condition "+(i-2));
        }
      */

       manager.loadMean(manager.experimentId);
    }

    public void loadMetaData(int column, String name) throws Exception {

//obtaining input bytes from a file
        FileInputStream fis = new FileInputStream(new File(fileName));
//creating workbook instance that refers to .xls file
        XSSFWorkbook wb = new XSSFWorkbook(fis);
        XSSFSheet sheet = wb.getSheet(expType);      //creating a Sheet object to retrieve object
        HashMap<String, Integer> rowMap = new HashMap<>();
        boolean guideData = false;
        boolean editorData = false;
        boolean meData = false;
        boolean npData = false;
        boolean apData = false;
        boolean vvData = false;
        boolean pgData = false;
        boolean vlpData = false;
        boolean cellData = false;
        boolean animalData = false;
        boolean crData = false;
        boolean traData = false;
        Guide guide = new Guide();
        Editor editor = new Editor();
        Delivery delivery = new Delivery();
        Model model = new Model();
        Vector vector = new Vector();
        ApplicationMethod method = new ApplicationMethod();
        OffTarget offTarget = new OffTarget();
        List<OffTarget> offTargets = new ArrayList<>();

        ExperimentRecord experiment = new ExperimentRecord();
        experiment.setExperimentName(name);
        experiment.setStudyId(studyId);
        experiment.setExperimentId(experimentId);

        for (Row row : sheet) {

            Cell cell0 = row.getCell(0);
            Cell cell1 = row.getCell(1);
            Cell cell = row.getCell(column);
            String data;

            if(row.getRowNum() < 3 || cell1 == null)
                continue;

            if(cell == null)
                data = null;
            else if (cell.getCellType() == Cell.CELL_TYPE_STRING || cell.getCellType() == Cell.CELL_TYPE_BLANK)
                data = cell.getStringCellValue();
            else data = String.valueOf(cell.getNumericCellValue());

            //Read and insert guide
           if (guideData ||
                   (cell0 != null && cell0.getStringCellValue().equalsIgnoreCase("Guide RNA (gRNA)"))) {
                //guide starts
                guideData = true;


                   /* if (cell1.getStringCellValue().equalsIgnoreCase("SCGE ID")) {
                        if(!data.equals("") && data != null)
                            guide.setGuide_id(Integer.valueOf(data));
                    } else */ if(cell1.getStringCellValue().equalsIgnoreCase("Guide Description")) {
                        guide.setGuideDescription(data);
                    } else if (cell1.getStringCellValue().equalsIgnoreCase("Source")) {
                        guide.setSource(data);
                    } else if (cell1.getStringCellValue().equalsIgnoreCase("Species")) {
                        guide.setSpecies(data);
                    } else if (cell1.getStringCellValue().equalsIgnoreCase("Lab gRNA Name/ID")) {
                        guide.setGrnaLabId(data);
                        guide.setGuide(data);
                    } else if (cell1.getStringCellValue().equalsIgnoreCase("Target Locus")) {
                        guide.setTargetLocus(data);
                    } else if (cell1.getStringCellValue().equalsIgnoreCase("Target Sequence")) {
                        if(data != null)
                            guide.setTargetSequence(data.toUpperCase());
                    } else if (cell1.getStringCellValue().equalsIgnoreCase("Target Sequence+PAM")) {
                        guide.setPam(data.toUpperCase());
                        continue;
                    } else if (cell1.getStringCellValue().equalsIgnoreCase("Genome Version")) {
                        guide.setAssembly(data);
                        continue;
                    } else if (cell1.getStringCellValue().equalsIgnoreCase("Chromosome")) {
                        guide.setChr(data);
                    } else if (cell1.getStringCellValue().equalsIgnoreCase("Chromosome Start")) {
                        guide.setStart(data);
                    } else if (cell1.getStringCellValue().equalsIgnoreCase("Chromosome End")) {
                        guide.setStop(data);
                    } else if (cell1.getStringCellValue().equalsIgnoreCase("Chromosome Strand")) {
                        if(data != null && !data.equals("")) {
                            if(data.equalsIgnoreCase("plus"))
                                guide.setStrand("+");
                            else if(data.equalsIgnoreCase("minus"))
                                guide.setStrand("-");
                            else guide.setStrand(data);
                        }else guide.setStrand(data);
                    } else if (cell1.getStringCellValue().equalsIgnoreCase("Spacer Sequence")) {
                        if(data != null)
                            guide.setSpacerSequence(data.toUpperCase());
                    } else if (cell1.getStringCellValue().equalsIgnoreCase("Spacer Length")) {
                        guide.setSpacerLength(data);
                    } else if (cell1.getStringCellValue().equalsIgnoreCase("Guide Format")) {
                        guide.setGuideFormat(data);
                    } else if (cell1.getStringCellValue().equalsIgnoreCase("Off-target mutation detection method 1")) {
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
                    }else if (cell1.getStringCellValue().equalsIgnoreCase("Specificity Ratio")) {
                        guide.setSpecificityRatio(Double.valueOf(data).toString());
                    }else if (cell1.getStringCellValue().equalsIgnoreCase("Standard guide scaffold sequence?")) {
                        guide.setStandardScaffoldSequence(data);
                    } else if (cell1.getStringCellValue().equalsIgnoreCase("IVT construct Source")) {
                        guide.setIvtConstructSource(data);
                    } else if (cell1.getStringCellValue().equalsIgnoreCase("Catalog#")) {
                        guide.setVectorId(data);
                    } else if (cell1.getStringCellValue().equalsIgnoreCase("Lab Name/ID")) {
                        guide.setVectorName(data);
                    } else if (cell1.getStringCellValue().equalsIgnoreCase("Vector Description")) {
                        guide.setVectorDescription(data);
                    } else if (cell1.getStringCellValue().equalsIgnoreCase("Vector Type")) {
                        guide.setVectorType(data);
                    } else if (cell1.getStringCellValue().equalsIgnoreCase("Annotated Map")) {
                        guide.setAnnotatedMap(data);
                    } else if (cell1.getStringCellValue().equalsIgnoreCase("Modifications")) {
                        guide.setModifications(data);
                    } else if (cell1.getStringCellValue().equalsIgnoreCase("Forward Primer")) {
                        guide.setForwardPrimer(data);
                    } else if (cell1.getStringCellValue().equalsIgnoreCase("Reverse Primer")) {
                        guide.setReversePrimer(data);
                    } else if (cell1.getStringCellValue().equalsIgnoreCase("Repeat Sequence")) {
                        if(data != null)
                            guide.setRepeatSequence(data.toUpperCase());
                    } else if (cell1.getStringCellValue().equalsIgnoreCase("Linker Sequence")) {
                        if(data != null)
                            guide.setLinkerSequence(data.toUpperCase());
                    } else if (cell1.getStringCellValue().equalsIgnoreCase("Anti Repeat Sequence")) {
                        if(data != null)
                            guide.setAntiRepeatSequence(data.toUpperCase());
                    } else if (cell1.getStringCellValue().equalsIgnoreCase("StemLoop 1 Sequence")) {
                        guide.setStemloop1Sequence(data);
                    } else if (cell1.getStringCellValue().equalsIgnoreCase("StemLoop 2 Sequence")) {
                        guide.setStemloop2Sequence(data);
                    } else if (cell1.getStringCellValue().equalsIgnoreCase("StemLoop 3 Sequence")) {
                        guide.setStemloop3Sequence(data);
                        guideData = false;
                        if((guide.getTargetSequence() == null || guide.getTargetSequence().equals("")) &&
                                (guide.getPam() == null || guide.getPam().equals("")))
                            guide = new Guide();
                        else {
                            int guideId = dao.getGuideId(guide);
                            if (guideId == 0) {
                                guide.setTier(tier);
                                guideId = dao.insertGuide(guide);
                                System.out.println("Inserted guide: " +guideId);
                            }else System.out.println("Got guide: " +guideId);
                            guides.add(guideId);
                            //experiment.setGuideId(guideId);
                        }
                    }
                }

            //Read and insert Editor
            if (editorData ||
                    (cell0!= null && cell0.getStringCellValue().equalsIgnoreCase("Editor Protein"))) {
                //editor starts
                editorData = true;

                if (cell1.getStringCellValue().equalsIgnoreCase("Source")) {
                    editor.setSource(data);
                } else if (cell1.getStringCellValue().equalsIgnoreCase("SCGE ID")) {
                    if(data != null  && !data.equals(""))
                        editor.setId(Integer.valueOf(data));
                } else if (cell1.getStringCellValue().equalsIgnoreCase("Catalog#")) {
                    editor.setSymbol(data);
                } else if (cell1.getStringCellValue().equalsIgnoreCase("Editor Species")) {
                    editor.setSpecies(data);
                } else if (cell1.getStringCellValue().equalsIgnoreCase("Editor Type")) {
                    editor.setType(data);
                } else if (cell1.getStringCellValue().equalsIgnoreCase("Editor Subtype")) {
                    editor.setSubType(data);
                }else if (cell1.getStringCellValue().equalsIgnoreCase("Editor Name")) {
                    editor.setSymbol(data);
                }else if (cell1.getStringCellValue().equalsIgnoreCase("Editor Description")) {
                    editor.setEditorDescription(data);
                }else if (cell1.getStringCellValue().equalsIgnoreCase("Annotated Map")) {
                    editor.setAnnotatedMap(data);
                }else if (cell1.getStringCellValue().equalsIgnoreCase("Editor Alias")) {
                    editor.setAlias(data);
                } else if (cell1.getStringCellValue().equalsIgnoreCase("Protein Sequence")) {
                    editor.setProteinSequence(data);
                } else if (cell1.getStringCellValue().equalsIgnoreCase("Editor Substrate")) {
                    editor.setSubstrateTarget(data);
                } else if (cell1.getStringCellValue().equalsIgnoreCase("PAM Preference")) {
                    editor.setPamPreference(data);
                } else if (cell1.getStringCellValue().equalsIgnoreCase("Editor Protein Variant")) {
                    editor.setEditorVariant(data);
                } else if (cell1.getStringCellValue().equalsIgnoreCase("Editor Fusion/Effector")) {
                    editor.setFusion(data);
                } else if (cell1.getStringCellValue().equalsIgnoreCase("Editor Activity")) {
                    editor.setActivity(data);
                } else if (cell1.getStringCellValue().equalsIgnoreCase("Editor Cleavage Type")) {
                    editor.setDsbCleavageType(data);
                } else if (cell1.getStringCellValue().equalsIgnoreCase("Target Sequence")) {
                    editor.setTarget_sequence(data);
                    editorData = false;
                    if((editor.getType() == null || editor.getType().equals("")) && (editor.getActivity() == null || editor.getActivity().equals(""))
                            && (editor.getSymbol() == null || editor.getSymbol().equals("")))
                        editor = new Editor();
                    else {
                        int editorId = dao.getEditorId(editor);
                        if (editorId == 0) {
                            editor.setTier(tier);
                            editorId = dao.insertEditor(editor);
                            System.out.println("Inserted editor: " + editorId);
                        } else System.out.println("Got editor: " + editorId);
                        experiment.setEditorId(editorId);
                    }
                }
            }

                if (vvData ||
                        (cell0!= null && cell0.getStringCellValue().equalsIgnoreCase("Vector/Format"))) {
                    //editor starts
                    vvData = true;

                    if (cell1.getStringCellValue().equalsIgnoreCase("RRID")) {
                        vector = new Vector();
                    } else if (cell1.getStringCellValue().equalsIgnoreCase("Source")) {
                        vector.setSource(data);
                    } else if (cell1.getStringCellValue().equalsIgnoreCase("Lab Name/ID")) {
                        vector.setLabId(data);
                        vector.setName(data);
                    } else if (cell1.getStringCellValue().equalsIgnoreCase("Vector Type")) {
                        vector.setSubtype(data);
                    } else if (cell1.getStringCellValue().equalsIgnoreCase("VV Genome Serotype")) {
                        vector.setGenomeSerotype(data);
                    } else if (cell1.getStringCellValue().equalsIgnoreCase("VV Capsid Serotype")) {
                        vector.setCapsidSerotype(data);
                    } else if (cell1.getStringCellValue().equalsIgnoreCase("VV Capsid Variant")) {
                        vector.setCapsidVariant(data);
                    } else if (cell1.getStringCellValue().equalsIgnoreCase("Annotated Map")) {
                        vector.setAnnotatedMap(data);
                    } else if (cell1.getStringCellValue().equalsIgnoreCase("Vector Description")) {
                        vector.setDescription(data);
                    } else if (cell1.getStringCellValue().equalsIgnoreCase("Titer Method")) {
                        vector.setTiterMethod(data);
                        if((vector.getSource()== null || vector.getSource().equals("")) && (vector.getLabId() == null || vector.getLabId().equals("")))
                            vector = new Vector();
                        else {
                            vector.setType("Viral Vector");
                            int vectorId = dao.getVectorId(vector);
                            if(vectorId == 0) {
                                vector.setTier(tier);
                                vectorId = dao.insertVector(vector);
                                System.out.println("Inserted vector: " +vectorId);
                            }else System.out.println("Got vector: " +vectorId);
                           // experiment.setVectorId(vectorId);
                            vectors.add(20);
                        }
                        vvData = false;
                    }

                }

                //Read and insert Delivery
                if (pgData ||
                        (cell0!= null && cell0.getStringCellValue().equalsIgnoreCase("Protein Conjugate") )) {

                    pgData = true;
                    if (cell1.getStringCellValue().equalsIgnoreCase("Source")) {
                        delivery = new Delivery();
                        delivery.setSource(data);
                    } else if (cell1.getStringCellValue().equalsIgnoreCase("Lab Name/ID")) {
                        delivery.setLabId(data);
                    } else if (cell1.getStringCellValue().equalsIgnoreCase("PCJ Description")) {
                        delivery.setDescription(data);
                        if((delivery.getSource() == null || delivery.getSource().equals("")) && (delivery.getLabId() == null || delivery.getLabId().equals("")))
                            delivery = new Delivery();
                        else {
                            delivery.setType("Protein Conjugate");
                            int deliveryId = dao.getDeliveryId(delivery);
                            if(deliveryId == 0) {
                                delivery.setTier(tier);
                                deliveryId = dao.insertDelivery(delivery);
                            }
                            experiment.setDeliverySystemId(deliveryId);
                        }
                        pgData = false;
                    }

                }else if (vlpData ||
                        (cell0!= null && cell0.getStringCellValue().equalsIgnoreCase("Virus Like Particle") )) {

                    vlpData = true;
                    if (cell1.getStringCellValue().equalsIgnoreCase("Source")) {
                        delivery = new Delivery();
                        delivery.setSource(data);
                    } else if (cell1.getStringCellValue().equalsIgnoreCase("Lab Name/ID")) {
                        delivery.setLabId(data);
                    } else if (cell1.getStringCellValue().equalsIgnoreCase("VLP Description")) {
                        delivery.setDescription(data);
                        if((delivery.getSource() == null || delivery.getSource().equals("")) && (delivery.getLabId() == null || delivery.getLabId().equals("")))
                            delivery = new Delivery();
                        else {
                            delivery.setType("Virus Like Particle");
                            int deliveryId = dao.getDeliveryId(delivery);
                            if(deliveryId == 0) {
                                delivery.setTier(tier);
                                deliveryId = dao.insertDelivery(delivery);
                            }
                            experiment.setDeliverySystemId(deliveryId);
                        }
                        vlpData = false;
                    }

                }else if (crData ||
                        (cell0!= null && cell0.getStringCellValue().equalsIgnoreCase("Commercial Reagent") )) {

                    crData = true;
                    cell = row.getCell(column);
                    if (cell.getCellType() == Cell.CELL_TYPE_STRING || cell.getCellType() == Cell.CELL_TYPE_BLANK)
                        data = cell.getStringCellValue();
                    else data = String.valueOf(cell.getNumericCellValue());

                    if (cell1.getStringCellValue().equalsIgnoreCase("Source")) {
                        delivery = new Delivery();
                        delivery.setSource(data);
                    } else if (cell1.getStringCellValue().equalsIgnoreCase("Lab Name/ID") || cell1.getStringCellValue().equalsIgnoreCase("Catalog#") ) {
                        delivery.setLabId(data);
                    } else if (cell1.getStringCellValue().equalsIgnoreCase("Reagent Name")) {
                        delivery.setName(data);
                    }else if (cell1.getStringCellValue().equalsIgnoreCase("Reagent Description")) {
                        delivery.setDescription(data);
                        if((delivery.getSource() == null || delivery.getSource().equals("")) && (delivery.getLabId() == null || delivery.getLabId().equals("")))
                            delivery = new Delivery();
                        else {
                            delivery.setType("Commercial Reagent");
                            int deliveryId = dao.getDeliveryId(delivery);
                            if(deliveryId == 0) {
                                delivery.setTier(tier);
                                deliveryId = dao.insertDelivery(delivery);
                                System.out.println("Inserted delivery: " +deliveryId);
                            }else System.out.println("Got delivery: " +deliveryId);
                            experiment.setDeliverySystemId(deliveryId);
                        }
                        crData = false;
                    }

                }else if (apData ||
                        (cell0!= null && cell0.getStringCellValue().equalsIgnoreCase("Amphiphilic peptide"))) {

                    apData = true;
                    if (cell1.getStringCellValue().equalsIgnoreCase("Source")) {
                        delivery = new Delivery();
                        delivery.setSource(data);
                    } else if (cell1.getStringCellValue().equalsIgnoreCase("Lab Name/ID")) {
                        delivery.setLabId(data);
                        delivery.setName(data);
                    } else if (cell1.getStringCellValue().equalsIgnoreCase("AP Description")) {
                        delivery.setDescription(data);
                        if((delivery.getSource() == null || delivery.getSource().equals("")) && (delivery.getLabId() == null || delivery.getLabId().equals("")))
                            delivery = new Delivery();
                        else {
                            delivery.setType("Amphiphilic peptide");
                            int deliveryId = dao.getDeliveryId(delivery);
                            if(deliveryId == 0) {
                                delivery.setTier(tier);
                                deliveryId = dao.insertDelivery(delivery);
                            System.out.println("Inserted Amphiphilic peptide: " +deliveryId);
                        }else System.out.println("Got Amphiphilic peptide: " +deliveryId);
                            experiment.setDeliverySystemId(deliveryId);
                        }
                        apData = false;
                    }

                }else if (npData ||
                        (cell0!= null && cell0.getStringCellValue().equalsIgnoreCase("Nanoparticle"))) {

                    npData = true;
                    if (cell1.getStringCellValue().equalsIgnoreCase("Source")) {
                        delivery = new Delivery();
                        delivery.setSource(data);
                    } else if (cell1.getStringCellValue().equalsIgnoreCase("Lab Name/ID")) {
                        delivery.setLabId(data);
                    } else if (cell1.getStringCellValue().equalsIgnoreCase("NP Type")) {
                        delivery.setSubtype(data);
                        delivery.setName(data);
                    } else if (cell1.getStringCellValue().equalsIgnoreCase("NP Description")) {
                        delivery.setDescription(data);
                    }else if (cell1.getStringCellValue().equalsIgnoreCase("NP size")) {
                        delivery.setNpSize(data);
                        if(( delivery.getLabId() == null || delivery.getLabId().equals("")) &&
                                (delivery.getNpSize()== null || delivery.getNpSize().equals("")))
                            delivery = new Delivery();
                        else {
                            delivery.setType("Nanoparticle");
                            int deliveryId = dao.getDeliveryId(delivery);
                            if(deliveryId == 0) {
                                delivery.setTier(tier);
                                deliveryId = dao.insertDelivery(delivery);
                            }
                            experiment.setDeliverySystemId(deliveryId);
                        }
                        npData = false;
                    }

                }


            if (cellData ||
                    (cell0!= null && cell0.getStringCellValue().equalsIgnoreCase("Cell model"))) {

                cellData = true;
                if (cell1.getStringCellValue().equalsIgnoreCase("RRID")) {
                    model.setRrid(data);
                } else if (cell1.getStringCellValue().equalsIgnoreCase("SCGE ID")) {
                    if(!data.equals("") && data != null)
                        model.setModelId(Integer.valueOf(data));
                    else model.setModelId(0);
                } else if (cell1.getStringCellValue().equalsIgnoreCase("Source")) {
                    model.setSource(data);
                } else if (cell1.getStringCellValue().equalsIgnoreCase("Parental Origin")) {
                    model.setParentalOrigin(data);
                } else if (cell1.getStringCellValue().equalsIgnoreCase("CM Name")) {
                    model.setName(data);
                } else if (cell1.getStringCellValue().equalsIgnoreCase("CM Description")) {
                    model.setDescription(data);
                } else if (cell1.getStringCellValue().equalsIgnoreCase("Species")) {
                    model.setOrganism(data);
                } else if (cell1.getStringCellValue().equalsIgnoreCase("Sex")) {
                    model.setSex(data);
                    experiment.setSex(data);
                } else if (cell1.getStringCellValue().equalsIgnoreCase("Type")) {
                    model.setSubtype(data);
                } else if (cell1.getStringCellValue().equalsIgnoreCase("Integrated Transgene")) {
                    model.setTransgene(data);
                } else if (cell1.getStringCellValue().equalsIgnoreCase("Annotated Map")) {
                    model.setAnnotatedMap(data);
                } else if (cell1.getStringCellValue().equalsIgnoreCase("Transgene Description")) {
                    model.setTransgeneDescription(data);
                } else if (cell1.getStringCellValue().equalsIgnoreCase("Transgene Reporter")) {
                    model.setTransgeneReporter(data);
                    if (model.getModelId() != 0 && ((model.getName() == null || model.getName().equals("")) && (model.getDescription() == null) )) {
                        model = new Model();
                    } else {
                        model.setType("cell");
                        int modelId = dao.getModelId(model);
                        if(model.getModelId() != 0)
                            modelId = model.getModelId();
                        if(modelId == 0) {
                            model.setTier(tier);
                            modelId = dao.insertModel(model);
                            System.out.println("Inserted model: " +modelId);
                        }else System.out.println("Got model: " +modelId);
                        experiment.setModelId(modelId);
                    }
                    cellData = false;
                }
            }

            if (traData ||
                    (cell0!= null && cell0.getStringCellValue().equalsIgnoreCase("Transient Reporter Assay"))) {
                traData = true;
                if (cell1.getStringCellValue().equalsIgnoreCase("RRID")) {
                    model.setRrid(data);
                } else if (cell1.getStringCellValue().equalsIgnoreCase("TRA Name")) {
                    model.setName(data);
                } else if (cell1.getStringCellValue().equalsIgnoreCase("TRA Description")) {
                    model.setDescription(data);
                } else if (cell1.getStringCellValue().equalsIgnoreCase("Reporter Type")) {
                    model.setSubtype(data);
                }else if (cell1.getStringCellValue().equalsIgnoreCase("Transgene")) {
                    model.setTransgene(data);
                } else if (cell1.getStringCellValue().equalsIgnoreCase("Annotated Map")) {
                    model.setAnnotatedMap(data);
                } else if (cell1.getStringCellValue().equalsIgnoreCase("Transgene Reporter")) {
                    model.setTransgeneReporter(data);
                    if (model.getName() == null || model.getName().equals("")) {
                        model = new Model();
                    } else {
                        model.setType("Transient Reporter Assay");
                        int modelId = dao.getModelId(model);
                        if(modelId == 0) {
                            modelId = dao.insertModel(model);
                            System.out.println("Inserted model: " +modelId);
                        }else System.out.println("Got model: " +modelId);
                        //experiment.setModelId(modelId);

                    }
                    traData = false;
                }

            }
            if (animalData ||
                    (cell0!= null && cell0.getStringCellValue().equalsIgnoreCase("Animal Model (AM)"))) {
                animalData = true;

                if (cell1.getStringCellValue().equalsIgnoreCase("RRID")) {
                    model.setRrid(data);
                } else if (cell1.getStringCellValue().equalsIgnoreCase("Parental Origin")) {
                    model.setParentalOrigin(data);
                } else if (cell1.getStringCellValue().equalsIgnoreCase("Strain Symbol")) {
                    model.setName(data);
                } else if (cell1.getStringCellValue().equalsIgnoreCase("Strain Aliases")) {
                    model.setStrainAlias(data);
                } else if (cell1.getStringCellValue().equalsIgnoreCase("Strain Code")) {
                    model.setStrainCode(data);
                } else if (cell1.getStringCellValue().equalsIgnoreCase("Species")) {
                    model.setOrganism(data);
                } else if (cell1.getStringCellValue().equalsIgnoreCase("Strain Description")) {
                    model.setDescription(data);
                }else if (cell1.getStringCellValue().equalsIgnoreCase("Integrated Transgene")) {
                    model.setTransgene(data);
                } else if (cell1.getStringCellValue().equalsIgnoreCase("Annotated Map")) {
                    model.setAnnotatedMap(data);
                } else if (cell1.getStringCellValue().equalsIgnoreCase("Transgene Description")) {
                    model.setTransgeneDescription(data);
                } else if (cell1.getStringCellValue().equalsIgnoreCase("Transgene Reporter")) {
                    model.setTransgeneReporter(data);
                    if (model.getName() == null || model.getName().equals("")) {
                        model = new Model();
                    } else {
                        model.setType("animal");
                        int modelId = dao.getModelId(model);
                        if(modelId == 0) {
                            model.setTier(tier);
                            modelId = dao.insertModel(model);
                            System.out.println("Inserted model: " +modelId);
                        }else System.out.println("Got model: " +modelId);
                        experiment.setModelId(modelId);

                    }
                    animalData = false;
                }

            }

                if ( experiment.getSex() == null && cell1.getStringCellValue().equalsIgnoreCase("Sex")) {
                    experiment.setSex(data);
                }
                if (cell1.getStringCellValue().equalsIgnoreCase("Age") || cell1.getStringCellValue().equalsIgnoreCase("Passage")) {
                    experiment.setAge(data);
                }
                if (cell1.getStringCellValue().equalsIgnoreCase("Zygosity")) {
                    experiment.setGenotype(data);
                }
                if (cell1.getStringCellValue().equalsIgnoreCase("Target Tissue")) {
                    method.setSiteOfApplication(data);
                }
                if (cell1.getStringCellValue().equalsIgnoreCase("Delivery route") || cell1.getStringCellValue().equalsIgnoreCase("Delivery method")) {
                    method.setApplicationType(data);
                }
                if (cell1.getStringCellValue().equalsIgnoreCase("Time post delivery sample collected")) {
                    method.setDaysPostInjection(data);
                }
                if (cell1.getStringCellValue().equalsIgnoreCase("Dosage (incl units)")) {
                    method.setDosage(data);
                }
                if (cell1.getStringCellValue().equalsIgnoreCase("Injection rate")) {
                    method.setInjectionRate(data);
                }
                if (cell1.getStringCellValue().equalsIgnoreCase("Injection frequency")) {
                    method.setInjectionFrequency(data);
                }
                if (cell1.getStringCellValue().equalsIgnoreCase("Injection volume")) {
                    method.setInjectionVolume(data);
                }
                if (cell1.getStringCellValue().equalsIgnoreCase("Format of editor/cargo")) {
                    method.setEditorFormat(data);
                    if((method.getDosage() == null || method.getDosage().equals("")) &&
                            (method.getDaysPostInjection() == null || method.getDaysPostInjection().equals("")))
                        method = new ApplicationMethod();
                    else {
                        int methodId = dao.getMethodId(method);
                        if (methodId == 0) {
                            methodId = dao.insertMethod(method);
                            System.out.println("Inserted method: " + methodId);
                        } else System.out.println("Got method: " + methodId);

                        experiment.setApplicationMethodId(methodId);
                    }
                }


        }
        if(expType.contains("In Vitro")) {
            int experimentRecId = dao.insertExperimentRecord(experiment);
            System.out.println(experimentRecId);
            for(int guideId:guides) {
                dao.insertGuideAssoc(experimentRecId, guideId);
                for(OffTarget o:offTargets) {
                    o.setGuideId(guideId);
                    dao.insertOffTarget(o);
                }
            }
            for(int vectorId:vectors)
                dao.insertVectorAssoc(experimentRecId,vectorId);


            loadData(experimentRecId,column);
            vectors.clear();
            guides.clear();
        }
        else loadData(experiment,column);
    }

    public void loadStudy() throws Exception{
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
            } else if(cell1.getStringCellValue().equalsIgnoreCase("POC_email")){
                Person p = dao.getPersonByEmail(data);
                s.setSubmitterId(p.getId());
            }

        }
        dao.insertStudy(s);
    }
    public void loadData(int expRecId,int column) throws Exception{
        FileInputStream fis = new FileInputStream(new File(fileName));
//creating workbook instance that refers to .xls file
        XSSFWorkbook wb = new XSSFWorkbook(fis);
        XSSFSheet sheet = wb.getSheet(expType);     //creating a Sheet object to retrieve object

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
            else data = String.valueOf(cell.getNumericCellValue());

            if(cell1.getStringCellValue().equalsIgnoreCase("Assay Description") || cell1.getStringCellValue().equalsIgnoreCase("Assay_Description")) {
                result.setAssayDescription(data);
            }
            if(cell1.getStringCellValue().equalsIgnoreCase("Edit Type")) {
                result.setEditType(data);
            }
            if(cell1.getStringCellValue().equalsIgnoreCase("Biological/Transfection/Delivery Replicates") ||
                    cell1.getStringCellValue().equalsIgnoreCase("Biological Replicates")) {

                if(data != null && !data.equals("")) {
                    result.setNumberOfSamples(Double.valueOf(data).intValue());
                }
            }
            if(cell1.getStringCellValue().equalsIgnoreCase("Units")) {
                result.setUnits(data);
            }
            if(cell1.getStringCellValue().equalsIgnoreCase("Editing Efficiency") ||
                    cell1.getStringCellValue().equalsIgnoreCase("Delivery Efficiency")  ) {

                if(data != null && !data.equals("")){
                    if(cell1.getStringCellValue().equalsIgnoreCase("Editing Efficiency"))
                        result.setResultType("Editing Efficiency");
                    else result.setResultType("Delivery Efficiency");
                    int resultId = dao.insertExperimentResult(result);
                    String valueString = data;
                    String[] values = valueString.split(",");
                    for(int i=0; i<values.length;i++){
                        ExperimentResultDetail detail = new ExperimentResultDetail();
                        detail.setResultId(resultId);
                        detail.setReplicate(i+1);
                        detail.setResult(values[i].trim().toLowerCase());
                        dao.insertExperimentResultDetail(detail);
                    }
                }
            }

        }
    }
    public void loadData(ExperimentRecord expRec, int column) throws Exception{
        FileInputStream fis = new FileInputStream(new File(fileName));
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
            String data;

            if (row.getRowNum() < 3 || cell1 == null)
                continue;

            if (cell == null)
                data = null;
            else if (cell.getCellType() == Cell.CELL_TYPE_STRING || cell.getCellType() == Cell.CELL_TYPE_BLANK)
                data = cell.getStringCellValue();
            else data = String.valueOf(cell.getNumericCellValue());

            if( cell0!= null && cell0.getStringCellValue().equalsIgnoreCase("Editing Efficiency"))
                editingData = true;

            if( cell0!= null && cell0.getStringCellValue().equalsIgnoreCase("Delivery Efficiency"))
                deliveryData = true;

            if(cell1.getStringCellValue().equalsIgnoreCase("Data_File_Name")) {
                editingData = false;
                deliveryData = false;

            }
            if(cell1.getStringCellValue().equalsIgnoreCase("Assay Description") || cell1.getStringCellValue().equalsIgnoreCase("Assay_Description")) {
                result.setAssayDescription(data);
            }else if(cell1.getStringCellValue().equalsIgnoreCase("Biological/Transfection/Delivery Replicates") ||
                    cell1.getStringCellValue().equalsIgnoreCase("Biological Replicates")) {

                if(data != null && !data.equals("")) {
                    result.setNumberOfSamples(Double.valueOf(data).intValue());
                }
            }else if(cell1.getStringCellValue().equalsIgnoreCase("Units")) {
                result.setUnits(data);
            }else if(editingData || deliveryData ) {

                if(editingData)
                    result.setResultType("Editing Efficiency");
                else result.setResultType("Delivery Efficiency");

                if(data != null && !data.equals("") && !data.equalsIgnoreCase("ND")){
                    System.out.println(cell1);
                    String tissue = cell1.getStringCellValue();
                    String cellType = cell2.getStringCellValue();
                    expRec.setTissueId(tissue);
                    expRec.setCellType(cellType);
                    int expRecId = dao.insertExperimentRecord(expRec);
                    for(int guideId:guides)
                        dao.insertGuideAssoc(expRecId, guideId);

                    for(int vectorId:vectors)
                        dao.insertVectorAssoc(expRecId,vectorId);

                    result.setExperimentRecordId(expRecId);
                    int resultId = dao.insertExperimentResult(result);
                    String valueString = data;
                    String[] values = valueString.split(",");
                    for(int i=0; i<values.length;i++){
                        ExperimentResultDetail detail = new ExperimentResultDetail();
                        detail.setResultId(resultId);
                        detail.setReplicate(i+1);
                        detail.setResult(values[i].trim());
                        dao.insertExperimentResultDetail(detail);
                        //System.out.println(values[i]);
                    }
                }


            }
        }
    }

    public void loadMean(int expId) throws Exception{
        List<ExperimentRecord> records = dao.getExpRecords(expId);
        for (ExperimentRecord record : records) {
            ExperimentResultDetail resultDetail = new ExperimentResultDetail();
            List<ExperimentResultDetail> experimentResults = dao.getExperimentalResults(record.getExperimentRecordId());
            double average = 0;
            int noOfSamples = 0;
            for (ExperimentResultDetail result : experimentResults) {
                noOfSamples = result.getNumberOfSamples();
                if(result.getResult() != null && !result.getResult().equals(""))
                    average += Double.valueOf(result.getResult());
                resultDetail = result;
            }
            average = average / noOfSamples;
            average = Math.round(average * 100.0) / 100.0;
            resultDetail.setReplicate(0);
            resultDetail.setResult(String.valueOf(average));
            System.out.println(resultDetail.getResultId() + "," + resultDetail.getResult());
            dao.insertExperimentResultDetail(resultDetail);

        }
    }
}


