package de.codecentric.mule.assertobjectequals;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.IOUtils;
import org.mule.api.MuleEvent;
import org.mule.api.transformer.DataType;
import org.mule.api.transport.OutputHandler;

/**
 * Hold a copy of consumable payload and create a new consumable copy on demand. Caveat: Reads streams into memory,
 * which should be ok in test context.
 */
public class StreamSaver {
    interface PayloadCreator {
        Object createPayload(StreamSaver saver);
    }

    enum PayloadType implements PayloadCreator {
        INPUT_STREAM {
            @Override
            public Object createPayload(StreamSaver saver) {
                return new ByteArrayInputStream(saver.data);
            }
        },
        OUTPUT_HANDLER {
            @Override
            public Object createPayload(StreamSaver saver) {
                throw new UnsupportedOperationException("createPayload for OUTPUT_HANDLER");
            }
        },
        OTHER {
            @Override
            public Object createPayload(StreamSaver saver) {
                return saver.payload;
            }
        };

    }

    private PayloadType type;
    private DataType<?> dataType;
    private Object payload;
    private byte[] data;

    public StreamSaver(MuleEvent event, Object payload, DataType<?> dataType) throws IOException {
        this.dataType = dataType;
        if (payload instanceof InputStream) {
            type = PayloadType.INPUT_STREAM;
            InputStream is = (InputStream) payload;
            data = IOUtils.toByteArray(is);
            is.close();
        } else if (payload instanceof OutputHandler) {
            type = PayloadType.OUTPUT_HANDLER;
            OutputHandler oh = (OutputHandler) payload;
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            oh.write(event, bos);
            data = bos.toByteArray();
        } else {
            type = PayloadType.OTHER;
            this.payload = payload;
        }
    }

    public Object createPayloadCopy() {
        return type.createPayload(this);
    }

    public DataType<?> getDataType() {
        return dataType;
    }
}
