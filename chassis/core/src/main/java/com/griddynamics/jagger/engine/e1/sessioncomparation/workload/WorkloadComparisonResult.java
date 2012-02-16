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
package com.griddynamics.jagger.engine.e1.sessioncomparation.workload;

import com.google.common.base.Objects;

public class WorkloadComparisonResult {
    private final double throughputDeviation;
    private final double totalDurationDeviation;
    private final double successRateDeviation;
    private final double avgLatencyDeviation;
    private final double stdDevLatencyDeviation;

    public static WorkloadComparisonResultBuilder builder() {
        return new WorkloadComparisonResultBuilder();
    }

    private WorkloadComparisonResult(double throughputDeviation, double totalDurationDeviation, double successRateDeviation, double avgLatencyDeviation, double stdDevLatencyDeviation) {
        this.throughputDeviation = throughputDeviation;
        this.totalDurationDeviation = totalDurationDeviation;
        this.successRateDeviation = successRateDeviation;
        this.avgLatencyDeviation = avgLatencyDeviation;
        this.stdDevLatencyDeviation = stdDevLatencyDeviation;
    }

    public double getThroughputDeviation() {
        return throughputDeviation;
    }

    public double getTotalDurationDeviation() {
        return totalDurationDeviation;
    }

    public double getSuccessRateDeviation() {
        return successRateDeviation;
    }

    public double getAvgLatencyDeviation() {
        return avgLatencyDeviation;
    }

    public double getStdDevLatencyDeviation() {
        return stdDevLatencyDeviation;
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this)
                .add("throughputDeviation", throughputDeviation)
                .add("totalDurationDeviation", totalDurationDeviation)
                .add("successRateDeviation", successRateDeviation)
                .add("avgLatencyDeviation", avgLatencyDeviation)
                .add("stdDevLatencyDeviation", stdDevLatencyDeviation)
                .toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        WorkloadComparisonResult that = (WorkloadComparisonResult) o;

        if (Double.compare(that.avgLatencyDeviation, avgLatencyDeviation) != 0) return false;
        if (Double.compare(that.stdDevLatencyDeviation, stdDevLatencyDeviation) != 0) return false;
        if (Double.compare(that.successRateDeviation, successRateDeviation) != 0) return false;
        if (Double.compare(that.throughputDeviation, throughputDeviation) != 0) return false;
        if (Double.compare(that.totalDurationDeviation, totalDurationDeviation) != 0) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        temp = throughputDeviation != +0.0d ? Double.doubleToLongBits(throughputDeviation) : 0L;
        result = (int) (temp ^ (temp >>> 32));
        temp = totalDurationDeviation != +0.0d ? Double.doubleToLongBits(totalDurationDeviation) : 0L;
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = successRateDeviation != +0.0d ? Double.doubleToLongBits(successRateDeviation) : 0L;
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = avgLatencyDeviation != +0.0d ? Double.doubleToLongBits(avgLatencyDeviation) : 0L;
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = stdDevLatencyDeviation != +0.0d ? Double.doubleToLongBits(stdDevLatencyDeviation) : 0L;
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        return result;
    }

    public static class WorkloadComparisonResultBuilder {
        private double throughputDeviation;
        private double totalDurationDeviation;
        private double successRateDeviation;
        private double avgLatencyDeviation;
        private double stdDevLatencyDeviation;

        private WorkloadComparisonResultBuilder() {

        }

        public WorkloadComparisonResultBuilder throughputDeviation(double throughputDeviation) {
            this.throughputDeviation = throughputDeviation;
            return this;
        }

        public WorkloadComparisonResultBuilder totalDurationDeviation(double totalDurationDeviation) {
            this.totalDurationDeviation = totalDurationDeviation;
            return this;
        }

        public WorkloadComparisonResultBuilder successRateDeviation(double successRateDeviation) {
            this.successRateDeviation = successRateDeviation;
            return this;
        }

        public WorkloadComparisonResultBuilder avgLatencyDeviation(double avgLatencyDeviation) {
            this.avgLatencyDeviation = avgLatencyDeviation;
            return this;
        }

        public WorkloadComparisonResultBuilder stdDevLatencyDeviation(double stdDevLatencyDeviation) {
            this.stdDevLatencyDeviation = stdDevLatencyDeviation;
            return this;
        }

        public WorkloadComparisonResult build() {
            return new WorkloadComparisonResult(throughputDeviation, totalDurationDeviation, successRateDeviation, avgLatencyDeviation, stdDevLatencyDeviation);
        }
    }

}
