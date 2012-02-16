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

import com.griddynamics.jagger.facade.client.navigation.FrameDTO;
import net.sf.jasperreports.engine.JRPrintElement;
import net.sf.jasperreports.engine.JRPrintFrame;
import net.sf.jasperreports.engine.JRPrintText;
import net.sf.jasperreports.engine.fill.JRTemplateFrame;
import net.sf.jasperreports.engine.fill.JRTemplatePrintFrame;
import net.sf.jasperreports.engine.fill.JRTemplatePrintText;
import net.sf.jasperreports.engine.fill.JRTemplateText;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 * User: dkotlyarov
 */
public class Frame {
    private final int index;
    private final String name;
    private final JRTemplatePrintText namePrintText;
    private final JRTemplateFrame templateFrame;
    private final JRPrintFrame printFrame;
    private final int childY;
    private final ArrayList<Integer> childYs = new ArrayList<Integer>(16);
    private final ArrayList<Frame> childFrames = new ArrayList<Frame>();

    public Frame(int index, JRPrintFrame printFrame) throws FrameException {
        List elements = printFrame.getElements();
        if (elements.isEmpty()) {
            throw new FrameException();
        }

        JRTemplatePrintText namePrintText = null;
        for (Object childElement : elements) {
            if (childElement instanceof JRTemplatePrintText) {
                namePrintText = (JRTemplatePrintText) childElement;
                break;
            }
        }
        if (namePrintText == null) {
            throw new FrameException();
        }

        this.index = index;
        this.name = namePrintText.getText();
        this.namePrintText = namePrintText;
        this.templateFrame = (JRTemplateFrame) ((JRTemplatePrintFrame) printFrame).getTemplate();
        this.printFrame = printFrame;
        this.childY = ((JRPrintElement) elements.get(0)).getY();

        for (Object element : elements) {
            childYs.add(((JRPrintElement) element).getY());
            if (element instanceof JRPrintFrame) {
                JRPrintFrame childPrintFrame = (JRPrintFrame) element;
                childFrames.add(new Frame(childFrames.size(), childPrintFrame));
            }
        }
    }

    public int getIndex() {
        return index;
    }

    public String getName() {
        return name;
    }

    public JRTemplateFrame getTemplateFrame() {
        return templateFrame;
    }

    public JRPrintFrame getPrintFrame() {
        return printFrame;
    }

    public Frame getChildFrame(int index) {
        return childFrames.get(index);
    }

    public JRPrintFrame createPrintFrame(String headerText, int x, int y) {
        JRTemplatePrintFrame newPrintFrame = new JRTemplatePrintFrame(templateFrame);
        newPrintFrame.setX(x);
        newPrintFrame.setY(y);
        newPrintFrame.setWidth(printFrame.getWidth());
        newPrintFrame.setHeight(0);

        JRTemplatePrintText frameHeader = null;
        if (headerText != null) {
            frameHeader = new JRTemplatePrintText((JRTemplateText) namePrintText.getTemplate());
            frameHeader.setX(0);
            frameHeader.setY(0);
            frameHeader.setWidth(printFrame.getWidth());
            frameHeader.setHeight(40);
            if (headerText.length() <= 117) {
                frameHeader.setText(headerText);
            } else {
                frameHeader.setText(headerText.substring(0, 117));
            }
            newPrintFrame.addElement(frameHeader);
        }

        int frameHeight = 0;
        Iterator<Integer> childYIt = childYs.iterator();
        for (Object childElement : printFrame.getElements()) {
            JRPrintElement childPrintElement = (JRPrintElement) childElement;
            childPrintElement.setY(childYIt.next() - childY + ((frameHeader != null) ? frameHeader.getHeight() : 0));
            int bottom = childPrintElement.getY() + childPrintElement.getHeight();
            if (bottom > frameHeight) {
                frameHeight = bottom;
                newPrintFrame.setHeight(frameHeight);
            }
            newPrintFrame.addElement(childPrintElement);
        }
        return newPrintFrame;
    }

    public FrameDTO toDTO() {
        return new FrameDTO(index, name, toDTOs(childFrames));
    }

    public static FrameDTO[] toDTOs(Collection<Frame> frames) {
        FrameDTO[] frameDTOs = new FrameDTO[frames.size()];
        int i = 0;
        for (Frame frame : frames) {
            frameDTOs[i++] = frame.toDTO();
        }
        return frameDTOs;
    }
}
