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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CumulativeMetricAggregatorProvider implements MetricAggregatorProvider {
    @Override
    public MetricAggregator provide() {
        return new CumulativeMetricAggregator();
    }

    private static class CumulativeMetricAggregator implements MetricAggregator<Number> {

        Logger log = LoggerFactory.getLogger(CumulativeMetricAggregator.class);

        // max value in current interval
        private Double currentValue = null;

        // max value in previous interval
        private Double previousValue = null;

        // very first value that appended to aggregator, to calculate value in first interval
        private Double veryFirstValue = null;

        // flag to determine whether getAggregated() method calls in first interval
        private boolean firstInterval = true;

        @Override
        public void append(Number calculated) {
            log.debug("append({})", calculated);

            if (veryFirstValue == null) {
                // remember very first value
                veryFirstValue = calculated.doubleValue();
            }

            if (currentValue == null) {
                currentValue = calculated.doubleValue();
            } else {
                currentValue = Math.max(currentValue, calculated.doubleValue());
            }
        }

        @Override
        public Number getAggregated() {
            // nothing was appended
            if (currentValue == null) {
                return null;
            }

            Number result;
            if (firstInterval) {
                // return true difference of max and min values in first interval
                result = currentValue - veryFirstValue;
            } else {
                result = currentValue - previousValue;
            }

            previousValue = currentValue;

            log.debug("getAggregated() = {}", result);
            return result;
        }

        @Override
        public void reset() {
            // that means that next 'append()' will be called for next interval
            firstInterval = false;
            currentValue = null;

            log.debug("reset()");
        }

        @Override
        public String getName() {
            return "cumulative";
        }

        @Override
        public String toString() {
            return "CumulativeMetricAggregator{" +
                    "currentValue=" + currentValue +
                    ", previousValue=" + previousValue +
                    ", veryFirstValue=" + veryFirstValue +
                    ", firstInterval=" + firstInterval +
                    '}';
        }
    }
}
