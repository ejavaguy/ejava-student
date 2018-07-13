package ejava.examples.daoex.jpa;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ejava.examples.daoex.AuthorDAO;
import ejava.examples.daoex.bo.Author;

/**
 * This class implements a DAO using javax.persistence.EntityManager. Most
 * of the work of mapping the objects to the database is being performed in
 * either the @Entity class or in a orm.xml descriptor file. The caller of 
 * this object must manage the transaction scope. The EntityManager is being
 * injected into the DAO at the start of the overall transaction.
 * 
 * @author jcstaff
 * $Id:$
 */
public class JPAAuthorDAO implements AuthorDAO {
    @SuppressWarnings("unused")
    private static final Logger log_ = LoggerFactory.getLogger(JPAAuthorDAO.class);
    private EntityManager em;
    
    /*
     * This methos is not in the DAO interface and must be injected at a 
     * point where we know the DAO's implementation class.
     */
    public void setEntityManager(EntityManager em) {
        this.em=em;
    }
    
    /* (non-Javadoc)
     * @see ejava.examples.dao.jpa.AuthorDAO#create(ejava.examples.dao.domain.Author)
     */
    public void create(Author author) {
        em.persist(author);
    }
    
    /* (non-Javadoc)
     * @see ejava.examples.dao.jpa.AuthorDAO#get(long)
     */
    public Author get(long id) {
        return em.find(Author.class, id);
    }
    
    /* (non-Javadoc)
     * @see ejava.examples.dao.jpa.AuthorDAO#getByQuery(long)
     */
    public Author getByQuery(long id) {
        Query query = em.createQuery("select a from jpaAuthor a where id=:id")
        		            .setParameter("id", id);
        return (Author)query.getSingleResult();
    }
    
    /* (non-Javadoc)
     * @see ejava.examples.dao.jpa.AuthorDAO#update(ejava.examples.dao.domain.Author)
     */
    public Author update(Author author) {
        Author dbAuthor = em.find(Author.class, author.getId());
        dbAuthor.setFirstName(author.getFirstName());
        dbAuthor.setLastName(author.getLastName());
        dbAuthor.setSubject(author.getSubject());
        dbAuthor.setPublishDate(author.getPublishDate());
        return dbAuthor;
    }

    /* (non-Javadoc)
     * @see ejava.examples.dao.jpa.AuthorDAO#updateByMerge(ejava.examples.dao.domain.Author)
     */
    public Author updateByMerge(Author author) {
        return em.merge(author);
    }
    
    /* (non-Javadoc)
     * @see ejava.examples.dao.jpa.AuthorDAO#remove(ejava.examples.dao.domain.Author)
     */
    public void remove(Author author) {
        em.remove(author);
    }
}
