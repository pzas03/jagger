package com.griddynamics.jagger.storage.fs.logging;

import org.jboss.serial.io.JBossObjectInputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;

public class JBossBufferedLogReader extends BufferedLogReader {

    private static class JBossLogReaderInput implements LogReaderInput {

        private final Logger log = LoggerFactory.getLogger(JBossBufferedLogReader.class);

        private JBossObjectInputStream in;

        private JBossLogReaderInput(InputStream is) {
            try {
                in = new JBossObjectInputStream(is);
            } catch (IOException e) {
                log.error("", e); // TODO
            }
        }

        @Override
        public Object readObject() throws IOException {
            try {
                Object result = in.readObject();
                return result; 
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        }
    }

    protected LogReaderInput getInput(InputStream in) {
        return new JBossLogReaderInput(in);
    }
}
