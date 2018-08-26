package info.ejava.examples.secureping.dto;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="pingResult", namespace="urn:ejava.ejb.security.ping")
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
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("PingResult [context=").append(context)
               .append(", userName=").append(userName)
               .append(", isAdmin=").append(isAdmin)
               .append(", isUser=").append(isUser)
               .append(",\nserviceResult=")
               .append(serviceResult).append("]");
        return builder.toString();
    }
}
