package com.griddynamics.jagger.storage.fs.logging;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Output;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.Collection;

public class KryoBufferedLogWriter extends BufferedLogWriter {

    @Override
    protected void log(Collection<Serializable> fileQueue, OutputStream os) throws IOException {
        Kryo kryo = new Kryo();
        Output output = new Output(os);
        try {
            for (Serializable logEntry : fileQueue) {
                kryo.writeClassAndObject(output, logEntry);
            }
        } finally {
            output.close();
        }
    }
}
