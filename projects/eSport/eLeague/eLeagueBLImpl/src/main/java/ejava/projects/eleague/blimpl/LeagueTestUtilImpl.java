package ejava.projects.eleague.blimpl;

import java.util.Map;

import javax.persistence.EntityManager;

import ejava.projects.eleague.bl.LeagueTestUtil;
import ejava.util.jpa.DBUtil;

public class LeagueTestUtilImpl implements LeagueTestUtil {
    protected static final String DROP_SCRIPT = "ddl/eLeague_drop.ddl";  
    protected static final String CREATE_SCRIPT = "ddl/eLeague_create.ddl";      
    private EntityManager em;
    
    public void setEntityManager(EntityManager em) {
        this.em = em;
    }
    
    protected DBUtil getDBUtil() {
        DBUtil dbUtil = new DBUtil();
        dbUtil.setEntityManager(em);
        dbUtil.addDropPath(DROP_SCRIPT);
        dbUtil.addCreatePath(CREATE_SCRIPT);
        return dbUtil;
    }
    

    public Map<String, Number> drop() throws RuntimeException {
        DBUtil dbUtil = getDBUtil();
        return dbUtil.dropAll();
    }
    
    public void create(Map<String, Number> sequenceVals) throws RuntimeException {
        DBUtil dbUtil = getDBUtil();
        dbUtil.createAll(sequenceVals);
    }

    @Override
    public void reset() throws RuntimeException {
        Map<String, Number> sequenceVals = drop();
        create(sequenceVals);
    }
}
