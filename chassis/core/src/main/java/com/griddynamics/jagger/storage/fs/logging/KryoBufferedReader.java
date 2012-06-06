package com.griddynamics.jagger.storage.fs.logging;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;

import java.io.IOException;
import java.io.InputStream;

public class KryoBufferedReader extends BufferedLogReader {
    @Override
    protected LogReaderInput getInput(InputStream in) {
        return new KryoLogReaderInput(in);
    }

    private class KryoLogReaderInput implements LogReaderInput {

        private Input input;

        private Kryo kryo = new Kryo();

        private KryoLogReaderInput(InputStream in) {
            this.input = new Input(in);
        }

        @Override
        public Object readObject() throws IOException {
            return kryo.readClassAndObject(input);
        }
    }
}
