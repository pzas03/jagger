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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class LimitSet {
    private static final Logger log = LoggerFactory.getLogger(LimitSet.class);

    private List<Limit> limits = Collections.EMPTY_LIST;
    private String id;
    //??? set baseline sessionId here

    public void setLimits(List<Limit> limits) {

        removeDuplicates(limits);
        checkThresholdsRelation(limits);

        this.limits = limits;
    }

    public List<Limit> getLimits() {
        return limits;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    private void removeDuplicates(List<Limit> inputList) {
        Set<String> params = new HashSet<String>();
        String param;
        List<Limit> duplicates = new ArrayList<Limit>();

        for(Limit limit : inputList) {
            param = limit.getMetricName();

            if(params.contains(param)) {
                duplicates.add(limit);
                log.error("Limit with metricName '" + param + "' already exists. New limit with the same name will be ignored");
            }
            params.add(param);
        }

        inputList.removeAll(duplicates);
    }

    private void checkThresholdsRelation(List<Limit> inputList) {
        List<Limit> limitsWithErrors = new ArrayList<Limit>();

        for(Limit limit : inputList) {
            if (limit.getLEL() > limit.getLWL()) {
                limitsWithErrors.add(limit);
                log.error("Limit with metricName '" + limit.getMetricName() +
                        "' has wrong relation of thresholds. LEL "+ limit.getLEL() + " should be less than LWL " + limit.getLWL());
                continue;
            }
            if (limit.getLWL() > limit.getUWL()) {
                limitsWithErrors.add(limit);
                log.error("Limit with metricName '" + limit.getMetricName() +
                        "' has wrong relation of thresholds. LWL " + limit.getLWL() + " should be less than UWL " + limit.getUWL());
                continue;
            }
            if (limit.getUWL() > limit.getUEL()) {
                limitsWithErrors.add(limit);
                log.error("Limit with metricName '" + limit.getMetricName() +
                        "' has wrong relation of thresholds. UWL " + limit.getUWL() + " should be less than UEL " + limit.getUEL());
                continue;
            }
        }

        inputList.removeAll(limitsWithErrors);
    }

}

