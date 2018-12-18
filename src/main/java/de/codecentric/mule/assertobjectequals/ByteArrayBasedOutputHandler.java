package de.codecentric.mule.assertobjectequals;

import java.io.IOException;
import java.io.OutputStream;

import org.mule.api.MuleEvent;
import org.mule.api.transport.OutputHandler;

/**
 * {@link OutputHandler} backed by a <code>byte[]</code>.
 */
public class ByteArrayBasedOutputHandler implements OutputHandler {
    private byte[] data;

    /**
     * @param data
     *            Content, will be copied.
     */
    public ByteArrayBasedOutputHandler(byte[] data) {
        this.data = data.clone();
    }

    @Override
    public void write(MuleEvent event, OutputStream out) throws IOException {
        out.write(data);
    }

}
