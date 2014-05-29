package com.griddynamics.jagger.webclient.server;

import org.springframework.beans.factory.annotation.Required;
import org.springframework.web.HttpRequestHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;

/**
 * Download service.
 */
public class FileDownloadRequestHandler implements HttpRequestHandler {

    private NewFileStorage fileStorage;

    @Required
    public void setFileStorage(NewFileStorage fileStorage) {
        this.fileStorage = fileStorage;
    }

    @Override
    public void handleRequest(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        final int BUFFER = 1024 * 100;

        String fileKey = req.getParameter("fileKey");

        if (!fileStorage.exists(fileKey)) {
            String message = "File you asked not presented in Storage";
            resp.sendError(404, message);
            return;
        }

        InputStream inputStream = fileStorage.open(fileKey);
        int fileLength = fileStorage.fileLength(fileKey);

        resp.setContentType("application/octet-stream");
        resp.setHeader("Content-Disposition:", "attachment;filename=" + "\"" + fileKey + ".csv\"");
        OutputStream outputStream = resp.getOutputStream();

        resp.setBufferSize(BUFFER);
        resp.setContentLength(fileLength);

        int bytesRead;
        byte[] bytes = new byte[BUFFER];

        while ((bytesRead = inputStream.read(bytes)) != -1) {
            outputStream.write(bytes, 0, bytesRead);
        }

        inputStream.close();
        outputStream.close();

        // delete file if necessary
        fileStorage.delete(fileKey);
    }
}
