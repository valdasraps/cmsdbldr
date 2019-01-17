package org.cern.cms.dbloader.manager;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.cern.cms.dbloader.model.condition.ChannelBase;
import org.cern.cms.dbloader.model.condition.ChannelBaseJson;

import java.io.IOException;

public class ChannelDeserilizerMock extends JsonDeserializer<ChannelBase> {

    @Override
    public ChannelBase deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JsonProcessingException {

        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        ChannelBase cb = null;
        try {
            cb = mapper.readerFor(ChannelBaseJson.class).readValue(jsonParser);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return cb;
    }
}
