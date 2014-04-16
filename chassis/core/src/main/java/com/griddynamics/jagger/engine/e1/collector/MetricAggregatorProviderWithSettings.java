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

package com.griddynamics.jagger.engine.e1.collector;

import com.griddynamics.jagger.util.TimeUnits;

/**
 * Wrapper of MetricAggregatorProvider with specified settings
 */
public class MetricAggregatorProviderWithSettings {

    private MetricAggregatorProvider aggregatorProvider;
    private Settings settings = Settings.EMPTY_SETTINGS;

    public MetricAggregatorProviderWithSettings(MetricAggregatorProvider aggregatorProvider, Settings settings) {
        this.aggregatorProvider = aggregatorProvider;
        this.settings = settings;
    }

    public MetricAggregatorProviderWithSettings(MetricAggregatorProvider aggregatorProvider) {
        this.aggregatorProvider = aggregatorProvider;
    }

    public MetricAggregatorProviderWithSettings() {
    }

    public MetricAggregatorProvider getAggregatorProvider() {
        return aggregatorProvider;
    }

    public void setAggregatorProvider(MetricAggregatorProvider aggregatorProvider) {
        this.aggregatorProvider = aggregatorProvider;
    }

    public Settings getSettings() {
        return settings;
    }

    public void setSettings(Settings settings) {
        this.settings = settings;
    }

    public static class Settings {

        private static final Settings EMPTY_SETTINGS = new Settings();

        private TimeUnits normalizationBy = TimeUnits.NONE;
        private int aggregationInterval = 0;
        private int pointsCount = 0;

        public TimeUnits getNormalizationBy() {
            return normalizationBy;
        }

        public int getAggregationInterval() {
            return aggregationInterval;
        }

        public int getPointsCount() {
            return pointsCount;
        }

        public void setNormalizationBy(TimeUnits normalizationBy) {
            this.normalizationBy = normalizationBy;
        }

        public void setAggregationInterval(int aggregationInterval) {
            this.aggregationInterval = aggregationInterval;
        }

        public void setPointsCount(int pointsCount) {
            this.pointsCount = pointsCount;
        }
    }
}