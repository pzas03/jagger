package com.griddynamics.jagger.webclient.server;

import com.griddynamics.jagger.dbapi.csv.PlotToCsvGenerator;
import com.griddynamics.jagger.dbapi.dto.*;
import com.griddynamics.jagger.webclient.client.DownloadService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;

import java.io.ByteArrayOutputStream;

public class DownloadServiceImpl implements DownloadService {


    private InMemoryFileStorage fileStorage;

    private Logger log = LoggerFactory.getLogger(DownloadServiceImpl.class);

    @Required
    public void setFileStorage(InMemoryFileStorage fileStorage) {
        this.fileStorage = fileStorage;
    }

    @Override
    public String createPlotCsvFile(PlotIntegratedDto plot) throws RuntimeException {
        try {

            String fileKey = plot.getPlotHeader().replaceAll(",", "_");

            if (fileStorage.exists(fileKey)) {
                // return same object
                return fileKey;
            }

            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

            // create csv file
            PlotToCsvGenerator.generateCsvFile(plot, byteArrayOutputStream);

            byte[] fileInBytes = byteArrayOutputStream.toByteArray();

            fileStorage.store(fileKey, fileInBytes);

            return fileKey;
        } catch (Exception e) {
            log.error("Errors while creating csv file for " + plot, e);
            throw new RuntimeException("Errors while creating csv file for " + plot, e);
        }
    }
}
