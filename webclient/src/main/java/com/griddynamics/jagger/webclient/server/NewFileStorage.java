package com.griddynamics.jagger.webclient.server;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public interface NewFileStorage {

    boolean exists(String path) throws IOException;

    int fileLength(String path) throws IOException;

    OutputStream create(String path)  throws IOException;

    InputStream open(String path)  throws IOException;

    /**
     * Delete a file.
     *
     * @param path      the path to delete.
     * @return true if delete is successful else false.
     * @throws java.io.IOException
     */
    boolean delete(String path) throws IOException;
}
