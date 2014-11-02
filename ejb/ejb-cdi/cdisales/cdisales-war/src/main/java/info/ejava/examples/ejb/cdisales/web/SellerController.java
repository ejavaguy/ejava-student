package info.ejava.examples.ejb.cdisales.web;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import info.ejava.examples.ejb.cdisales.bl.ProductCatalog;
import info.ejava.examples.ejb.cdisales.bl.Tx;
import info.ejava.examples.ejb.cdisales.bo.CurrentUser;
import info.ejava.examples.ejb.cdisales.bo.Member;
import info.ejava.examples.ejb.cdisales.bo.Product;
import info.ejava.examples.ejb.cdisales.bo.ProductCategory;

import javax.annotation.PostConstruct;
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

@Named("sellerController")
@ConversationScoped
/*
    Properties within this class are referred to using #{sellerController.xxx}
    syntax in the JSF page. 
*/
public class SellerController implements Serializable {
    private static final Logger logger = LoggerFactory.getLogger(SellerController.class);
    
    @Inject
    private Conversation conversation;
    
    @Inject 
    private CurrentUser user;
    
    @Inject @Tx
    private ProductCatalog catalog;
    
    //domain properties
    Member seller;
    //<h:dataTable value="#{sellerController.products}" var="product">
    List<Product> products;
    //<h:inputText value="#{sellerController.product.name}" required="true"/>
    Product product;
    
    //jsf properties
    private UIForm form;
    private UIForm tableForm;
    private UICommand addCommand;
    
    @PostConstruct
    public void init() {
        logger.info("SellerController({}):init", super.hashCode());
        conversation.begin();
    }

    //data model methods
    public CurrentUser getUser() {
        return user;
    }
    public List<Product> getProducts() {
        logger.info("getProducts()={}", products);
        if (products==null) {
            products = catalog.getSellerProducts(user.getMember(), 0, 0);
        }
        return products;
    }
    public void setProducts(List<Product> products) {
        logger.info("setProducts({})", products);
        this.products=products;
    }
    
    /**
     * Associated getter/setter is called for inputText.value elements
        <h:inputText value="#{sellerController.product.name}"/>
     */
    public Product getProduct() {
        logger.info("getProduct()={}", product);
        return product; 
    }
    public void setProduct(Product product) {
        logger.info("setProduct({})", product);
        this.product = product;
    }
    public List<SelectItem> getCategories() {
        logger.info("getCategories()");
        List<SelectItem> list = new ArrayList<SelectItem>(ProductCategory.values().length);
        for (ProductCategory pc: ProductCategory.values()) {
            list.add(new SelectItem(pc, pc.getPrettyName()));
        }
        return list;
    }
    
    
    /*
    <h:form>
        <h:commandLink binding="#{sellerController.addCommand}" accesskey="n" 
            action="#{sellerController.addNew}" 
            value="Sell New Product"/>
    </h:form>
     */
    public UICommand getAddCommand() { 
        logger.info("getAddCommand()={}", addCommand);
        return addCommand; 
    }
    public void setAddCommand(UICommand addCommand) {
        logger.info("setAddCommand(addCommand={})", addCommand);
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
        logger.info("addNew()");
        this.product = new Product(); //create a new Product instance
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
        logger.info("getForm()={}, rendered={}", form, form==null?null : form.isRendered());
        return form; 
    }
    public void setForm(UIForm form) {
        logger.info("setForm(form={}, rendered={})", form, form==null? null : form.isRendered());
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
        logger.info("add(): product={}", product);
        products.add(product);
        Collections.sort(products, new Product.ProductASC());
        form.setRendered(false);
        addCommand.setRendered(true);
        return "/seller/seller-products.xhtml";
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
        logger.info("getTableForm()={}, rendered={}", tableForm, tableForm==null?null:tableForm.isRendered());
        return tableForm; 
    }
    public void setTableForm(UIForm tableForm) {
        logger.info("setTableForm(tableForm={}, rendered={})", tableForm, tableForm==null ? null : tableForm.isRendered());
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
        logger.info("delete(): product={}", product);
        products.remove(product);
        catalog.remove(product);
        return null;
    }
    
    public String save() {
        logger.info("save", product);
        //create a relationship to the current user as the seller
        product.setSeller(user.getMember());
        //save to DB
        catalog.addProduct(product);
        return null;
    }
    
    public String complete() {
        logger.info("complete({})", super.hashCode());
        conversation.end();
        return "/index";
    }
}
