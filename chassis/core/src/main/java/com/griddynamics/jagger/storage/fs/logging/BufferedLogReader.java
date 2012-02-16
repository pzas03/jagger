/*
 * Copyright (c) 2010-2012 Grid Dynamics Consulting Services, Inc, All Rights Reserved
 * http://www.griddynamics.com
 *
 * This library is free software; you can redistribute it and/or modify it under the terms of
 * the GNU Lesser General Public License as published by the Free Software Foundation; either
 * version 2.1 of the License, or any later version.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package com.griddynamics.jagger.storage.fs.logging;

import com.caucho.hessian.io.Hessian2Input;
import com.google.common.base.Throwables;
import com.griddynamics.jagger.storage.FileStorage;
import com.griddynamics.jagger.storage.Namespace;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.Queue;

import static com.google.common.collect.Lists.newLinkedList;

public class BufferedLogReader implements LogReader {
    private FileStorage fileStorage;

    @Override
    public <T> FileReader<T> read(String sessionId, String logOwner, String kernelId, Class<T> clazz) {
        Namespace path = Namespace.of(sessionId, logOwner, kernelId);
        try {
            if (!fileStorage.exists(path.toString())) {
                throw new IllegalArgumentException("Path " + path + " doesn't exist");
            }
        } catch (IOException e) {
            throw Throwables.propagate(e);
        }

        InputStream in;
        try {
            in = fileStorage.open(path.toString());
        } catch (IOException e) {
            throw Throwables.propagate(e);
        }
        Hessian2Input hessian2Input = new Hessian2Input(in);
        final IteratorImpl<T> iterator = new IteratorImpl<T>(hessian2Input, clazz);
        return new FileReaderImpl<T>(iterator, in);
    }


    public void setFileStorage(FileStorage fileStorage) {
        this.fileStorage = fileStorage;
    }

    /*package*/ static class IteratorImpl<T> implements Iterator<T> {
        public static final int BUF_SIZE = 1;

        private final Hessian2Input input;
        private final Class clazz;

        private Queue<T> buffer = newLinkedList();
        private boolean loaded = false;

        public IteratorImpl(Hessian2Input input, Class clazz) {
            this.input = input;
            this.clazz = clazz;
        }

        @Override
        public boolean hasNext() {
            return !getBuffer().isEmpty();
        }

        @Override
        public T next() {
            return getBuffer().poll();
        }

        private Queue<T> getBuffer() {
            if (buffer.isEmpty()) {
                update();
            }
            return buffer;
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException("Read only iterator!");
        }

        private void update() {
            if (loaded) {
                return;
            }

            for (int i = 0; i < BUF_SIZE; i++) {
                try {
                    Object entry = input.readObject();
                    if (!clazz.isInstance(entry)) {
                        throw new IllegalStateException("entry" + entry + " is not instance of class " + clazz);
                    }
                    buffer.add((T) entry);
                } catch (EOFException e) {
                    loaded = true;
                    break;
                } catch (IOException e) {
                    throw Throwables.propagate(e);
                }
            }
        }
    }

    private static class FileReaderImpl<T> implements FileReader<T> {
        private final Iterator<T> iterator;
        private final InputStream inputStream;

        private FileReaderImpl(Iterator<T> iterator, InputStream inputStream) {
            this.iterator = iterator;
            this.inputStream = inputStream;
        }

        @Override
        public void close() {
            try {
                inputStream.close();
            } catch (IOException e) {
                throw Throwables.propagate(e);
            }
        }

        @Override
        public Iterator<T> iterator() {
            return iterator;
        }
    }
}
