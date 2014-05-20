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

package com.griddynamics.jagger.util;

/**
 * Class is used in chassis, web UI server and web UI client
 * to use it in web UI client - keep it simple (use only standard java libraries)
 */
public class StandardMetricsNamesUtil {
    public static final String THROUGHPUT_TPS_REGEX = "^Throughput, tps";
    public static final String THROUGHPUT_REGEX = "^Throughput";
    public static final String LATENCY_SEC_REGEX = "^Latency, sec";
    public static final String LATENCY_REGEX = "^Latency";
    public static final String LATENCY_PERCENTILE_REGEX = "^Latency\\s\\S+\\s%";
    public static final String ITERATIONS_SAMPLES_REGEX = "^Iterations, samples";
    public static final String SUCCESS_RATE_REGEX = "^Success rate";
    public static final String DURATION_SEC_REGEX = "^Duration, sec";
    public static final String TIME_LATENCY_PERCENTILE_REGEX = "^Time Latency Percentile";
}
