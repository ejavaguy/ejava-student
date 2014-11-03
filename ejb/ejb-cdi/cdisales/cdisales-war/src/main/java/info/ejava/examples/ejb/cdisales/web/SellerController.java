package info.ejava.examples.ejb.cdisales.web;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import info.ejava.examples.ejb.cdisales.bl.ProductCatalog;
import info.ejava.examples.ejb.cdisales.bl.Tx;
import info.ejava.examples.ejb.cdisales.bo.CurrentUser;
import info.ejava.examples.ejb.cdisales.bo.Member;
import info.ejava.examples.ejb.cdisales.bo.Product;
import info.ejava.examples.ejb.cdisales.bo.ProductCategory;
import info.ejava.examples.ejb.cdisales.ejb.InvalidProduct;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.enterprise.context.Conversation;
import javax.enterprise.context.ConversationScoped;
import javax.faces.component.UICommand;
import javax.faces.component.UIForm;
import javax.faces.model.SelectItem;
import javax.inject.Inject;
import javax.inject.Named;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

@SuppressWarnings("serial")

@Named("sellerController") //named used by the JSF page to access properties and methods
@ConversationScoped        //stays alive longer than a request and shorter than a session
/*
    Properties within this class are referred to using #{sellerController.xxx}
    syntax in the JSF page. 
*/
public class SellerController implements Serializable {
    private static final Logger logger = LoggerFactory.getLogger(SellerController.class);
    
    /**
     * Gets injected and is place to stash errors to be displayed.
     */
    @Inject 
    private ErrorController error;
    
    /** Can be used to manage the conversation */
    @Inject
    private Conversation conversation; 
    
    
    /** A reference to back-end business logic that can manage products. This
     * will be injected by CDI with the aid of a @Tx Qualifier 
     */
    @Inject @Tx
    private ProductCatalog catalog;
    
    /***************************************
     * Business data 
     */
    
    /** 
     * Current user will be injected by CDI into this property using a @Produces
     * by one of our components. 
     */
    @Inject 
    private CurrentUser user;
    
    /**
     * This is initially populated using a call to the back-end for persisted 
     * items for sale and then updated as products are added for sale during the 
     * conversation.
     */
    List<Product> products;
    /**
     * This is the product the page is currently working with. No argument 
     * actions methods will be called and it is assumed they should be acting
     * on the last item referenced by setProduct().
     */
    Product product;

    /***************************************
     * JSF data
     */
    /**
     * Form used when adding a new product. We will hide this form 
     * when the user is not actively adding a new product.
     <pre>
         <h:form binding="#{sellerController.form}" rendered="false">
     </pre>
     */
    private UIForm form;
    /**
     * Form used when displaying the list of products
     <pre>
         <h:form binding="#{sellerController.tableForm}"
     </pre>
     */
    private UIForm tableForm;
    /**
     * Command button used when user chooses to add a new product. We track this
     * so we can hide it while the user is entering the product data.
     <pre>
        <h:commandButton binding="#{sellerController.addCommand}" accesskey="n" 
     </pre>
     */
    private UICommand addCommand;
    
    @PostConstruct
    public void init() {
        logger.debug("*** SellerController({}):init ***", super.hashCode());
        conversation.begin(); //got to start it since we declared it
    }

    @PreDestroy
    public void destroy() {
        logger.debug("*** SellerController({}):destroy ***", super.hashCode());
    }
    
    //data model methods
    public Member getSeller() {
        return user.getMember();
    }
    public List<Product> getProducts() {
        logger.debug("getProducts()={}", products);
        if (products==null) {
            products = catalog.getSellerProducts(user.getMember(), 0, 0);
        }
        return products;
    }
    public void setProducts(List<Product> products) {
        logger.debug("setProducts({})", products);
        this.products=products;
    }
    
    /**
     * Associated getter/setter is called for inputText.value elements. The 
     * initial value of the form field will be the value provided in getProduct.
     <pre>
        <h:inputText value="#{sellerController.product.name}"/>
     </pre>
     */
    public Product getProduct() {
        logger.debug("getProduct()={}", product);
        return product; 
    }
    public void setProduct(Product product) {
        logger.debug("setProduct({})", product);
        this.product = product;
    }
    public List<SelectItem> getCategories() {
        logger.debug("getCategories()");
        List<SelectItem> list = new ArrayList<SelectItem>(ProductCategory.values().length);
        for (ProductCategory pc: ProductCategory.values()) {
            list.add(new SelectItem(pc, pc.getPrettyName()));
        }
        return list;
    }
    
    
    /** 
     * The provider will supply a UICommand object for a form within the binding
     * sellerController.addCommand. 
    <pre>
    <h:form>
        <h:commandLink binding="#{sellerController.addCommand}" accesskey="n" 
            action="#{sellerController.addNew}" 
            value="Sell New Product"/>
    </h:form>
    </pre>
    The provider will access that property through setAddCommand() and getAddCommand().
    This controller gets a chance to modify the UICommand properties but the object
    itself is provided by the JSF provider.
     */
    public UICommand getAddCommand() { 
        logger.debug("getAddCommand()={}", addCommand);
        return addCommand; 
    }
    public void setAddCommand(UICommand addCommand) {
        logger.debug("setAddCommand(addCommand={})", addCommand);
        this.addCommand = addCommand;
    }
    
    /**
     * This action method will instantiate a new "Product" bean, disable 
     * the form containing this action, enable the form that will work on the 
     * new Product bean. 
     *    
     * The action method is referred to by name in an action. The Return value is used
     * to navigate to a new page.
            action="#{sellerController.addNew}"
            
        1) optionally call business logic
        2) return a value that will be used to choose the next page             
     */
    public String addNew() {
        logger.debug("addNew()");
        this.product = new Product(); //create a new Product instance
        
        //instead of having our user fill in everything -- lets make some stuff up
        //to be initially displayed in the form
        Random r = new Random();
        product.setCategory(ProductCategory.values()[r.nextInt(ProductCategory.SPORT.ordinal())]);
        product.setName("name" + r.nextInt(100));
        product.setYear(1980 + r.nextInt(30));
        product.setPrice(new BigDecimal(r.nextInt(10000)));
        form.setRendered(true);       //activate the form that will operate on new Product
        addCommand.setRendered(false);//disable the view that called this action method
        return null;                  
    }
    
    
    
    /*
    <h:form binding="#{sellerController.form}" rendered="false">
        <h:outputText value="Category"/>
        <h:selectOneMenu value="#{sellerController.product.category}" required="true">
            <f:selectItems value="#{sellerController.categories}"/>
        </h:selectOneMenu>
        
        <h:outputText value="Name"/>
        <h:inputText value="#{sellerController.product.name}" required="true"/>
        
        <h:outputText value="Year"/>
        <h:inputText value="#{sellerController.product.year}"/>
        
        <h:outputText value="Price"/>
        <h:inputText value="#{sellerController.product.price}"/>
        
        <p></p>
        <h:commandButton value="Add Product" action="#{sellerController.add}"/>
    </h:form>
     */
    public UIForm getForm() { 
        logger.debug("getForm()={}, rendered={}", form, form==null?null : form.isRendered());
        return form; 
    }
    public void setForm(UIForm form) {
        logger.debug("setForm(form={}, rendered={})", form, form==null? null : form.isRendered());
        this.form = form;
    }
    
    /**
     * This action method is used to add the populated "Product" bean to the 
     * conversation. 
     * 
     * Action Method to start working with a new product. The exact name of this 
     * method is referenced in the action element and the return value is used 
     * to navigate to another page.
     * 
        The exact name of this command is used in commandButton.action        
        <h:commandButton value="Add Product" action="#{sellerController.add}"/>
        
        1) optionally call business logic
        2) return a value that will be used to choose the next page
     */
    public String add() {
        logger.debug("add(): product={}", product);
        products.add(product);
        Collections.sort(products, new Product.ProductASC());
        form.setRendered(false);
        addCommand.setRendered(true);
        return "/seller/seller-products";
    }
    
    
    /*
    <h:form binding="#{sellerController.tableForm}">
     <h:dataTable value="#{sellerController.products}" var="product">
       <h:column>
            <f:facet name="header">
                <h:column>
                    <h:outputText value="Name"></h:outputText>
                </h:column>
            </f:facet>
            <h:outputText value="#{product.name}"/>
         </h:column>
         
         <h:column>
             <f:facet name="header">
                 <h:column>
                     <h:outputText value="Actions"></h:outputText>
                 </h:column>
             </f:facet>
             <h:panelGrid columns="2">
                 <h:commandLink value="delete" action="#{sellerController.delete}">
                     <f:setPropertyActionListener 
                         target="#{sellerController.product}" 
                         value="#{product}"/>
                 </h:commandLink>
             </h:panelGrid>
         </h:column>
         
     </h:dataTable>
    </h:form>
     */
    public UIForm getTableForm() { 
        logger.debug("getTableForm()={}, rendered={}", tableForm, tableForm==null?null:tableForm.isRendered());
        return tableForm; 
    }
    public void setTableForm(UIForm tableForm) {
        logger.debug("setTableForm(tableForm={}, rendered={})", tableForm, tableForm==null ? null : tableForm.isRendered());
        this.tableForm = tableForm;
    }
    /*
     * Action Method: The exact name of this method is referenced in the action
     *     attribute and the return value is used to navigate to another page.
        <h:commandLink value="delete" action="#{sellerController.delete}">
        
        1) optionally call business logic
        2) return a value that will be used to choose the next page
     */
    public String delete() {
        logger.debug("delete(): product={}", product);
        products.remove(product);
        catalog.remove(product);
        return null;
    }
    
    public String save() {
        logger.debug("save", product);
        //create a relationship to the current user as the seller
        product.setSeller(user.getMember());
        //save to DB
        products.remove(product);
        try {
            product = catalog.addProduct(product);
            //replace with merged instance
            products.add(product);
            Collections.sort(products, new Product.ProductASC());
            return null;
        } catch (InvalidProduct ex) {
            String errorMsg = "error saving product:" + product;
            error.setError(errorMsg);
            error.setException(ex);
            return "error";
        }
    }
}
