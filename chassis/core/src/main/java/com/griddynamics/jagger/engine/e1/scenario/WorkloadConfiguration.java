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

package com.griddynamics.jagger.engine.e1.scenario;

import com.google.common.base.Preconditions;

import java.io.Serializable;

/**
 * Presents workload configuration on kernel side.
 *
 * @author Mairbek Khadikov
 */
public class WorkloadConfiguration implements Serializable {
    private final int threads;
    private final int delay;

    public static WorkloadConfiguration with(int threads, int delay) {
        return new WorkloadConfiguration(threads, delay);
    }

    public static WorkloadConfiguration withTreads(int threads) {
        return new WorkloadConfiguration(threads, 0);
    }

    public static WorkloadConfiguration zero() {
        return with(0, 0);
    }

    private WorkloadConfiguration(int threads, int delay) {
        Preconditions.checkArgument(threads >= 0);
        Preconditions.checkArgument(delay >= 0);

        this.threads = threads;
        this.delay = delay;
    }

    public int getThreads() {
        return threads;
    }

    public int getDelay() {
        return delay;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        WorkloadConfiguration that = (WorkloadConfiguration) o;

        if (delay != that.delay) return false;
        if (threads != that.threads) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = threads;
        result = 31 * result + delay;
        return result;
    }

    @Override
    public String toString() {
        return "configuration " + threads + " threads " + delay + " delay";
    }
}
