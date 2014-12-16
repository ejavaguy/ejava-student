package info.ejava.examples.ejb.interceptor.ejb;

import java.util.List;

@SuppressWarnings("serial")
public class InvalidParam extends Exception {
    private String param;
    private List<String> errors;
    public InvalidParam(String param, List<String> errors) {
        this.param = param;
        this.errors = errors;
    }
    
    @Override
    public String getMessage() {
        StringBuilder text = new StringBuilder();
        if (param!=null) {
            text.append(param.toString());
        }
        if (errors!=null) {
            for (String error: errors) {
                text.append("\n");
                text.append(error);
            }
        }
        return text.toString();
    }    
}
