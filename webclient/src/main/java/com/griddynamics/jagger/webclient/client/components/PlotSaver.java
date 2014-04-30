package com.griddynamics.jagger.webclient.client.components;

import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.dom.client.ImageElement;
import com.google.gwt.user.client.Window;
import com.googlecode.gflot.client.Series;
import com.googlecode.gflot.client.SimplePlot;

/**
 * Class that enables to save plots */
public class PlotSaver {

    /**
     * Saves plot as png
     * @param plot plot to be saved
     * @param plotHeader plot header
     * @param xAxisLabel X axis label
     */
    public void saveAsPng (SimplePlot plot, String plotHeader, String xAxisLabel) {
        saveAsPng(plot, plotHeader, xAxisLabel, 2);
    }

    /**
     * Saves plot as png
     * @param plot plot to be saved
     * @param plotHeader plot header
     * @param xAxisLabel X axis label
     * @param numberOfColumns number of columns in legend
     */
    public void saveAsPng (SimplePlot plot, String plotHeader, String xAxisLabel, int numberOfColumns) {
        if (plot.isExportAsImageEnabled()) {

            ImageElement imageElement = ImageElement.as(plot.getImage().getElement());
            int plotWidth = imageElement.getWidth();
            int plotHeight = imageElement.getHeight();
            int fontSize = 13;
            int delta = 5;


            JsArray<Series> series = plot.getModel().getSeries();
            int legendHeight = calculateLegendHeight(series, numberOfColumns, fontSize, delta);

            int totalHeight = plotHeight + 2 * fontSize + 3 * delta + legendHeight;

            Canvas canvasTmp = Canvas.createIfSupported();

            canvasTmp.setWidth(plotWidth + "px");
            canvasTmp.setHeight(totalHeight +  "px");
            canvasTmp.setCoordinateSpaceWidth(plotWidth);
            canvasTmp.setCoordinateSpaceHeight(totalHeight);
            Context2d context = canvasTmp.getContext2d();

            int currentY = 0;
            context.setFillStyle("black");
            context.setFont("bold " + fontSize + "px sans-serif");
            context.setTextAlign(Context2d.TextAlign.START);

            currentY += fontSize + delta;
            // add plot header
            context.fillText(plotHeader, delta, currentY);

            currentY += delta;
            // add image of plot
            context.drawImage(imageElement, 0.0, currentY, imageElement.getWidth(), imageElement.getHeight());

            currentY += plotHeight + delta;
            context.setFont(fontSize + "px sans-serif");
            context.setTextAlign(Context2d.TextAlign.CENTER);
            // add x axis label
            context.fillText(xAxisLabel, imageElement.getWidth() / 2, currentY + fontSize / 2);

            currentY += fontSize + delta;

            // calculate max width of legend label to avoid overlay
            int maxWidth = calculateLabelMaxWidth(series, context, fontSize);

            int currentX = 0;
            for (int i = 0; i < series.length(); i++) {

                Series s = series.get(i);
                int recW = (int)(fontSize * 1.5);
                int recH = fontSize;

                if (i % numberOfColumns == 0 ) {
                    currentX = delta;
                } else {
                    currentX += (maxWidth + 2 * fontSize + 3 * delta);
                }

                // add rectangle with color
                context.setFillStyle("gray");
                context.fillRect(currentX, currentY, recW, recH);
                context.setFillStyle("white");
                recW -= 2;
                recH -= 2;
                context.fillRect(currentX + 1, currentY + 1, recW, recH);
                context.setFillStyle(s.getColor());
                recW -= 2;
                recH -= 2;
                context.fillRect(currentX + 2, currentY + 2, recW, recH);
                context.setFillStyle("black");
                context.setTextAlign(Context2d.TextAlign.START);
                // add label of current series
                context.fillText(s.getLabel(), currentX + delta * 2 + fontSize * 1.5, currentY + fontSize - 2);

                if (i % numberOfColumns == numberOfColumns - 1) {
                    currentY += fontSize + delta;
                }
            }

            // get url of png data created from canvas
            String url = canvasTmp.toDataUrl("image/png");
            // fire browser event to download png
            Window.Location.assign(url.replace("image/png", "image/octet-stream"));

        } else {
            new ExceptionPanel("Can not save image in your browser.");
        }
    }

    private int calculateLegendHeight(JsArray<Series> series, int numberOfColumns, int fontSize, int delta) {

        int legendHeight = 0;
        for (int i = 0; i < series.length(); i++) {
            if (i % numberOfColumns == 0)
                legendHeight += fontSize + delta;
        }
        return legendHeight;
    }

    private int calculateLabelMaxWidth(JsArray<Series> series, Context2d context, int fontSize) {

        context.setFont(fontSize + "px sans-serif");
        int maxWidth = 0;
        for (int i = 0; i < series.length(); i++) {

            Series s = series.get(i);
            double width = context.measureText(s.getLabel()).getWidth();
            if (maxWidth < width)
                maxWidth = (int)width;
        }

        return maxWidth;
    }
}
