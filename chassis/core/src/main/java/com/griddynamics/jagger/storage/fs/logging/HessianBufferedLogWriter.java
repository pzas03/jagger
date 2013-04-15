package com.griddynamics.jagger.storage.fs.logging;

import com.caucho.hessian.io.Hessian2Output;
import org.jboss.serial.io.JBossObjectOutputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.Collection;

public class HessianBufferedLogWriter extends BufferedLogWriter {

    private final Logger log = LoggerFactory.getLogger(HessianBufferedLogWriter.class);

    private static class HessianLogWriterOutput implements LogWriterOutput {
        private final Hessian2Output out;

        private HessianLogWriterOutput(OutputStream out) throws IOException {
            this.out = new Hessian2Output(out);
        }

        @Override
        public void writeObject(Object object) throws IOException {
            out.writeObject(object);
        }

        @Override
        public void close() throws IOException {
            out.close();
        }
    }

    @Override
    public LogWriterOutput getOutput(OutputStream out) throws IOException {
        return new HessianLogWriterOutput(out);
    }

    protected void log(Collection<Serializable> fileQueue, OutputStream os) throws IOException {
        Hessian2Output objectStream;
        objectStream = new Hessian2Output(os);
        try {
            for (Serializable logEntry : fileQueue) {
                objectStream.writeObject(logEntry);
            }
        } finally {
            try {
                objectStream.close();
            } catch (IOException e) {
                log.error(e.getMessage(), e);
            }
        }
    }
}
