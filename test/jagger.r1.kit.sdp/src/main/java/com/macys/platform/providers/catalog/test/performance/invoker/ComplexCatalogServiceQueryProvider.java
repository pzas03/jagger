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


import com.macys.platform.navigation.providers.catalog.dto.CategoryDTO;
import com.macys.platform.navigation.providers.catalog.dto.CategoryTreeQueryDTO;
import com.macys.platform.navigation.providers.catalog.dto.ProductParamsDTO;
import com.macys.platform.navigation.providers.catalog.dto.ProductQueryDTO;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ComplexCatalogServiceQueryProvider {

    private List<Integer> rootCategoryIds;
    private int childrenSearchDepth;
    private int parentSearchHeight;
    private int parentsChildrenSearchDepth;
    private String categoryGroupType;

    private boolean withRefinements;
    private Integer pageNumber;
    private Integer itemsPerPage;
    private String sortType;
    private String sortOrder;

    private String boDetalization;


    public List<OperationQuery> queries(){
        List<OperationQuery> queries = new ArrayList<OperationQuery>();
        for(Integer rootCategoryId : rootCategoryIds){

            OperationQuery operationQuery = new OperationQuery();

            CategoryTreeQueryDTO.CategoryNodeParameters parameters = new CategoryTreeQueryDTO.CategoryNodeParameters();
            parameters.setGroups(Collections.singletonList(CategoryDTO.GroupType.valueOf(categoryGroupType)));
            CategoryTreeQueryDTO categoryTreeQuery = new CategoryTreeQueryDTO();
            categoryTreeQuery.setChildrenSearchDepth(childrenSearchDepth);
            categoryTreeQuery.setParentSearchHeight(parentSearchHeight);
            categoryTreeQuery.setParentsChildrenSearchDepth(parentsChildrenSearchDepth);
            categoryTreeQuery.setCategoryId(rootCategoryId);
            categoryTreeQuery.setCategoryParameters(parameters);
            categoryTreeQuery.setChildrenParameters(parameters);
            categoryTreeQuery.setParentParameters(parameters);
            categoryTreeQuery.setParentsChildrenParameters(parameters);

            operationQuery.setCategoryTreeQueryDTO(categoryTreeQuery);

            ProductQueryDTO productQuery = new ProductQueryDTO().paginated(pageNumber, itemsPerPage);
            if (withRefinements){
                productQuery = productQuery.withRefinements();
            }
            if (sortType != null && sortOrder != null){
                productQuery = productQuery.sorted(ProductQueryDTO.ProductSort.valueOf(sortType),
                                                    ProductQueryDTO.SortOrder.valueOf(sortOrder));
            }

            operationQuery.setProductQueryDTO(productQuery);

            ProductParamsDTO productParamsDTO;
            if(boDetalization.equals("common")){
                productParamsDTO = new ProductParamsDTO().common();
            }else if(boDetalization.equals("all")){
                productParamsDTO = new ProductParamsDTO().all();
            }else{
                productParamsDTO = new ProductParamsDTO();
            }

            operationQuery.setProductParamsDTO(productParamsDTO);

            queries.add(operationQuery);
        }

        return queries;
    }

    public static class OperationQuery implements Serializable{

        CategoryTreeQueryDTO categoryTreeQueryDTO;

        ProductQueryDTO productQueryDTO;

        ProductParamsDTO productParamsDTO;

        public CategoryTreeQueryDTO getCategoryTreeQueryDTO() {
            return categoryTreeQueryDTO;
        }

        public void setCategoryTreeQueryDTO(CategoryTreeQueryDTO categoryTreeQueryDTO) {
            this.categoryTreeQueryDTO = categoryTreeQueryDTO;
        }

        public ProductQueryDTO getProductQueryDTO() {
            return productQueryDTO;
        }

        public void setProductQueryDTO(ProductQueryDTO productQueryDTO) {
            this.productQueryDTO = productQueryDTO;
        }

        public ProductParamsDTO getProductParamsDTO() {
            return productParamsDTO;
        }

        public void setProductParamsDTO(ProductParamsDTO productParamsDTO) {
            this.productParamsDTO = productParamsDTO;
        }
    }

    public void setRootCategoryIds(List<Integer> rootCategoryIds) {
        this.rootCategoryIds = rootCategoryIds;
    }

    public void setChildrenSearchDepth(int childrenSearchDepth) {
        this.childrenSearchDepth = childrenSearchDepth;
    }

    public void setParentSearchHeight(int parentSearchHeight) {
        this.parentSearchHeight = parentSearchHeight;
    }

    public void setParentsChildrenSearchDepth(int parentsChildrenSearchDepth) {
        this.parentsChildrenSearchDepth = parentsChildrenSearchDepth;
    }

    public void setCategoryGroupType(String categoryGroupType) {
        this.categoryGroupType = categoryGroupType;
    }

    public void setWithRefinements(boolean withRefinements) {
        this.withRefinements = withRefinements;
    }

    public void setPageNumber(Integer pageNumber) {
        this.pageNumber = pageNumber;
    }

    public void setItemsPerPage(Integer itemsPerPage) {
        this.itemsPerPage = itemsPerPage;
    }

    public void setSortType(String sortType) {
        this.sortType = sortType;
    }

    public void setSortOrder(String sortOrder) {
        this.sortOrder = sortOrder;
    }

    public void setBoDetalization(String boDetalization) {
        this.boDetalization = boDetalization;
    }
}
