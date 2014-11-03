package info.ejava.examples.ejb.cdisales.bo;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.Date;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@Table(name="CDISALES_PRODUCT")
@NamedQueries({
    @NamedQuery(name="CDIProduct.getProductsByBuyer", 
        query="select p from Product p where p.buyer=:buyer"),
    @NamedQuery(name="CDIProduct.getProductsByBuyerLogin", 
        query="select p from Product p where p.buyer.login=:login"),
    @NamedQuery(name="CDIProduct.getProductsBySeller", 
        query="select p from Product p where p.seller=:seller"),
    @NamedQuery(name="CDIProduct.getProductsBySellerLogin", 
        query="select p from Product p where p.seller.login=:login")
})
public class Product {
    @Id @GeneratedValue
    @Column(name="PRODUCT_ID")
    private int id;
    
    @NotNull
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name="DATE_CREATED")
    private Date dateCreated;
    
    @NotNull
    @Enumerated(EnumType.ORDINAL)
    @Column(name="CATEGORY", nullable=false)
    private ProductCategory category;
    
    @NotNull
    @Column(name="PRODUCT_NAME", length=50)
    private String name;
    
    @Column(name="YEAR")
    private int year;
    
    @NotNull
    @Column(name="PRICE", precision=7, scale=2, nullable=false)
    private BigDecimal price;
    
    @NotNull
    @ManyToOne(fetch=FetchType.LAZY, optional=false)
    @JoinColumn(name="SELLER_ID", nullable=false)
    private Member seller;
    
    @ManyToOne(fetch=FetchType.EAGER)
    @JoinColumn(name="BUYER_ID")
    private Member buyer;
    
    @PrePersist
    protected void prePersist() { if (dateCreated==null) { dateCreated=new Date(); } }
    
    public Product() {}
    public Product(int id) {
        this.id = id;
    }
    
    public int getId() { return id; }    
    public Date getDateCreated() { return dateCreated; }
    
    public ProductCategory getCategory() { return category;}
    public void setCategory(ProductCategory category) {
        this.category = category;
    }
    
    public String getName() { return name; }
    public void setName(String name) {
        this.name = name;
    }
    
    public int getYear() { return year; }
    public void setYear(int year) {
        this.year = year;
    }
    
    public BigDecimal getPrice() { return price;}
    public void setPrice(BigDecimal price) {
        this.price = price;
    }
    
    public Member getSeller() { return seller; }
    public void setSeller(Member seller) {
        this.seller = seller;
    }
    
    public Member getBuyer() { return buyer; }
    public void setBuyer(Member buyer) {
        this.buyer = buyer;
    }
    
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("Product [category=").append(category)
                .append(", name=").append(name)
                .append(", year=").append(year)
                .append(", price=").append(price)
                .append("]");
        return builder.toString();
    }

    public static class ProductASC implements Comparator<Product> {
        @Override
        public int compare(Product lhs, Product rhs) {
            if (lhs==null && rhs==null) { return 0; }
            if (lhs!=null && (rhs==null || rhs.name==null)) { return 1; }
            if (rhs!=null && (lhs==null || lhs.name==null)) { return -1; }
            int result=lhs.name.compareTo(rhs.name);
            if (result!=0) { return result; }
            
            if (lhs.dateCreated==null && rhs.dateCreated==null) { return result; }
            if (lhs.dateCreated!=null && rhs.dateCreated==null) { return -1; }
            if (rhs.dateCreated!=null && lhs.dateCreated==null) { return 1; }
            return lhs.dateCreated.compareTo(lhs.dateCreated);
        }        
    }
}
