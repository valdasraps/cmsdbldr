package org.cern.cms.dbloader.manager.json;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.cern.cms.dbloader.model.condition.CondBase;

import java.io.IOException;

public class CondDeserializer extends JsonDeserializer<CondBase> {

    @Override
    public CondBase deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JsonProcessingException {
        
        return new CondBase() {
            
            private final JsonNode jsonNode = jsonParser.readValueAsTree();
            
            @Override
            public <T extends CondBase> T getDelegate(Class<T> clazz) throws Exception {
                ObjectMapper mapper = new ObjectMapper();
                return mapper.treeToValue(jsonNode, clazz);
            }
            
        };
        
    }
}
