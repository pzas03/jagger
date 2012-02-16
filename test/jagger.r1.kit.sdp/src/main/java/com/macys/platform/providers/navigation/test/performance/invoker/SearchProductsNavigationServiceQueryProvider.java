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

package com.macys.platform.providers.navigation.test.performance.invoker;

import com.macys.platform.services.navigation.dto.DefaultConstraintDTO;
import com.macys.platform.services.navigation.dto.ProductQueryDTO;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SearchProductsNavigationServiceQueryProvider {

    private List<Integer> categoryIdsUnderTest;

    private Integer pageNumber;

    private Integer itemsPerPage;

    private boolean withRefinements;

    private List<DefaultConstraintDTO> filters;

    private String sortType;

    private String sortOrder;

    private Map<String, List<DefaultConstraintDTO>> filterMap;

    public void setCategoryIdsUnderTest(List<Integer> categoryIdsUnderTest) {
        this.categoryIdsUnderTest = categoryIdsUnderTest;
    }

    public void setPageNumber(Integer pageNumber) {
        this.pageNumber = pageNumber;
    }

    public void setItemsPerPage(Integer itemsPerPage) {
        this.itemsPerPage = itemsPerPage;
    }

    public void setWithRefinements(boolean withRefinements) {
        this.withRefinements = withRefinements;
    }

    public void setFilters(List<DefaultConstraintDTO> filters) {
        this.filters = filters;
    }

    public void setSortType(String sortType) {
        this.sortType = sortType;
    }

    public void setSortOrder(String sortOrder) {
        this.sortOrder = sortOrder;
    }

    public void setFilterMap(Map<String, List<DefaultConstraintDTO>> filterMap) {
        this.filterMap = filterMap;
    }

    public List<ProductQueryDTO> queries() {
        final List<ProductQueryDTO> queryList = new ArrayList<ProductQueryDTO>();

        for (Integer categoryId : categoryIdsUnderTest){
            ProductQueryDTO productQuery = new ProductQueryDTO().forCategory(categoryId).paginated(pageNumber, itemsPerPage);

            if (filterMap != null){
                if (filterMap.keySet().contains(categoryId.toString())){
                    for (DefaultConstraintDTO filter : filterMap.get(categoryId.toString())){
                        if (filter.getRange() != null) {
                            productQuery = productQuery.filtered(filter.getName(), filter.getRange().getFrom(),
                                    filter.getRange().getTo());
                        } else {
                            productQuery = productQuery.filtered(filter.getName(), filter.getValue().toString());
                        }
                    }
                }
            }

            if (filters != null){
                for (DefaultConstraintDTO filter : filters) {
                    if (filter.getRange() != null) {
                        productQuery = productQuery.filtered(filter.getName(), filter.getRange().getFrom(),
                                filter.getRange().getTo());
                    } else {
                        productQuery = productQuery.filtered(filter.getName(), filter.getValue().toString());
                    }
                }
            }

            if (withRefinements){
                productQuery = productQuery.withRefinements();
            }

            if (sortType != null && sortOrder != null){
                productQuery = productQuery.sorted(ProductQueryDTO.ProductSort.valueOf(sortType),
                        ProductQueryDTO.SortOrder.valueOf(sortOrder));
            }

            queryList.add(productQuery);
        }

        return queryList;
    }
}

