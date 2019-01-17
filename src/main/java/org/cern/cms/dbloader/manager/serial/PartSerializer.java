package org.cern.cms.dbloader.manager.serial;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import org.cern.cms.dbloader.model.construct.Part;

import java.io.IOException;

public class PartSerializer extends StdSerializer<Part>
{
    public PartSerializer() {
        this(null);
    }

    protected PartSerializer(Class<Part> t) {
        super(t);
    }

    @Override
    public void serialize(Part part, JsonGenerator gen, SerializerProvider serializerProvider) throws IOException {
        // gen.writeString("CHILDREN", part.getChildren());
        gen.writeStartArray();
        gen.writeString("Hello");
        gen.writeObject(new Part());
        gen.writeEndArray();
        System.out.println();
    }
}
