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

package com.griddynamics.jagger.engine.e1.collector.limits;

public class Limit {
    String metricName = null;
    String limitDescription;
    //??? optional baseline or reference
    Double reference = -1D;
    Double LWL = 0D;
    Double UWL = 0D;
    Double LEL = 0D;
    Double UEL = 0D;

    //???
//    public Limit(String metricName, String limitDescription, Double reference, Double LWL, Double UWL, Double LEL, Double UEL) {
//        this.metricName = metricName;
//        this.limitDescription = limitDescription;
//        this.reference = reference;
//        this.LWL = LWL;
//        this.UWL = UWL;
//        this.LEL = LEL;
//        this.UEL = UEL;
//    }
//
//    public Limit(String metricName, Double reference, Double LWL, Double UWL, Double LEL, Double UEL) {
//        this.metricName = metricName;
//        this.reference = reference;
//        this.LWL = LWL;
//        this.UWL = UWL;
//        this.LEL = LEL;
//        this.UEL = UEL;
//    }

    public Limit() {}

    public String getMetricName() {
        return metricName;
    }

    public void setMetricName(String metricName) {
        this.metricName = metricName;
    }

    public String getLimitDescription() {
        return limitDescription;
    }

    public void setLimitDescription(String limitDescription) {
        this.limitDescription = limitDescription;
    }

    public Double getReference() {
        return reference;
    }

    public void setReference(Double reference) {
        this.reference = reference;
    }

    public Double getLWL() {
        return LWL;
    }

    public void setLWL(Double LWL) {
        this.LWL = LWL;
    }

    public Double getUWL() {
        return UWL;
    }

    public void setUWL(Double UWL) {
        this.UWL = UWL;
    }

    public Double getLEL() {
        return LEL;
    }

    public void setLEL(Double LEL) {
        this.LEL = LEL;
    }

    public Double getUEL() {
        return UEL;
    }

    public void setUEL(Double UEL) {
        this.UEL = UEL;
    }
}

