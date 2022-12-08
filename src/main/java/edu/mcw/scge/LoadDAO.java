package edu.mcw.scge;

import edu.mcw.rgd.process.Utils;
import edu.mcw.scge.dao.*;

import edu.mcw.scge.dao.implementation.*;
import edu.mcw.scge.datamodel.*;

import java.util.List;


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
    public List<Guide> getAllGuides() throws Exception {
        return guideDao.getGuides();
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

        if( !isMatching ) {
            // no match
            if( guideInDb.getTier()==0 ) {
                guideDao.updateGuide(guide);
                return true;
            }

            // no match, guide tier in db >0 -- report a problem
            Manager.getManagerInstance().info("*** guide "+guide.getGuide_id()+" data in db differs from incoming -- guide tier in db "+guideInDb.getTier());
            return false;
        }

        // guide data matches -- check genome info data
        isMatching =
                Utils.stringsAreEqual(guide.getTargetLocus(), guideInDb.getTargetLocus()) &&
            Utils.stringsAreEqual(guide.getTargetSequence(), guideInDb.getTargetSequence()) &&
            Utils.stringsAreEqual(guide.getAssembly(), guideInDb.getAssembly()) &&
            Utils.stringsAreEqual(guide.getChr(), guideInDb.getChr()) &&
            Utils.stringsAreEqual(guide.getStart(), guideInDb.getStart()) &&
            Utils.stringsAreEqual(guide.getStop(), guideInDb.getStop()) &&
            Utils.stringsAreEqual(guide.getStrand(), guideInDb.getStrand()) &&
            Utils.stringsAreEqual(guide.getSpecies(), guideInDb.getSpecies());

        if( !isMatching ) {
            // no match
            if( guideInDb.getTier()==0 ) {
                guideDao.updateGenomeInfo(guide);
                return true;
            }

            // no match, guide tier in db >0 -- report a problem
            Manager.getManagerInstance().info("*** guide "+guide.getGuide_id()+" data in db differs from incoming -- guide tier in db "+guideInDb.getTier());
            return false;
        } else {
            return false; // match -- nothing to do
        }
    }
    public void updateGuideAssoc(long oldId,long newId) throws Exception{
        String sql = "update guide_associations set guide_id = ? where guide_id = ?";

        update(sql,newId,oldId);

        sql = "update off_target set guide_id = ? where guide_id = ?";

        update(sql,newId,oldId);
    }
    public void updateVector(long oldId,long newId) throws Exception{
        String sql = "update vector set vector_id = ? where vector_id = ?";

        update(sql,newId,oldId);
    }
    public void updateVectorAssoc(long oldId,long newId) throws Exception{
        String sql = "update vector_associations set vector_id = ? where vector_id = ?";

        update(sql,newId,oldId);
    }
    public void updateEditor(long oldId,long newId) throws Exception{
        String sql = "update editor set editor_id = ? where editor_id = ?";

        update(sql,newId,oldId);
        sql = "update experiment_record set editor_id = ? where editor_id = ?";

        update(sql,newId,oldId);
    }
    public void updateModel(long oldId,long newId) throws Exception{
        String sql = "update model set model_id = ? where model_id = ?";

        update(sql,newId,oldId);
        sql = "update experiment_record set model_id = ? where model_id = ?";

        update(sql,newId,oldId);
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

    public void updateExperiment(long oldId,long newId) throws Exception {
        String sql = "update experiment set experiment_id = ? where experiment_id = ?";

        update(sql,newId,oldId);
        sql = "update experiment_record set experiment_id = ? where experiment_id = ?";

        update(sql,newId,oldId);

       /* String sql = "update guide_associations set experiment_record_id = ? where experiment_record_id = ?";

        update(sql,newId,oldId);
        sql = "update vector_associations set experiment_record_id = ? where experiment_record_id = ?";

        update(sql,newId,oldId);
        */
    }



    public List<Study> getStudyById(int studyId) throws Exception {
        return studyDao.getStudyById(studyId);
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

        String sql = "delete from experiment_result_detail where result_id in (" +
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

        sql = "delete from experiment_record where experiment_id = ?";
        rowsDeleted += expDao.update(sql, expId);

        return rowsDeleted;
    }
}
