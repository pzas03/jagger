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

package com.griddynamics.jagger.facade;

import com.griddynamics.jagger.engine.e1.aggregator.session.model.SessionData;
import com.griddynamics.jagger.facade.client.navigation.ReportDTO;
import com.griddynamics.jagger.master.SessionIdProviderBean;
import com.griddynamics.jagger.reporting.ReportingService;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.base.JRBasePrintPage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;

import java.io.File;
import java.util.ArrayList;
import java.util.UUID;

/**
 * User: dkotlyarov
 */
public class Report {
    private static final Logger log = LoggerFactory.getLogger(Report.class);

    private final Context context;
    private final SessionData sessionData;
    private final JasperPrint print;
    private final JasperPrint framePrint;
    private final ArrayList<Frame> frames = new ArrayList<Frame>();

    public Report(Context context, SessionData sessionData) {
        this.context = context;
        this.sessionData = sessionData;

        ApplicationContext applicationContext = context.getApplicationContext();
        SessionIdProviderBean sessionIdProvider = applicationContext.getBean("sessionIdProvider", SessionIdProviderBean.class);
        sessionIdProvider.setSessionId(sessionData.getSessionId());
        ReportingService reportingService = applicationContext.getBean("reportingService", ReportingService.class);

        this.print = reportingService.generateReport(true);
        this.framePrint = reportingService.generateReport(false);

        JRPrintPage printPage = (JRPrintPage) framePrint.getPages().get(0);
        for (Object element : printPage.getElements()) {
            if (element instanceof JRPrintFrame) {
                JRPrintFrame printFrame = (JRPrintFrame) element;
                try {
                    frames.add(new Frame(frames.size(), printFrame));
                } catch (FrameException e) {
                }
            }
        }
    }

    public Context getContext() {
        return context;
    }

    public SessionData getSessionData() {
        return sessionData;
    }

    public JasperPrint getPrint() {
        return print;
    }

    public JasperPrint getFramePrint() {
        return framePrint;
    }

    public Frame getFrame(int index) {
        return frames.get(index);
    }

    public ArrayList<Frame> getFrames() {
        return frames;
    }

    public Frame getFrame(ArrayList<Integer> framePath) {
        Frame frame = frames.get(framePath.get(0));
        for (int i = 1, ci = framePath.size(); i < ci; ++i) {
            frame = frame.getChildFrame(framePath.get(i));
        }
        return frame;
    }

    public String getFrameDirectory(ArrayList<Integer> framePath) {
        if (framePath.size() > 1) {
            Frame frame = frames.get(framePath.get(0));
            String frameDirectory = frame.getName();
            for (int i = 1, ci = framePath.size() - 1; i < ci; ++i) {
                frame = frame.getChildFrame(framePath.get(i));
                frameDirectory += " | ";
                frameDirectory += frame.getName();
            }
            return frameDirectory;
        } else {
            return "";
        }
    }

    public int getFrameX(ArrayList<Integer> framePath) {
        Frame frame = frames.get(framePath.get(0));
        int x = frame.getPrintFrame().getX();
        for (int i = 1, ci = framePath.size(); i < ci; ++i) {
            frame = frame.getChildFrame(framePath.get(i));
            x += frame.getPrintFrame().getX();
        }
        return x;
    }

    public JasperPrint createEmptyPrint() {
        try {
            JasperPrint newPrint = new JasperPrint();
            newPrint.setName(framePrint.getName());
            newPrint.setOrientation(framePrint.getOrientationValue());
            newPrint.setPageWidth(framePrint.getPageWidth());
            newPrint.setPageHeight(framePrint.getPageHeight());
            newPrint.setLeftMargin(framePrint.getLeftMargin());
            newPrint.setTopMargin(framePrint.getTopMargin());
            newPrint.setRightMargin(framePrint.getRightMargin());
            newPrint.setBottomMargin(framePrint.getBottomMargin());
            newPrint.setLocaleCode(framePrint.getLocaleCode());
            newPrint.setFormatFactoryClass(framePrint.getFormatFactoryClass());
            newPrint.setTimeZoneId(framePrint.getTimeZoneId());

            for (JRReportFont font : framePrint.getFonts()) {
                newPrint.addFont(font);
            }
            newPrint.setDefaultFont(framePrint.getDefaultFont());

            for (JRStyle style : framePrint.getStyles()) {
                newPrint.addStyle(style);
            }
            newPrint.setDefaultStyle(framePrint.getDefaultStyle());

            for (JROrigin origin : framePrint.getOrigins()) {
                newPrint.addOrigin(origin);
            }

            return newPrint;
        } catch (JRException e) {
            throw new RuntimeException(e);
        }
    }

    public JasperPrint createPrint() {
        JasperPrint newPrint = createEmptyPrint();
        newPrint.setPageHeight(0);
        newPrint.addPage(new JRBasePrintPage());
        return newPrint;
    }

    public void fillPrint(JasperPrint newPrint, ArrayList<ArrayList<Integer>> frameIndexes) {
        String sessionName = "Session-" + sessionData.getSessionId() + " [" + sessionData.getStartTime() + "] of " + context.getLocation().getName();
        JRPrintPage printPage = (JRPrintPage) newPrint.getPages().get(0);
        int y = newPrint.getPageHeight() + 20;
        ArrayList<Integer> prevFramePath = null;
        for (ArrayList<Integer> framePath : frameIndexes) {
            String headerText = null;
            if (!equalParentFrames(prevFramePath, framePath)) {
                String frameDirectory = getFrameDirectory(framePath);
                headerText = frameDirectory.isEmpty() ? sessionName : sessionName + ": " + frameDirectory;
            }

            JRPrintFrame printFrame = getFrame(framePath).createPrintFrame(headerText, getFrameX(framePath), y);
            printPage.addElement(printFrame);
            y += printFrame.getHeight() + 20;

            prevFramePath = framePath;
        }
        newPrint.setPageHeight(y);
    }

    public ReportDTO toDTO() {
        return new ReportDTO(Frame.toDTOs(frames));
    }

    public static boolean equalParentFrames(ArrayList<Integer> framePath1, ArrayList<Integer> framePath2) {
        if ((framePath1 != null) && (framePath2 != null) && (framePath1.size() == framePath2.size())) {
            for (int i = 0, ci = framePath1.size() - 1; i < ci; ++i) {
                if (!framePath1.get(i).equals(framePath2.get(i))) {
                    return false;
                }
            }
            return true;
        } else {
            return false;
        }
    }

    public static String export(User user, JasperPrint print) {
        try {
            String contentDirectory = User.getConfig().getContentDirectory();
            String userDirectory = user.getId() + "/";
            String directory = contentDirectory + "report/" + userDirectory;

            File directoryFile = new File(directory);
            if (directoryFile.mkdirs()) {
                log.info("Directory {} is created", directoryFile.getAbsolutePath());
            }

            File[] files = directoryFile.listFiles();
            for (File file : files) {
                file.delete();
            }

            String id = UUID.randomUUID().toString();
            JasperExportManager.exportReportToPdfFile(print, directory + "report-" + id + ".pdf");
            return "report/" + userDirectory + "report-" + id + ".pdf";
        } catch (JRException e) {
            throw new RuntimeException(e);
        }
    }
}
