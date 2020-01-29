package org.cern.cms.dbloader.manager;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.*;

import com.fasterxml.jackson.databind.module.SimpleModule;
import org.cern.cms.dbloader.model.condition.ChannelBase;
import org.cern.cms.dbloader.model.condition.CondBase;
import org.cern.cms.dbloader.model.serial.Root;


import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import org.cern.cms.dbloader.manager.json.ChannelDeserializer;
import org.cern.cms.dbloader.manager.json.CondDeserializer;

public class JsonManager {

    private ObjectMapper getMapper() {
        
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        
        SimpleModule module = new SimpleModule();
        module.addDeserializer(CondBase.class, new CondDeserializer());
        module.addDeserializer(ChannelBase.class, new ChannelDeserializer());
        mapper.registerModule(module);

        return mapper;
    }
    
    public Root deserialize(File file) throws IOException {
        
        return getMapper().readerFor(Root.class).readValue(file);

    }

    public List<String> deserilizeRootArray(String data) throws IOException {
        
        ObjectMapper mapper = getMapper();
        
        Object[] list = mapper.readerFor(Object[].class).readValue(data);
        List<String> roots = new ArrayList<>();
        for (Object elmnt: list) {
            roots.add(mapper.writeValueAsString(((LinkedHashMap) elmnt)));
        }
        return roots;
    }

    public <T> String serialize(T bean) {
        try {
            return this.getMapper().writeValueAsString(bean);
        } catch (JsonProcessingException e) {
            e.printStackTrace(System.err);
        }
        return null;
    }

}
