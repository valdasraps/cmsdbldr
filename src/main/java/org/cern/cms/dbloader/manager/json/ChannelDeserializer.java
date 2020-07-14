package org.cern.cms.dbloader.manager.json;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.cern.cms.dbloader.model.condition.ChannelBase;

import java.io.IOException;

public class ChannelDeserializer extends JsonDeserializer<ChannelBase> {

    @Override
    public ChannelBase deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JsonProcessingException {

        return new ChannelBase() {
            
            private final JsonNode jsonNode = jsonParser.readValueAsTree();
            
            @Override
            public <T extends ChannelBase> T getDelegate(Class<T> clazz) throws Exception {
                ObjectMapper mapper = new ObjectMapper();
                return mapper.treeToValue(jsonNode, clazz);
            }
            
        };

    }
}
