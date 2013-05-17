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
package com.griddynamics.jagger.invoker.http;

import com.griddynamics.jagger.invoker.Invoker;
import org.apache.http.client.methods.*;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * {@link Invoker} that invokes services of SuT via http protocol.
 *
 * @author Alexey Kiselyov
 */
public class HttpInvoker extends ApacheAbstractHttpInvoker<HttpQuery> {
    private static final Logger log = LoggerFactory.getLogger(ApacheAbstractHttpInvoker.class);

    @Override
    protected HttpRequestBase getHttpMethod(HttpQuery query, String endpoint) {
        HttpRequestBase method = createMethod(query, endpoint);

        if (!HttpQuery.Method.POST.equals(query.getMethod()) && !HttpQuery.Method.CONNECT.equals(query.getMethod())) {
            for (Map.Entry<String, String> methodParam : query.getMethodParams().entrySet()) {
                method.getParams().setParameter(methodParam.getKey(), methodParam.getValue());
            }
        }

        return method;
    }

    @Override
    protected HttpParams getHttpClientParams(HttpQuery query) {
        HttpParams clientParams = new BasicHttpParams();
        for (Map.Entry<String, Object> clientParam : query.getClientParams().entrySet()) {
            clientParams.setParameter(clientParam.getKey(), clientParam.getValue());
        }
        return clientParams;
    }

    private HttpRequestBase createMethod(HttpQuery query, String endpoint) {
        HttpRequestBase method;
        switch (query.getMethod()) {
            case POST:
                method = new HttpPost(endpoint);
                for (Map.Entry<String, String> methodParam : query.getMethodParams().entrySet()) {
                    method.getParams().setParameter(methodParam.getKey(), methodParam.getValue());
                }
                break;
            case PUT:
                method = new HttpPut(endpoint);
                break;
            case GET:
                method = new HttpGet(endpoint);
                for (Map.Entry<String, String> stringStringEntry : query.getMethodParams().entrySet()) {
                    method.getParams().setParameter(stringStringEntry.getKey(), stringStringEntry.getValue());
                }
                break;
            case DELETE:
                method = new HttpDelete(endpoint);
                break;
            case TRACE:
                method = new HttpTrace(endpoint);
                break;
            case HEAD:
                method = new HttpHead(endpoint);
                break;
            case OPTIONS:
                method = new HttpOptions(endpoint);
                break;
            default:
                throw new UnsupportedOperationException("Invoker does not support \"" + query.getMethod() + "\" HTTP request.");
        }
        return method;
    }

    @Override
    public String toString() {
        return "Apache Commons Http Invoker";
    }
}
