package ejava.examples.ejbwar.jaxrs;

import java.io.InputStream;

import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;
import javax.json.bind.JsonbConfig;
import javax.json.bind.config.PropertyNamingStrategy;
import javax.ws.rs.ext.ContextResolver;
import javax.xml.bind.JAXBException;

public class JSONUtils implements ContextResolver<Jsonb>{
    private static JSONUtils instance;
    private Jsonb jsb;
        
    @Override
    public Jsonb getContext(Class<?> type) {
        if (jsb==null) {            
            JsonbConfig config=new JsonbConfig();
            //config.setProperty(JsonbConfig.FORMATTING, true);
            config.setProperty(JsonbConfig.PROPERTY_NAMING_STRATEGY, PropertyNamingStrategy.LOWER_CASE_WITH_UNDERSCORES);
            jsb=JsonbBuilder.create(config);
        }
        return jsb;
    }

    private static JSONUtils getInstance() {
        if (instance==null) {
            instance=new JSONUtils();
        }
        return instance;
    }
    
    public static <T> String marshal(T object) {
        if (object==null) {
            return "";
        }
        
        Jsonb jsb = getInstance().getContext(object.getClass());
        String jsonString = jsb.toJson(object);
        return jsonString;        
    }
    
    public static <T> T unmarshal(String string, Class<T> type) throws JAXBException {
        if (string==null || string.isEmpty()) {
            return null;
        }
        
        Jsonb jsb = getInstance().getContext(type);
        T object = (T) jsb.fromJson(string, type);
        return object;
    }
    
    public static <T> T unmarshall(InputStream is, Class<T> type) throws JAXBException {
        if (is==null) {
            return null;
        }
        
        Jsonb jsb = getInstance().getContext(type);
        T object = (T) jsb.fromJson(is, type);
        return object;
    }
    

}
