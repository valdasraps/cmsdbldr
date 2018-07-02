package org.cern.cms.dbloader.manager;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;

import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.apache.commons.collections.bag.SynchronizedSortedBag;
import org.cern.cms.dbloader.manager.serial.PartSerializer;
import org.cern.cms.dbloader.model.construct.Part;
import org.cern.cms.dbloader.model.serial.Root;
import org.codehaus.jackson.map.jsontype.impl.TypeNameIdResolver;


import java.io.File;
import java.io.IOException;

public class JsonManager {

    private ObjectMapper mapper;

    public JsonManager() {
        this.mapper = new ObjectMapper();
    }

    public Root deserialize(File file) throws IOException {
        Root root = null;
        // this.mapper.configure(DeserializationFeature.UNWRAP_ROOT_VALUE, true);
            // mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, true);
            // mapper.reader(DeserializationFeature.UNWRAP_ROOT_VALUE);
        root = this.mapper.readerFor(Root.class).readValue(file);
        return root;
    }

    /**
     * Serializes bean to JSON string
     *
     * @param bean to serialize
     * @return bean as JSON string
     */
    public <T> String serialiaze(T bean) {
        String jsonStr = null;
        // this.mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, true);
        try {
            jsonStr = this.mapper.writeValueAsString(bean);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return jsonStr;
    }

}
