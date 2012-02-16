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
package com.griddynamics.jagger.master;

import com.google.common.base.Objects;
import org.springframework.beans.factory.annotation.Required;

public class MasterTimeoutConfiguration {
    private long nodeAwaitTime;
    private long taskExecutionTime;
    private long distributionStartTime;
    private long distributionStopTime;

    public long getTaskExecutionTime() {
        return taskExecutionTime;
    }

    @Required
    public void setTaskExecutionTime(long taskExecutionTime) {
        this.taskExecutionTime = taskExecutionTime;
    }

    public long getDistributionStartTime() {
        return distributionStartTime;
    }

    @Required
    public void setDistributionStartTime(long distributionStartTime) {
        this.distributionStartTime = distributionStartTime;
    }

    public long getDistributionStopTime() {
        return distributionStopTime;
    }

    @Required
    public void setDistributionStopTime(long distributionStopTime) {
        this.distributionStopTime = distributionStopTime;
    }

    public long getNodeAwaitTime() {
        return nodeAwaitTime;
    }

    @Required
    public void setNodeAwaitTime(long nodeAwaitTime) {
        this.nodeAwaitTime = nodeAwaitTime;
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this)
                .add("nodeAwaitTime", nodeAwaitTime)
                .add("taskExecutionTime", taskExecutionTime)
                .add("distributionStartTime", distributionStartTime)
                .add("distributionStopTime", distributionStopTime)
                .toString();
    }
}
