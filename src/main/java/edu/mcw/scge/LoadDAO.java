package edu.mcw.scge;

import edu.mcw.scge.dao.spring.IntListQuery;
import edu.mcw.scge.dao.spring.StringListQuery;
import edu.mcw.rgd.process.Utils;
import edu.mcw.scge.dao.*;

import edu.mcw.scge.dao.implementation.*;
import edu.mcw.scge.datamodel.*;

import java.util.List;
import java.util.Map;


/**
 * @author hsnalabolu
 * wrapper to handle all DAO code
 */
public class LoadDAO extends AbstractDAO {


    GuideDao guideDao = new GuideDao();
    EditorDao editorDao = new EditorDao();
    DeliveryDao deliveryDao = new DeliveryDao();
    ModelDao modelDao = new ModelDao();
    ExperimentRecordDao expRecordDao = new ExperimentRecordDao();
    ExperimentDao expDao = new ExperimentDao();
    PersonDao personDao = new PersonDao();
    StudyDao studyDao = new StudyDao();
    ApplicationMethodDao methodDao = new ApplicationMethodDao();
    VectorDao vectorDao = new VectorDao();
    ExperimentResultDao resultDao = new ExperimentResultDao();
    AntibodyDao antibodyDao = new AntibodyDao();
    HRDonorDao hrDonorDao = new HRDonorDao();

    public long insertGuide(Guide guide) throws Exception{
        return guideDao.insertGuide(guide);
    }
    public void insertGuideGenomeInfo(Guide guide) throws Exception{
        guideDao.insertGenomeInfo(guide);
    }
    public long insertEditor(Editor editor) throws Exception{
        return editorDao.insertEditor(editor);
    }

    public boolean updateEditorIfNeeded(Editor ed) throws Exception {

        List<Editor> list = editorDao.getEditorById(ed.getId());
        if( list==null && list.isEmpty() ) {
            return false;
        }
        Editor edInDb = list.get(0);

        boolean isMatching =
            Utils.stringsAreEqual(ed.getType(), edInDb.getType())
                && Utils.stringsAreEqual(ed.getSubType(), edInDb.getSubType())
                && Utils.stringsAreEqual(ed.getSymbol(), edInDb.getSymbol())
                && Utils.stringsAreEqual(ed.getAlias(), edInDb.getAlias())
                && Utils.stringsAreEqual(ed.getSpecies(), edInDb.getSpecies())
                && Utils.stringsAreEqual(ed.getEditorVariant(), edInDb.getEditorVariant())
                && Utils.stringsAreEqual(ed.getPamPreference(), edInDb.getPamPreference())
                && Utils.stringsAreEqual(ed.getSubstrateTarget(), edInDb.getSubstrateTarget())
                && Utils.stringsAreEqual(ed.getActivity(), edInDb.getActivity())
                && Utils.stringsAreEqual(ed.getFusion(), edInDb.getFusion())
                && Utils.stringsAreEqual(ed.getDsbCleavageType(), edInDb.getDsbCleavageType())
                && Utils.stringsAreEqual(ed.getTarget_sequence(), edInDb.getTarget_sequence())
                && Utils.stringsAreEqual(ed.getSource(), edInDb.getSource())
                && Utils.stringsAreEqual(ed.getProteinSequence(), edInDb.getProteinSequence())
                && Utils.stringsAreEqual(ed.getEditorDescription(), edInDb.getEditorDescription())
                && Utils.stringsAreEqual(ed.getAnnotatedMap(), edInDb.getAnnotatedMap())
                && Utils.stringsAreEqual(ed.getOrientation(), edInDb.getOrientation())
                && Utils.stringsAreEqual(ed.getCatalog(), edInDb.getCatalog())
                && Utils.stringsAreEqual(ed.getRrid(), edInDb.getRrid());

        boolean wasUpdated = false;
        if( !isMatching ) {
            // no match
            if( edInDb.getTier()==0 ) {
                editorDao.updateEditor(ed);
                wasUpdated = true;
            } else {
                // no match, editor tier in db >0 -- report a problem
                Manager.getManagerInstance().info("*** editor " + ed.getId() + " data in db differs from incoming -- editor tier in db " + edInDb.getTier());
                return false;
            }
        }

        // guide data matches -- check genome info data
        boolean isMatching2 =
            Utils.stringsAreEqual(ed.getTargetLocus(), edInDb.getTargetLocus()) &&
            Utils.stringsAreEqual(ed.getTarget_sequence(), edInDb.getTarget_sequence()) &&
            Utils.stringsAreEqual(ed.getAssembly(), edInDb.getAssembly()) &&
            Utils.stringsAreEqual(ed.getChr(), edInDb.getChr()) &&
            Utils.stringsAreEqual(ed.getStart(), edInDb.getStart()) &&
            Utils.stringsAreEqual(ed.getStop(), edInDb.getStop()) &&
            Utils.stringsAreEqual(ed.getStrand(), edInDb.getStrand()) &&
            Utils.stringsAreEqual(ed.getSpecies(), edInDb.getSpecies());

        if( !isMatching2 ) {
            // no match
            if( edInDb.getTier()==0 ) {
                editorDao.updateGenomeInfo(ed);
                wasUpdated = true;
            } else {
                // no match, editor tier in db >0 -- report a problem
                Manager.getManagerInstance().info("*** editor " + ed.getId() + " data in db differs from incoming -- editor tier in db " + edInDb.getTier());
                return false;
            }
        }

        return wasUpdated;
    }

    public void insertEditorGenomeInfo(Editor editor) throws Exception{
        editorDao.insertGenomeInfo(editor);
    }
    public long insertDelivery(Delivery delivery) throws Exception{
        long id = deliveryDao.insertDelivery(delivery);
        delivery.setId(id);
        return id;
    }

    public long insertModel(Model model) throws Exception {
        return modelDao.insertModel(model);
    }

    public long getModelId(Model model) throws Exception {
        return modelDao.getModelId(model);
    }

    public long getGuideId(Guide guide) throws Exception {
        return guideDao.getGuideId(guide);
    }

    public long getEditorId(Editor editor) throws Exception {
        return editorDao.getEditorId(editor);
    }

    public long getDeliveryId(Delivery delivery) throws Exception {
        return deliveryDao.getDeliveryId(delivery);
    }

    public boolean updateDeliveryIfNeeded(Delivery ds) throws Exception {

        List<Delivery> list = deliveryDao.getDeliverySystemsById(ds.getId());
        if( list==null && list.isEmpty() ) {
            return false;
        }
        Delivery dsInDb = list.get(0);

        boolean isMatching =
                Utils.stringsAreEqual(ds.getType(), dsInDb.getType())
            && Utils.stringsAreEqual(ds.getSubtype(), dsInDb.getSubtype())
            && Utils.stringsAreEqual(ds.getName(), dsInDb.getName())
            && Utils.stringsAreEqual(ds.getSource(), dsInDb.getSource())
            && Utils.stringsAreEqual(ds.getDescription(), dsInDb.getDescription())
            && Utils.stringsAreEqual(ds.getLabId(), dsInDb.getLabId())
            && Utils.stringsAreEqual(ds.getAnnotatedMap(), dsInDb.getAnnotatedMap())
            && Utils.stringsAreEqual(ds.getRrid(), dsInDb.getRrid())
            && Utils.stringsAreEqual(ds.getNpSize(), dsInDb.getNpSize())
            && Utils.stringsAreEqual(ds.getMolTargetingAgent(), dsInDb.getMolTargetingAgent())
            && Utils.stringsAreEqual(ds.getSequence(), dsInDb.getSequence())
            && Utils.stringsAreEqual(ds.getZetaPotential(), dsInDb.getZetaPotential())
            && Utils.stringsAreEqual(ds.getNpPolydispersityIndex(), dsInDb.getNpPolydispersityIndex());

        if( !isMatching ) {
            // no match
            if( dsInDb.getTier()==0 ) {
                deliveryDao.updateDelivery(ds);
                return true;
            }

            // no match, ds tier in db >0 -- report a problem
            Manager.getManagerInstance().info("*** delivery system "+ds.getId()+" data in db differs from incoming -- ds tier in db "+dsInDb.getTier());
            return false;
        }

        return false; // match -- nothing to do
    }

    public long insertExperimentRecord(ExperimentRecord experiment) throws Exception{
        return expRecordDao.insertExperimentRecord(experiment);
    }

    public long getExpRecId(ExperimentRecord experiment) throws Exception {
        return expRecordDao.getExpRecordId(experiment);
    }

    public Person getPersonByEmail(String email) throws Exception{
     return personDao.getPersonByEmail(email).get(0);
    }

    public void insertStudy(Study s) throws Exception{
        studyDao.insertStudy(s);
    }

    public int insertMethod(ApplicationMethod method) throws Exception {
        return methodDao.insertApplicationMethod(method);
    }

    public long insertVector(Vector v) throws Exception{
        return vectorDao.insertVector(v);
    }

    public long getVectorId(Vector v) throws Exception {
        return vectorDao.getVectorId(v);
    }
    public int insertAntibody(Antibody a) throws Exception{
        return antibodyDao.insertAntibody(a);
    }

    public int getAntibodyId(Antibody a) throws Exception {
        return antibodyDao.getAntibodyId(a);
    }
    public int getMethodId(ApplicationMethod method) throws Exception {
        return methodDao.getAppMethodId(method);
    }

    public long insertExperimentResult(ExperimentResultDetail e) throws Exception{
        return resultDao.insertExperimentResult(e);
    }

    public void insertExperimentResultDetail(ExperimentResultDetail e) throws Exception {
        resultDao.insertExperimentResultDetail(e);
    }

    public void updateExperimentResultDetail(ExperimentResultDetail e) throws Exception {
        String sql = "UPDATE experiment_result_detail SET result=? WHERE result_id=? AND replicate=?";
        resultDao.update(sql, e.getResult(), e.getResultId(), e.getReplicate());
    }

    public String getResultForExperimentResultDetail(long resultId, int replicate) throws Exception {
        String sql = "SELECT MAX(result) FROM experiment_result_detail WHERE result_id=? AND replicate=?";
        List<String> list = StringListQuery.execute(resultDao, sql, resultId, replicate);
        if( list==null || list.isEmpty() ) {
            return null;
        }
        return list.get(0);
    }

    public int updateNumberOfSamplesForResult(long resultId, int nrOfSamples) throws Exception {

        int nrOfSamplesInDb = getCount("SELECT number_of_samples FROM experiment_result WHERE result_id=?", resultId);
        if( nrOfSamplesInDb != nrOfSamples ) {
            String sql = "UPDATE experiment_result SET number_of_samples=? WHERE result_id=? AND number_of_samples<>?";
            return resultDao.update(sql, nrOfSamples, resultId, nrOfSamples);
        }
        return 0;
    }


    public void insertGuideAssoc(long expRecId, long guideId) throws Exception {
        if( expRecId!=0 && guideId!=0 ) {
            guideDao.insertGuideAssoc(expRecId, guideId);
        }
    }

    public void insertVectorAssoc(long expRecId, long vectorId) throws Exception {
        if( expRecId!=0 && vectorId!=0 ) {
            vectorDao.insertVectorAssoc(expRecId, vectorId);
        }
    }

    public void insertAntibodyAssoc(long expRecId, int antibodyId) throws Exception {
        if( expRecId!=0 && antibodyId!=0 ) {
            antibodyDao.insertAntibodyAssoc(expRecId, antibodyId);
        }
    }

    public long insertHrdonor(HRDonor a) throws Exception{
        return hrDonorDao.insertHRDonor(a);
    }

    public long getHrdonorId(HRDonor a) throws Exception {
        return hrDonorDao.getHRDonorId(a);
    }

    public void insertOffTarget(OffTarget offTarget) throws Exception {
        OffTargetDao dao = new OffTargetDao();
        dao.insertOffTarget(offTarget);
    }

    public List<ExperimentRecord> getExpRecords( long experimentId) throws Exception {
        return expDao.getExperimentRecords(experimentId);
    }

    public List<Guide> getGuides(long expRecId) throws Exception {
        return guideDao.getGuidesByExpRecId(expRecId);
    }
    public List<Vector> getVectors(long expRecId) throws Exception {
        return vectorDao.getVectorsByExpRecId(expRecId);
    }

    public List<ExperimentResultDetail> getExperimentalResults(long expRecId) throws Exception{
        return resultDao.getResultsByExperimentRecId(expRecId);
    }

    public List<Guide> getGuides() throws Exception{
        return guideDao.getGuides();
    }

    public List<Vector> getVectors() throws Exception{
        return  vectorDao.getAllVectors();
    }

    public void insertOffTargetSite(OffTargetSite o) throws Exception{
        OffTargetSiteDao odao = new OffTargetSiteDao();
        odao.insertOffTargetSite(o);
    }

    public boolean updateGuideIfNeeded(Guide guide) throws Exception {

        List<Guide> guides = guideDao.getGuideById(guide.getGuide_id());
        if( guides==null && guides.isEmpty() ) {
            return false;
        }
        Guide guideInDb = guides.get(0);

        boolean isMatching =
                Utils.stringsAreEqual(guide.getSpecies(), guideInDb.getSpecies())
             && Utils.stringsAreEqual(guide.getSource(), guideInDb.getSource())
             && Utils.stringsAreEqual(guide.getPam(), guideInDb.getPam())
                && Utils.stringsAreEqual(guide.getGrnaLabId(), guideInDb.getGrnaLabId())
                && Utils.stringsAreEqual(guide.getGuideFormat(), guideInDb.getGuideFormat())
                && Utils.stringsAreEqual(guide.getSpacerSequence(), guideInDb.getSpacerSequence())
                && Utils.stringsAreEqual(guide.getSpacerLength(), guideInDb.getSpacerLength())
                && Utils.stringsAreEqual(guide.getRepeatSequence(), guideInDb.getRepeatSequence())
                && Utils.stringsAreEqual(guide.getGuide(), guideInDb.getGuide())
                && Utils.stringsAreEqual(guide.getGuideDescription(), guideInDb.getGuideDescription())
                && Utils.stringsAreEqual(guide.getForwardPrimer(), guideInDb.getForwardPrimer())
                && Utils.stringsAreEqual(guide.getReversePrimer(), guideInDb.getReversePrimer())
                && Utils.stringsAreEqual(guide.getLinkerSequence(), guideInDb.getLinkerSequence())
                && Utils.stringsAreEqual(guide.getAntiRepeatSequence(), guideInDb.getAntiRepeatSequence())
                && Utils.stringsAreEqual(guide.getStemloop1Sequence(), guideInDb.getStemloop1Sequence())
                && Utils.stringsAreEqual(guide.getStemloop2Sequence(), guideInDb.getStemloop2Sequence())
                && Utils.stringsAreEqual(guide.getStemloop3Sequence(), guideInDb.getStemloop3Sequence())
                && Utils.stringsAreEqual(guide.getStandardScaffoldSequence(), guideInDb.getStandardScaffoldSequence())
                && Utils.stringsAreEqual(guide.getModifications(), guideInDb.getModifications())
                && Utils.stringsAreEqual(guide.getIvtConstructSource(), guideInDb.getIvtConstructSource())
                && Utils.stringsAreEqual(guide.getVectorId(), guideInDb.getVectorId())
                && Utils.stringsAreEqual(guide.getVectorName(), guideInDb.getVectorName())
                && Utils.stringsAreEqual(guide.getVectorDescription(), guideInDb.getVectorDescription())
                && Utils.stringsAreEqual(guide.getVectorType(), guideInDb.getVectorType())
                && Utils.stringsAreEqual(guide.getAnnotatedMap(), guideInDb.getAnnotatedMap())
                && Utils.stringsAreEqual(guide.getSpecificityRatio(), guideInDb.getSpecificityRatio())
                && Utils.stringsAreEqual(guide.getFullGuide(), guideInDb.getFullGuide())
                && Utils.stringsAreEqual(guide.getGuideCompatibility(), guideInDb.getGuideCompatibility());

        boolean wasUpdated = false;
        if( !isMatching ) {
            // no match
            if( guideInDb.getTier()==0 ) {
                guideDao.updateGuide(guide);
                wasUpdated = true;
            } else {
                // no match, guide tier in db >0 -- report a problem
                Manager.getManagerInstance().info("*** guide " + guide.getGuide_id() + " data in db differs from incoming -- guide tier in db " + guideInDb.getTier());
                return false;
            }
        }

        // guide data matches -- check genome info data
        boolean isMatching2 =
            Utils.stringsAreEqual(guide.getTargetLocus(), guideInDb.getTargetLocus()) &&
            Utils.stringsAreEqual(guide.getTargetSequence(), guideInDb.getTargetSequence()) &&
            Utils.stringsAreEqual(guide.getAssembly(), guideInDb.getAssembly()) &&
            Utils.stringsAreEqual(guide.getChr(), guideInDb.getChr()) &&
            Utils.stringsAreEqual(guide.getStart(), guideInDb.getStart()) &&
            Utils.stringsAreEqual(guide.getStop(), guideInDb.getStop()) &&
            Utils.stringsAreEqual(guide.getStrand(), guideInDb.getStrand()) &&
            Utils.stringsAreEqual(guide.getSpecies(), guideInDb.getSpecies());

        if( !isMatching2 ) {
            // no match
            if( guideInDb.getTier()==0 ) {
                guideDao.updateGenomeInfo(guide);
                wasUpdated = true;
            } else {
                // no match, guide tier in db >0 -- report a problem
                Manager.getManagerInstance().info("*** guide " + guide.getGuide_id() + " data in db differs from incoming -- guide tier in db " + guideInDb.getTier());
                return false;
            }
        }

        return wasUpdated;
    }

    public boolean createExperimentIfMissing(int studyId, long experimentId, String expType) throws Exception {
        Experiment exp = expDao.getExperimentByStudyIdNExperimentId(studyId, experimentId);
        if( exp==null ) {
            String sql = "insert into experiment (study_id,experiment_id,name,type,description,created_date) values (?,?,?,?,?,current_timestamp)";
            expDao.update(sql, studyId, experimentId, "", expType, "");
            return true;
        }
        return false;
    }

    public boolean updateModelIfNeeded(Model model) throws Exception {

        Model modelInDb = modelDao.getModelById(model.getModelId());
        if( modelInDb==null ) {
            return false;
        }

        boolean isMatching =
                Utils.stringsAreEqual(model.getType(), modelInDb.getType())
            && Utils.stringsAreEqual(model.getName(), modelInDb.getName())
            && Utils.stringsAreEqual(model.getOrganism(), modelInDb.getOrganism())
            && Utils.stringsAreEqual(model.getSex(), modelInDb.getSex())
            && Utils.stringsAreEqual(model.getRrid(), modelInDb.getRrid())
            && Utils.stringsAreEqual(model.getSource(), modelInDb.getSource())
            && Utils.stringsAreEqual(model.getTransgene(), modelInDb.getTransgene())
            && Utils.stringsAreEqual(model.getSubtype(), modelInDb.getSubtype())
            && Utils.stringsAreEqual(model.getAnnotatedMap(), modelInDb.getAnnotatedMap())
            && Utils.stringsAreEqual(model.getTransgeneDescription(), modelInDb.getTransgeneDescription())
            && Utils.stringsAreEqual(model.getTransgeneReporter(), modelInDb.getTransgeneReporter())
            && Utils.stringsAreEqual(model.getParentalOrigin(), modelInDb.getParentalOrigin())
            && Utils.stringsAreEqual(model.getDescription(), modelInDb.getDescription())
            && Utils.stringsAreEqual(model.getDisplayName(), modelInDb.getDisplayName())
            && Utils.stringsAreEqual(model.getStrainAlias(), modelInDb.getStrainAlias())
            && Utils.stringsAreEqual(model.getCatalog(), modelInDb.getCatalog())
            && Utils.stringsAreEqual(model.getOntology(), modelInDb.getOntology())
            && Utils.stringsAreEqual(model.getOfficialName(), modelInDb.getOfficialName())
            ;

        if( !isMatching ) {
            // no match
            if( modelInDb.getTier()==0 ) {
                modelDao.updateModel(model);
                return true;
            }

            // no match, model tier in db >0 -- report a problem
            Manager.getManagerInstance().info("*** model "+model.getModelId()+" data in db differs from incoming -- model tier in db "+modelInDb.getTier());
            return false;
        }
        return false; // match -- nothing to do
    }

    public void updateDelivery(long oldId,long newId) throws Exception{
        String sql = "update delivery_system set ds_id = ? where ds_id = ?";

        update(sql,newId,oldId);
        sql = "update experiment_record set ds_id = ? where ds_id = ?";

        update(sql,newId,oldId);
    }

    public void updateExperimentRecord(long oldId,long newId) throws Exception{
        String sql = "update experiment_record set experiment_record_id = ? where experiment_record_id = ?";

        update(sql,newId,oldId);
        sql = "update experiment_result set experiment_record_id = ? where experiment_record_id = ?";

        update(sql,newId,oldId);

       /* String sql = "update guide_associations set experiment_record_id = ? where experiment_record_id = ?";

        update(sql,newId,oldId);
        sql = "update vector_associations set experiment_record_id = ? where experiment_record_id = ?";

        update(sql,newId,oldId);
        */
    }

    public void updateExperimentName(long expId, String newName) throws Exception {
        String sql = "UPDATE experiment SET name=? WHERE experiment_id=?";

        update(sql, newName, expId);
    }

    public void updateExperimentDesc(long expId, String newDesc) throws Exception {
        String sql = "UPDATE experiment SET description=? WHERE experiment_id=?";

        update(sql, newDesc, expId);
    }


    public List<Experiment> getAllExperiments() throws Exception {
        return expDao.getAllExperiments();
    }

    public Experiment getExperiment(long expId) throws Exception {
        return expDao.getExperiment(expId);
    }

    public int deleteExperimentData(long expId, long studyId) throws Exception {

        int rowsDeleted = 0;

        Experiment e = getExperiment(expId);
        if( e.getStudyId()!=studyId ) {
            throw new Exception(expId+" has study "+e.getStudyId()+"  but you passed different study id: "+studyId);
        }

        String sql = "select scge_id from images where scge_id in(select experiment_record_id from experiment_record where experiment_id = ?)";
        List<Integer> imageIds = IntListQuery.execute(expDao, sql, expId);
        if( !imageIds.isEmpty() ) {
            throw new Exception("### LOAD ABORTED! there are "+imageIds.size()+" for experiment_id = "+expId);
        }

        sql = "delete from experiment_result_detail where result_id in (" +
                " select result_id from experiment_result WHERE experiment_record_id in (select experiment_record_id from experiment_record where experiment_id = ?) " +
                ")";
        rowsDeleted += expDao.update(sql, expId);

        sql = "delete from experiment_result WHERE experiment_record_id in (select experiment_record_id from experiment_record where experiment_id = ?)";
        rowsDeleted += expDao.update(sql, expId);

        sql = "delete from guide_associations where experiment_record_id in (select experiment_record_id from experiment_record where experiment_id = ?)";
        rowsDeleted += expDao.update(sql, expId);

        sql = "delete from vector_associations where experiment_record_id in (select experiment_record_id from experiment_record where experiment_id = ?)";
        rowsDeleted += expDao.update(sql, expId);

        sql = "delete from antibody_associations where experiment_record_id in (select experiment_record_id from experiment_record where experiment_id = ?)";
        rowsDeleted += expDao.update(sql, expId);

        sql = "delete from experiment_details where experiment_record_id in (select experiment_record_id from experiment_record where experiment_id = ?)";
        rowsDeleted += expDao.update(sql, expId);

        sql = "delete from experiment_record where experiment_id = ?";
        rowsDeleted += expDao.update(sql, expId);

        return rowsDeleted;
    }

    public void updateExperimentLastModifiedDate(long expId) throws Exception {
        String sql = "UPDATE experiment SET last_modified_date=NOW() WHERE experiment_id=?";
        expDao.update(sql, expId);
    }



    public Map<String,String> getExperimentRecordDetails(long expRecId) throws Exception {
        return expRecordDao.getExperimentRecordDetails(expRecId);
    }

    public void insertExperimentRecordDetails(long expRecId, String name, String value) throws Exception {
        expRecordDao.insertExperimentRecordDetails(expRecId, name, value);
    }

    public void updateExperimentRecordDetails(long expRecId, String name, String value) throws Exception {
        expRecordDao.updateExperimentRecordDetails(expRecId, name, value);
    }

    public void deleteExperimentRecordDetails(long expRecId, String name) throws Exception {
        expRecordDao.deleteExperimentRecordDetails(expRecId, name);
    }
}
