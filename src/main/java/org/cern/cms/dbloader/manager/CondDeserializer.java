package org.cern.cms.dbloader.manager;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.cern.cms.dbloader.metadata.ChannelEntityHandler;
import org.cern.cms.dbloader.metadata.CondEntityHandler;
import org.cern.cms.dbloader.model.condition.ChannelBase;
import org.cern.cms.dbloader.model.condition.CondBase;

import java.io.IOException;

public class CondDeserializer extends JsonDeserializer<CondBase> {

    protected CondEntityHandler coneh;

    public CondDeserializer(CondEntityHandler coneh) {
        this.coneh = coneh;
    }

    @Override
    public CondBase deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        CondBase cb = null;
        try {
            // or ceh.getEntityClass().getC()
            cb = mapper.readerFor(coneh.getEntityClass().getC()).readValue(jsonParser);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return cb;
    }
}
