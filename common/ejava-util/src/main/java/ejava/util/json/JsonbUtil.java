package ejava.util.json;

import java.io.InputStream;

import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;
import javax.json.bind.JsonbConfig;
import javax.json.bind.config.PropertyNamingStrategy;

public class JsonbUtil {
    static Jsonb getContext(Class<?> type) {
        JsonbConfig config=new JsonbConfig();
        //config.setProperty(JsonbConfig.FORMATTING, true);
        config.setProperty(JsonbConfig.PROPERTY_NAMING_STRATEGY, PropertyNamingStrategy.LOWER_CASE_WITH_UNDERSCORES);
        //config.setProperty(JsonbConfig.NULL_VALUES, true); //helps us spot fields we don't want
        return JsonbBuilder.create(config);
    }

    public static <T> String marshal(T object) {
        if (object==null) {
            return "";
        }
        
        Jsonb jsb = getContext(object.getClass());
        String jsonString = jsb.toJson(object);
        return jsonString;        
    }
    
    public static <T> T unmarshal(String string, Class<T> type) {
        if (string==null || string.isEmpty()) {
            return null;
        }
        
        Jsonb jsb = getContext(type);
        T object = (T) jsb.fromJson(string, type);
        return object;
    }
    
    public static <T> T unmarshall(InputStream is, Class<T> type) {
        if (is==null) {
            return null;
        }
        
        Jsonb jsb = getContext(type);
        T object = (T) jsb.fromJson(is, type);
        return object;
    }
}