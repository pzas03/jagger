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

package com.griddynamics.jagger.providers;

import com.google.common.base.Throwables;
import com.google.common.collect.AbstractIterator;
import com.griddynamics.jagger.exception.TechnicalException;
import com.griddynamics.jagger.providers.creators.ObjectCreator;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVStrategy;
import java.io.*;
import java.util.Iterator;
import java.util.NoSuchElementException;

/** Reads information from CSV files
 * @author Nikolay Musienko
 * @n
 * @par Details:
 * @details Reads data from CSV files and translates it to java objects.
 *
 * @ingroup Main_Providers_group */
public class CsvProvider<T> implements Iterable<T>, Serializable  {

    private String path;
    private CSVStrategy strategy = CSVStrategy.DEFAULT_STRATEGY;
    private boolean readHeader;
    private ObjectCreator<T> objectCreator;

    public String getPath() {
        return path;
    }

    /** Sets file name, which contains data
     * @author Nikolay Musienko
     * @n
     * @param path - full name of file */
    public void setPath(String path) {
        this.path = path;
    }

    public ObjectCreator<T> getObjectCreator() {
        return objectCreator;
    }

    /** Sets object creator
     * @author Nikolay Musienko
     * @n
     * @param objectCreator - translate data to java objects */
    public void setObjectCreator(ObjectCreator<T> objectCreator) {
        this.objectCreator = objectCreator;
    }

    public CSVStrategy getStrategy() {
        return strategy;
    }

    /** Sets CSV strategy
     * @author Nikolay Musienko
     * @n
     * @param strategy - apache CSV strategy, which say how to read data from CSV file */
    public void setStrategy(CSVStrategy strategy) {
        this.strategy = strategy;
    }

    public CsvProvider(String path) {
        this.path = path;
    }

    public boolean getReadHeader() {
        return readHeader;
    }

    public void setReadHeader(boolean readHeader) {
        this.readHeader = readHeader;
    }

    public CsvProvider() {
    }

    /** Returns iterator over created objects.
     * @author Nikolay Musienko
     * @n
     * @par Details:
     * @details Reads data from file, translates it to java objects, return iterator over this objects */
    public Iterator<T> iterator() {

        return new AbstractIterator<T>() {

            private CSVParser parser;

            {
                init();
            }

            private void init(){
                if (path == null) {
                    throw new TechnicalException("File path can't be NULL!");
                }
                try {
                    parser = new CSVParser(new BufferedReader(new FileReader(new File(path))), strategy);
                } catch (FileNotFoundException e) {
                    throw Throwables.propagate(e);
                }  if(readHeader) {
                    try {
                        objectCreator.setHeader(parser.getLine());
                    } catch (IOException e){
                        throw Throwables.propagate(e);
                    }
                }

            }

            @Override
            protected T computeNext() {
                try {
                    String[] strings = parser.getLine();
                    if(strings == null) {
                        return endOfData();
                    }
                    return objectCreator.createObject(strings);
                } catch (IOException e) {
                    throw Throwables.propagate(e);
                }
            }

        };
    }
}
