package org.cern.cms.dbloader.manager.xml;

import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.cern.cms.dbloader.manager.ChannelDeserializer;
import org.cern.cms.dbloader.manager.CondDeserializer;
import org.cern.cms.dbloader.manager.JsonManager;
import org.cern.cms.dbloader.metadata.ChannelEntityHandler;
import org.cern.cms.dbloader.metadata.CondEntityHandler;
import org.cern.cms.dbloader.model.condition.ChannelBase;
import org.cern.cms.dbloader.model.condition.CondBase;
import org.cern.cms.dbloader.model.condition.Dataset;
import org.cern.cms.dbloader.model.serial.Root;

import java.io.File;
import java.io.IOException;

public class CondJsonManager extends JsonManager {

    ChannelEntityHandler cheh;
    CondEntityHandler coneh;

    public CondJsonManager(ChannelEntityHandler cheh, CondEntityHandler coneh) {
        this.cheh = cheh;
        this.coneh = coneh;
    }

    @Override
    public Root deserialize(File file) throws IOException {
        // TODO improve reliability
        ObjectMapper mapper = new ObjectMapper();
        SimpleModule module = new SimpleModule();
        JsonDeserializer<ChannelBase> chd = new ChannelDeserializer(cheh);
        JsonDeserializer<CondBase> asd = new CondDeserializer(coneh);
        module.addDeserializer(ChannelBase.class, chd);
        module.addDeserializer(CondBase.class, asd);
        mapper.registerModule(module);
        Root root = mapper.readerFor(Root.class).readValue(file);
        return root;
    }
}
