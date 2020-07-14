package org.cern.cms.dbloader.manager.json;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import org.cern.cms.dbloader.model.construct.PartDetailsBase;

public class PartDetailsDeserializer extends JsonDeserializer<PartDetailsBase> {

    @Override
    public PartDetailsBase deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JsonProcessingException {

        return new PartDetailsBase() {
            
            private final JsonNode jsonNode = jsonParser.readValueAsTree();
            
            @Override
            public <T extends PartDetailsBase> T getDelegate(Class<T> clazz) throws Exception {
                ObjectMapper mapper = new ObjectMapper();
                return mapper.treeToValue(jsonNode, clazz);
            }
            
        };

    }
}
