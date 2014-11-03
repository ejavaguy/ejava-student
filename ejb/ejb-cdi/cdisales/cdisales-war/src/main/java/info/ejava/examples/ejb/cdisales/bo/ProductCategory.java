package info.ejava.examples.ejb.cdisales.bo;

public enum ProductCategory {
    ELECTRONICS ("Electronics"),
    TOOLS ("Tools"),
    MARINE ("Marine"),
    SPORT ("Sport");
    
    private final String prettyName;
    private ProductCategory(String prettyName) { this.prettyName = prettyName; }
    
    public String getPrettyName() { return prettyName; }
    public static ProductCategory fromString(String value) {
        if (value==null) { return null; }
        
        ProductCategory pc = ProductCategory.valueOf(value);
        if (pc!=null) {
            return pc;
        } else {
            for (ProductCategory pc2: values()) {
                if (pc2.prettyName.equalsIgnoreCase(value)) {
                    return pc2;
                }
            }
        }
        
        return null;
    }
}
