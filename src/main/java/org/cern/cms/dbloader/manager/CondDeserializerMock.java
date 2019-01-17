package org.cern.cms.dbloader.manager;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.cern.cms.dbloader.model.condition.CondBase;
import org.cern.cms.dbloader.model.condition.CondBaseJson;

import java.io.IOException;

public class CondDeserializerMock extends JsonDeserializer<CondBase> {

    @Override
    public CondBase deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        CondBase cb = null;
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        try {
            cb = mapper.readerFor(CondBaseJson.class).readValue(jsonParser);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return cb;
    }
}
