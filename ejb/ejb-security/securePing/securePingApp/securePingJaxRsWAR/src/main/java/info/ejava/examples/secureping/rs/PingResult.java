package info.ejava.examples.secureping.rs;

public class PingResult {
    private String context;
    private String userName;
    private Boolean isAdmin;
    private Boolean isUser;
    private String serviceResult;
    
    public PingResult() {}
    public PingResult(String context, String user, Boolean isAdmin, Boolean isUser) {
        this.context = context;
        this.userName = user;
        this.isAdmin = isAdmin;
        this.isUser = isUser;
    }
    
    public void setServiceResult(String serviceResult) {
        this.serviceResult = serviceResult;        
    }
    public String getContext() {
        return context;
    }
    public void setContext(String context) {
        this.context = context;
    }
    public String getUserName() {
        return userName;
    }
    public void setUserName(String userName) {
        this.userName = userName;
    }
    public Boolean getIsAdmin() {
        return isAdmin;
    }
    public void setIsAdmin(Boolean isAdmin) {
        this.isAdmin = isAdmin;
    }
    public Boolean getIsUser() {
        return isUser;
    }
    public void setIsUser(Boolean isUser) {
        this.isUser = isUser;
    }
    public String getServiceResult() {
        return serviceResult;
    }
}
