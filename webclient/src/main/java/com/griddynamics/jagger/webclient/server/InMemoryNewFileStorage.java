package com.griddynamics.jagger.webclient.server;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class InMemoryNewFileStorage implements NewFileStorage {

    private Map<String, ByteArray> store = new HashMap<String, ByteArray>();

    @Override
    public boolean exists(String path) throws IOException {
        return store.containsKey(path);
    }

    @Override
    public int fileLength(String path) throws IOException {
        return store.get(path).getBuff().length;
    }

    @Override
    public OutputStream create(String path) throws IOException {

        ByteArray byteArray = new ByteArray();
        store.put(path, byteArray);

        return new ExternalByteArrayOutputStream(byteArray);
    }

    @Override
    public InputStream open(String path) throws IOException {
        return new ByteArrayInputStream(store.get(path).getBuff());
    }

    @Override
    public boolean delete(String path) throws IOException {
        return null != store.remove(path);
    }

    /**
     * Wrapper of byte array
     */
    private static class ByteArray {

        private byte [] buff;

        public ByteArray() {
            this(32);
        }

        public ByteArray(int size) {
            if (size < 0) {
                throw new IllegalArgumentException("Negative initial size: "
                        + size);
            }

            this.buff = new byte[size];
        }

        public void setBuff(byte[] buff) {
            this.buff = buff;
        }

        public byte[] getBuff() {
            return buff;
        }
    }

    /**
     * ByteArrayOutputStream that write to external byte array
     * Note that it is total copy of ByteArrayOutputStream, except wrapped buffer.
     */
    private static class ExternalByteArrayOutputStream extends OutputStream {

        /**
         * The buffer where data is stored.
         */
        protected ByteArray byteArray;

        /**
         * The number of valid bytes in the buffer.
         */
        protected int count;

        public ExternalByteArrayOutputStream(ByteArray byteArray) {
            this.byteArray = byteArray;
        }

        /**
         * Writes the specified byte to this byte array output stream.
         *
         * @param   b   the byte to be written.
         */
        public synchronized void write(int b) {
            int newcount = count + 1;
            if (newcount > byteArray.getBuff().length) {
                byteArray.setBuff(Arrays.copyOf(byteArray.getBuff(), Math.max(byteArray.getBuff().length << 1, newcount)));
            }
            byteArray.getBuff()[count] = (byte)b;
            count = newcount;
        }

        /**
         * Writes <code>len</code> bytes from the specified byte array
         * starting at offset <code>off</code> to this byte array output stream.
         *
         * @param   b     the data.
         * @param   off   the start offset in the data.
         * @param   len   the number of bytes to write.
         */
        public synchronized void write(byte b[], int off, int len) {
            if ((off < 0) || (off > b.length) || (len < 0) ||
                    ((off + len) > b.length) || ((off + len) < 0)) {
                throw new IndexOutOfBoundsException();
            } else if (len == 0) {
                return;
            }
            int newcount = count + len;
            if (newcount > byteArray.getBuff().length) {
                byteArray.setBuff(Arrays.copyOf(byteArray.getBuff(), Math.max(byteArray.getBuff().length << 1, newcount)));
            }
            System.arraycopy(b, off, byteArray.getBuff(), count, len);
            count = newcount;
        }

        /**
         * Returns the current size of the buffer.
         *
         * @return  the value of the <code>count</code> field, which is the number
         *          of valid bytes in this output stream.
         * @see     java.io.ByteArrayOutputStream#count
         */
        public synchronized int size() {
            return count;
        }

        /**
         * Converts the buffer's contents into a string decoding bytes using the
         * platform's default character set. The length of the new <tt>String</tt>
         * is a function of the character set, and hence may not be equal to the
         * size of the buffer.
         *
         * <p> This method always replaces malformed-input and unmappable-character
         * sequences with the default replacement string for the platform's
         * default character set. The {@linkplain java.nio.charset.CharsetDecoder}
         * class should be used when more control over the decoding process is
         * required.
         *
         * @return String decoded from the buffer's contents.
         * @since  JDK1.1
         */
        public synchronized String toString() {
            return new String(byteArray.getBuff(), 0, count);
        }

        /**
         * Closing a <tt>ByteArrayOutputStream</tt> has no effect. The methods in
         * this class can be called after the stream has been closed without
         * generating an <tt>IOException</tt>.
         * <p>
         *
         */
        public void close() throws IOException {
        }

    }
}
