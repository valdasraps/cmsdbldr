package org.cern.cms.dbloader.manager;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.cern.cms.dbloader.metadata.ChannelEntityHandler;
import org.cern.cms.dbloader.model.condition.ChannelBase;

import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSetMetaData;
import java.util.HashMap;
import java.util.Map;

public class ChannelDeserializer extends JsonDeserializer<ChannelBase> {

    protected ChannelEntityHandler ceh;

    public ChannelDeserializer(ChannelEntityHandler cheh) {
        this.ceh = cheh;
    }

    @Override
    public ChannelBase deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JsonProcessingException {

        ObjectMapper mapper = new ObjectMapper();
        ChannelBase cb = null;
        try {
            cb = mapper.readerFor(ceh.getEntityClass().getC()).readValue(jsonParser);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return cb;
    }
}
