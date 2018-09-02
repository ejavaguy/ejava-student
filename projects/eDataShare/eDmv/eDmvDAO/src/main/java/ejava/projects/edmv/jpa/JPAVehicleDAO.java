package ejava.projects.edmv.jpa;

import java.util.List;

import javax.persistence.EntityManager;

import ejava.projects.edmv.bo.VehicleRegistration;
import ejava.projects.edmv.dao.VehicleDAO;

/**
 * This class provides a _sparse_ example of a JPA DAO for the class project.
 * It is put in place here to demonstrate some of the end-to-end use cases,
 *
 */
public class JPAVehicleDAO implements VehicleDAO {
	private EntityManager em;
	
	public void setEntityManager(EntityManager em) {
		this.em = em;
	}

	public void createRegistration(VehicleRegistration registration) {
        em.persist(registration);
	}

	@SuppressWarnings("unchecked")
    public List<VehicleRegistration> getRegistrations(int index, int count)  {
	    return (List<VehicleRegistration>)
	        em.createQuery("select vr from VehicleRegistration vr")
	                             .setFirstResult(index)
	                             .setMaxResults(count)
	                             .getResultList();
	}
}
