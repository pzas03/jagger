package com.griddynamics.jagger.dbapi.csv;

import com.griddynamics.jagger.dbapi.dto.PlotDatasetDto;
import com.griddynamics.jagger.dbapi.dto.PlotSeriesDto;
import com.griddynamics.jagger.dbapi.dto.PointDto;
import org.supercsv.io.CsvListWriter;
import org.supercsv.prefs.CsvPreference;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.Collection;

public class PlotToCsvGenerator {


    /**
     * Write csv file to OutputStream. OutputStream will be automatically closed.
     * @param plot Plot to represent in csv
     * @param outputStream stream to write csv to
     * @throws IOException
     */
    public static void generateCsvFile(PlotSeriesDto plot, OutputStream outputStream) throws IOException {

        Collection<PlotDatasetDto> curves = plot.getPlotSeries();

        OutputStreamWriter osw = new OutputStreamWriter(outputStream);

        CsvListWriter writer = new CsvListWriter(osw, CsvPreference.STANDARD_PREFERENCE);

        try {
            String[] header = new String[curves.size() + 1]; // one for xAxis label
            header[0] = plot.getXAxisLabel();
            int i = 1;
            for (PlotDatasetDto curve : curves) {
                header[i++] = curve.getLegend();
            }

            writer.writeHeader(header);

            i = 1; // first curve to first column
            for (PlotDatasetDto curve : curves) {

                for (PointDto point : curve.getPlotData()) {
                    Double[] raw = new Double[curves.size() + 1]; // one for xAxis label
                    raw[0] = point.getX();
                    raw[i] = point.getY();

                    writer.write(raw);
                }
                i++;
            }
        } finally {
            writer.flush();
            writer.close();
        }
    }
}
