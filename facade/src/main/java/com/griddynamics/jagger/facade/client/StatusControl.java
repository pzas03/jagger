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

package com.griddynamics.jagger.facade.client;

import com.google.gwt.core.client.GWT;
import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.types.ImageStyle;
import com.smartgwt.client.types.VerticalAlignment;
import com.smartgwt.client.widgets.Img;
import com.smartgwt.client.widgets.Label;
import com.smartgwt.client.widgets.layout.VLayout;

/**
 * User: dkotlyarov
 */
public class StatusControl extends VLayout {
    private Status status;
    private final Img image = new Img();
    private final Label text = new Label();

    public StatusControl(Status status, String contents) {
        this.status = status;

        setWidth100();
        setHeight100();
        setAlign(Alignment.CENTER);
        setAlign(VerticalAlignment.CENTER);

        image.setSrc(status.image);
        image.setImageType(ImageStyle.CENTER);
        image.setAlign(Alignment.CENTER);
        image.setValign(VerticalAlignment.CENTER);

        text.setContents(contents);
        text.setAlign(Alignment.CENTER);
        text.setValign(VerticalAlignment.CENTER);

        addMember(image);
        addMember(text);
        redraw();
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status, String contents) {
        if (!status.equals(this.status)) {
            this.status = status;

            String hostPageBaseURL = GWT.getHostPageBaseURL();
            image.setSrc(hostPageBaseURL + status.image);
        }

        if (!contents.equals(text.getContents())) {
            text.setContents(contents);
        }
    }
}
