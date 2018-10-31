package info.ejava.examples.jaxrs.todos.dto;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.IOException;
import java.io.StringWriter;
import java.util.LinkedList;
import java.util.Map;
import java.util.stream.Collectors;

import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;
import javax.json.bind.JsonbConfig;
import javax.json.bind.config.PropertyNamingStrategy;

import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class JsonbTest extends MarshallingTest {
    private Logger logger = LoggerFactory.getLogger(JsonbTest.class);
    
    Jsonb builder;
            
    @Before
    public void setupJsonb() {
        JsonbConfig config=new JsonbConfig();
        //config.setProperty(JsonbConfig.FORMATTING, true);
        //config.setProperty(JsonbConfig.PROPERTY_NAMING_STRATEGY, PropertyNamingStrategy.LOWER_CASE_WITH_UNDERSCORES);
        //config.setProperty(JsonbConfig.NULL_VALUES, true); //helps us spot fields we don't want
        builder = JsonbBuilder.create(config);
    }
    
    @Override
    protected <T> String marshal(T object) {
        if (object==null) { return ""; }
        
        String buffer = builder.toJson(object);
        logger.info("{} toJSON: {}", object, buffer);
        return buffer;        
    }

    @Override
    protected <T> T demarshal(Class<T> type, String buffer)  {
        T result = (T) builder.fromJson(buffer, type);
        logger.info("{} fromJSON: {}", buffer, result);
        return result;
    }
}
