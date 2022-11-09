package edu.mcw.scge;

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

    public void insertGuideAssoc(long expRecId,long guideId) throws Exception {
        guideDao.insertGuideAssoc(expRecId,guideId);
    }

    public void insertVectorAssoc(long expRecId,long vectorId) throws Exception {
        vectorDao.insertVectorAssoc(expRecId,vectorId);
    }
    public void insertAntibodyAssoc(long expRecId,int antibodyId) throws Exception {
        antibodyDao.insertAntibodyAssoc(expRecId,antibodyId);
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
    public void updateGuide(long oldId,long newId) throws Exception{
        String sql = "update guide set guide_id = ? where guide_id = ?";

        update(sql,newId,oldId);


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

    public void updateExperiment(long oldId,long newId) throws Exception{
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
}
