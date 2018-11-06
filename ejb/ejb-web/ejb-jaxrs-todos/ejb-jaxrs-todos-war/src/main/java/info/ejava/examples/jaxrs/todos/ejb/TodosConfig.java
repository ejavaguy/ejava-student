package info.ejava.examples.jaxrs.todos.ejb;

import javax.enterprise.inject.Produces;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

public class TodosConfig {
    @Produces
    @PersistenceContext(unitName="ejavaTodos")
    public EntityManager em;
}
