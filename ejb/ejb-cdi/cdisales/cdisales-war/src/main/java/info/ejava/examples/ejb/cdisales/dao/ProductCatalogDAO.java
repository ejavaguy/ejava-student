package info.ejava.examples.ejb.cdisales.dao;

import java.util.Collections;
import java.util.List;

import info.ejava.examples.ejb.cdisales.bl.ProductCatalog;
import info.ejava.examples.ejb.cdisales.bo.Member;
import info.ejava.examples.ejb.cdisales.bo.Product;

import javax.enterprise.inject.Alternative;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

@Named
public class ProductCatalogDAO implements ProductCatalog {
    @Inject
    private EntityManager em;

    @Override
    public Product addProduct(Product product) {
        if (product==null) { return null; }
        product=em.merge(product);
        return product;
    }

    @Override
    public List<Product> getProductsForSale(Product template, int offset, int limit) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Product> qdef = cb.createQuery(Product.class);
        Root<Product> p = qdef.from(Product.class);
        qdef.select(p).orderBy(cb.asc(p.get("name")));
        
        Predicate pred = cb.conjunction();
        if (template != null) {
            List<Expression<Boolean>> expr = pred.getExpressions();
            if (template.getCategory()!=null) {
                expr.add(cb.equal(p.get("category"), template.getCategory()));
            }
            if (template.getName()!=null) {
                String name = template.getName().replace("*", "%").trim();
                expr.add(cb.like(p.<String>get("name"), name));
            }
        }
        qdef.where(pred);
        
        TypedQuery<Product> query = em.createQuery(qdef);
        if (offset > 0) {
            query.setFirstResult(offset);
        }
        if (limit > 0) {
            query.setMaxResults(limit);
        }
        return query.getResultList();
    }

    @Override
    public List<Product> getBuyerProducts(Member buyer, int offset, int limit) {
        TypedQuery<Product> query = null;
        if (buyer!=null) {
            if (buyer.getId()!=0) {
                query = em.createNamedQuery("CDIProduct.getProductsByBuyer", Product.class).setParameter("buyer", buyer);
            } else if (buyer.getLogin()!=null) {
                query = em.createNamedQuery("CDIProduct.getProductsByBuyerLogin", Product.class).setParameter("login", buyer.getLogin());
            }
        }
        if (query!=null) {
            if (offset > 0) {
                query.setFirstResult(offset);
            }
            if (limit > 0) {
                query.setMaxResults(limit);
            }
        }
        
        return query==null ? Collections.<Product>emptyList() : query.getResultList();
    }

    @Override
    public List<Product> getSellerProducts(Member seller, int offset, int limit) {
        TypedQuery<Product> query = null;
        if (seller!=null) {
            if (seller.getId()!=0) {
                query = em.createNamedQuery("CDIProduct.getProductsBySeller", Product.class).setParameter("seller", seller);
            } else if (seller.getLogin()!=null) {
                query = em.createNamedQuery("CDIProduct.getProductsBySellerLogin", Product.class).setParameter("login", seller.getLogin());
            }
        }
        if (query!=null) {
            if (offset > 0) {
                query.setFirstResult(offset);
            }
            if (limit > 0) {
                query.setMaxResults(limit);
            }
        }
        
        return query==null ? Collections.<Product>emptyList() : query.getResultList();
    }
    
    @Override
    public int remove(Product product) {
        if (product==null || product.getId()==0) { return 0; }
        product = em.find(Product.class, product.getId());
        if (product!=null) {
            em.remove(product);
            return 1;
        }
        return 0;
    }
}
