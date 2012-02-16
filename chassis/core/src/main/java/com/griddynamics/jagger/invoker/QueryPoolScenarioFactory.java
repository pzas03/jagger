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

package com.griddynamics.jagger.invoker;

import com.griddynamics.jagger.coordinator.NodeContext;
import com.griddynamics.jagger.util.JavaSystemClock;
import com.griddynamics.jagger.util.SystemClock;
import org.springframework.beans.factory.annotation.Required;

public class QueryPoolScenarioFactory<Q, R, E> implements ScenarioFactory<Q, R, E> {
    private Class<Invoker<Q, R, E>> invokerClazz;
    private LoadBalancer<Q, E> loadBalancer;
    private SystemClock systemClock = new JavaSystemClock();

    @Override
    public Scenario<Q, R, E> get(NodeContext nodeContext) {
        Invoker<Q, R, E> invoker = nodeContext.getService(invokerClazz);
        return new QueryPoolScenario<Q, R, E>(invoker, loadBalancer.provide(), systemClock);
    }

    @Required
    public void setInvokerClazz(Class<Invoker<Q, R, E>> invokerClazz) {
        this.invokerClazz = invokerClazz;
    }

    @Required
    public void setLoadBalancer(LoadBalancer<Q, E> loadBalancer) {
        this.loadBalancer = loadBalancer;
    }

    public void setSystemClock(SystemClock systemClock) {
        this.systemClock = systemClock;
    }

    @Override
    public int getCalibrationSamplesCount() {
        return loadBalancer.querySize();
    }
}
