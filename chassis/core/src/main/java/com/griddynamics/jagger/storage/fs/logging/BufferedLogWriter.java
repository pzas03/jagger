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

import com.google.common.base.Preconditions;
import com.google.common.io.Closeables;
import com.griddynamics.jagger.storage.FileStorage;
import com.griddynamics.jagger.storage.Namespace;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.*;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author Alexey Kiselyov, Vladimir Shulga
 *         Date: 20.07.11
 */
public abstract class BufferedLogWriter implements LogWriter {

    private final Logger log = LoggerFactory.getLogger(BufferedLogWriter.class);
    private FileStorage fileStorage;
    private int flushSize;
    private ArrayBlockingQueue<Log> buffer;
    private ExecutorService executorService = Executors.newSingleThreadExecutor();

    {
        buffer = new ArrayBlockingQueue<Log>(5000);
        executorService.submit(new FileLogger());
    }

    @Override
    public void log(String sessionId, String dir, String kernelId, Serializable logEntry) {
        String path = Namespace.of(sessionId, dir, kernelId).toString();
        log(path, logEntry);
    }

    public void log(String path, Serializable logEntry) {
        Preconditions.checkNotNull(logEntry, "Null is not supported");
        try {
            buffer.put(new Log(path, logEntry));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public synchronized void flush() {
        HashMap<String, ArrayList<Serializable>> map =  new HashMap<String, ArrayList<Serializable>>();
        while (!buffer.isEmpty()){
            Log logEntry = buffer.poll();
            ArrayList<Serializable> list = map.get(logEntry.getPath());
            if (list != null){
                list.add(logEntry.getLogValue());
            }else{
                list = new ArrayList<Serializable>();
                list.add(logEntry.getLogValue());
                map.put(logEntry.getPath(), list);
            }
        }
        writeToStorage(map);
    }

    @Required
    public void setFileStorage(FileStorage fileStorage) {
        this.fileStorage = fileStorage;
    }

    @Required
    public void setFlushSize(int flushSize) {
        this.flushSize = flushSize;
    }

    private void collectByFlashSize(){
        HashMap<String, ArrayList<Serializable>> map =  new HashMap<String, ArrayList<Serializable>>();
        int size = 0;
        while (size < flushSize){
            try {
                Log logEntry = buffer.take();
                ArrayList<Serializable> list = map.get(logEntry.getPath());
                if (list != null){
                    list.add(logEntry.getLogValue());
                    size++;
                }else{
                    list = new ArrayList<Serializable>();
                    list.add(logEntry.getLogValue());
                    map.put(logEntry.getPath(), list);
                    size++;
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        writeToStorage(map);
    }

    private synchronized void writeToStorage(Map<String, ArrayList<Serializable>> map){
        long startTime = System.currentTimeMillis();

        for (String logFilePath : map.keySet()) {
            Collection<Serializable> fileQueue = map.get(logFilePath);
            if (fileQueue.isEmpty()) {
                continue;
            }
            OutputStream os = null;
            LogWriterOutput objectOutput = null;
            try {
                if (fileStorage.exists(logFilePath)) {
                    os = fileStorage.append(logFilePath);
                } else {
                    os = fileStorage.create(logFilePath);
                }
                os = new BufferedOutputStream(os);
                objectOutput = getOutput(os);
                for(Serializable serializable: fileQueue){
                    objectOutput.writeObject(serializable);
                }

            } catch (IOException e) {
                log.error(e.getMessage(), e);
            } finally {
                try {
                    Closeables.closeQuietly(objectOutput);
                    Closeables.close(os, true);
                } catch (IOException e) {
                    log.error(e.getMessage(), e);
                }
                log.debug("Flush queue finished. flushId {}. Flush took: {}", startTime, (System.currentTimeMillis() - startTime));
            }
        }
        map.clear();
    }

    private class FileLogger implements Runnable{

        @Override
        public void run() {
            while(!Thread.interrupted()){
                collectByFlashSize();
            }
        }
    }

    private class Log{
        private String path;
        private Serializable logValue;

        public Log(String path, Serializable logValue){
            this.path = path;
            this.logValue = logValue;
        }

        private String getPath() {
            return path;
        }

        private void setPath(String path) {
            this.path = path;
        }

        private Serializable getLogValue() {
            return logValue;
        }

        private void setLogValue(Serializable logValue) {
            this.logValue = logValue;
        }
    }
}
