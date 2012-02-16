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

package com.macys.platform.providers.catalog.test.performance.invoker;

import com.macys.platform.navigation.providers.catalog.dto.ProductParamsDTO;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

public class GetProductsCatalogServiceQueryProvider {
    private List<Integer> productIdsUnderTest;

    private String boDetalization;

    public List<OperationQuery> queries(){
        final ProductParamsDTO productParamsDTO;

        if(boDetalization.equals("common")){
            productParamsDTO = new ProductParamsDTO().common();
        }else if(boDetalization.equals("all")){
            productParamsDTO = new ProductParamsDTO().all();
        }else{
            productParamsDTO = new ProductParamsDTO();
        }

        return Collections.singletonList(new OperationQuery(productIdsUnderTest, productParamsDTO));
    }

    public static class OperationQuery implements Serializable{
        public List<Integer> productIdsUnderTest;

        public ProductParamsDTO productBOParams;

        public OperationQuery(List<Integer> productIdsUnderTest, ProductParamsDTO productBOParams) {
            this.productIdsUnderTest = productIdsUnderTest;
            this.productBOParams = productBOParams;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            OperationQuery that = (OperationQuery) o;

            if (productBOParams != null ? !productBOParams.equals(that.productBOParams) : that.productBOParams != null)
                return false;
            if (productIdsUnderTest != null ? !productIdsUnderTest.equals(that.productIdsUnderTest) : that.productIdsUnderTest != null)
                return false;

            return true;
        }

        @Override
        public int hashCode() {
            int result = productIdsUnderTest != null ? productIdsUnderTest.hashCode() : 0;
            result = 31 * result + (productBOParams != null ? productBOParams.hashCode() : 0);
            return result;
        }
    }

    public void setProductIdsUnderTest(List<Integer> productIdsUnderTest) {
        this.productIdsUnderTest = productIdsUnderTest;
    }

    public void setBoDetalization(String boDetalization) {
        this.boDetalization = boDetalization;
    }
}
