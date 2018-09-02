package ejava.projects.edmv.jpa;

import java.util.List;

import javax.persistence.EntityManager;

import ejava.projects.edmv.bo.DMVVehicleRegistration;
import ejava.projects.edmv.dao.DMVVehicleDAO;

/**
 * This class provides a JPA DAO implementation for vehicles.
 *
 */
public class JPADMVVehicleDAO implements DMVVehicleDAO {
    protected EntityManager em;

    public void setEntityManager(EntityManager em) {
        this.em = em;
    }

    public DMVVehicleRegistration getRegistration(long id) {
        return em.find(DMVVehicleRegistration.class, id);
    }

    @SuppressWarnings("unchecked")
    public List<DMVVehicleRegistration> getRegistrations(int index, int count) {
        return (List<DMVVehicleRegistration>)
            em.createQuery("select vr from DMVVehicleRegistration vr")
              .setFirstResult(index)
              .setMaxResults(count)
              .getResultList();
    }
}
