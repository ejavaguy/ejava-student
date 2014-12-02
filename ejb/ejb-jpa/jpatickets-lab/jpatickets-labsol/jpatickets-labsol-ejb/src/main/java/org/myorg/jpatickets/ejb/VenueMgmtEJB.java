package org.myorg.jpatickets.ejb;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.EJBException;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.myorg.jpatickets.bl.VenueMgmt;
import org.myorg.jpatickets.bl.VenueMgmtImpl;
import org.myorg.jpatickets.bo.Venue;
import org.myorg.jpatickets.dao.VenueDAOImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Stateless
public class VenueMgmtEJB implements VenueMgmtRemote {
    private static final Logger logger = LoggerFactory.getLogger(VenueMgmtEJB.class);
    
    @PersistenceContext(unitName="jpatickets-labsol")
    private EntityManager em;
    
    private VenueMgmt venueMgmt;
    
    @PostConstruct
    public void init() {
        logger.debug("*** VenueMgmtEJB:init({}) ***", super.hashCode());
        
        VenueDAOImpl vdao = new VenueDAOImpl();
        vdao.setEntityManager(em);
        venueMgmt=new VenueMgmtImpl();
        ((VenueMgmtImpl)venueMgmt).setDao(vdao);
    }
    
    @PreDestroy
    public void destroy() {
        logger.debug("*** VenueMgmtEJB:destroy({}) ***", super.hashCode()); 
    }

    @Override
    public Venue createVenue(Venue venue, int sections, int positions, int rows) {
        try {
            return venueMgmt.createVenue(venue, sections, positions, rows);
        } catch (Exception ex) {
            logger.error("error creating venue", ex);
            throw new EJBException("error creating venue:" + ex);
        }
    }

    @Override
    public Venue getVenue(String venueId) {
        try {
            return venueMgmt.getVenue(venueId);
        } catch (Exception ex) {
            logger.error("error getting venue", ex);
            throw new EJBException("error getting venue:" + ex);
        }
    }
}
