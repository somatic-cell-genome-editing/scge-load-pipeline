package edu.mcw.scge;

import edu.mcw.scge.dao.*;

import edu.mcw.scge.dao.implementation.*;
import edu.mcw.scge.datamodel.*;
import org.springframework.jdbc.object.BatchSqlUpdate;

import java.sql.Connection;
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

    public int insertGuide(Guide guide) throws Exception{
        return guideDao.insertGuide(guide);
    }
    public int insertEditor(Editor editor) throws Exception{
        return editorDao.insertEditor(editor);
    }
    public int insertDelivery(Delivery delivery) throws Exception{
        return deliveryDao.insertDelivery(delivery);
    }

    public int insertModel(Model model) throws Exception {
        return modelDao.insertModel(model);
    }

    public int getModelId(Model model) throws Exception {
        return modelDao.getModelId(model);
    }

    public int getGuideId(Guide guide) throws Exception {
        return guideDao.getGuideId(guide);
    }
    public int getEditorId(Editor editor) throws Exception {
        return editorDao.getEditorId(editor);
    }
    public int getDeliveryId(Delivery delivery) throws Exception {
        return deliveryDao.getDeliveryId(delivery);
    }

    public int insertExperimentRecord(ExperimentRecord experiment) throws Exception{
        return expRecordDao.insertExperimentRecord(experiment);
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

    public int insertVector(Vector v) throws Exception{
        return vectorDao.insertVector(v);
    }

    public int getVectorId(Vector v) throws Exception {
        return vectorDao.getVectorId(v);
    }
    public int getMethodId(ApplicationMethod method) throws Exception {
        return methodDao.getAppMethodId(method);
    }

    public int insertExperimentResult(ExperimentResultDetail e) throws Exception{
        return resultDao.insertExperimentResult(e);
    }

    public void insertExperimentResultDetail(ExperimentResultDetail e) throws Exception {
        resultDao.insertExperimentResultDetail(e);
    }

    public void insertGuideAssoc(int expRecId,int guideId) throws Exception {
        guideDao.insertGuideAssoc(expRecId,guideId);
    }

    public void insertVectorAssoc(int expRecId,int vectorId) throws Exception {
        vectorDao.insertVectorAssoc(expRecId,vectorId);
    }

    public void insertOffTarget(OffTarget offTarget) throws Exception {
        OffTargetDao dao = new OffTargetDao();
        dao.insertOffTarget(offTarget);
    }

    public List<ExperimentRecord> getExpRecords( int experimentId) throws Exception {
        return expDao.getExperimentRecords(experimentId);
    }

    public List<Guide> getGuides(int expRecId) throws Exception {
        return guideDao.getGuidesByExpRecId(expRecId);
    }
    public List<Vector> getVectors(int expRecId) throws Exception {
        return vectorDao.getVectorsByExpRecId(expRecId);
    }

    public List<ExperimentResultDetail> getExperimentalResults(int expRecId) throws Exception{
        return resultDao.getResultsByExperimentRecId(expRecId);
    }
}
