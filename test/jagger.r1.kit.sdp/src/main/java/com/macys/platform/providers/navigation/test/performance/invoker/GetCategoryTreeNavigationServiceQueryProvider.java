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


import com.macys.platform.services.navigation.dto.CategoryDTO.GroupType;
import com.macys.platform.services.navigation.dto.CategoryTreeQueryDTO;
import com.macys.platform.services.navigation.dto.CategoryTreeQueryDTO.CategoryNodeParameters;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class GetCategoryTreeNavigationServiceQueryProvider {
    private List<Integer> categoryIds;

    private int childrenSearchDepth;

    private int parentSearchHeight;

    private int parentsChildrenSearchDepth;

    private String childrenParametersAsString;

    private String parentsChildrenParametersAsString;

    private String parentParametersAsString;

    private String categoryParametersAsString;

    public List<CategoryTreeQueryDTO> queries() {
        List<CategoryTreeQueryDTO> queryList = new ArrayList<CategoryTreeQueryDTO>();

        for (Integer categoryId : categoryIds) {
            CategoryTreeQueryDTO.CategoryNodeParameters parameters = new CategoryTreeQueryDTO.CategoryNodeParameters();

            CategoryTreeQueryDTO categoryQuery = new CategoryTreeQueryDTO();
            categoryQuery.setCategoryId(categoryId);
            categoryQuery.setChildrenSearchDepth(childrenSearchDepth);
            categoryQuery.setParentSearchHeight(parentSearchHeight);
            categoryQuery.setParentsChildrenSearchDepth(parentsChildrenSearchDepth);
            categoryQuery.setCategoryParameters(parameters);

            CategoryNodeParameters childrenParameters = new CategoryNodeParameters();
            childrenParameters.setGroups(
                    Collections.singletonList(GroupType.valueOf(childrenParametersAsString)));
            categoryQuery.setChildrenParameters(childrenParameters);

            CategoryNodeParameters parentsChildrenParameters = new CategoryNodeParameters();
            parentsChildrenParameters.setGroups(
                    Collections.singletonList(GroupType.valueOf(parentsChildrenParametersAsString)));
            categoryQuery.setParentsChildrenParameters(parentsChildrenParameters);

            CategoryNodeParameters parentParameters = new CategoryNodeParameters();
            parentParameters.setGroups(
                    Collections.singletonList(GroupType.valueOf(parentParametersAsString)));
            categoryQuery.setParentParameters(parentParameters);

            CategoryNodeParameters categoryParameters = new CategoryNodeParameters();
            categoryParameters.setGroups(
                    Collections.singletonList(GroupType.valueOf(categoryParametersAsString)));
            categoryQuery.setCategoryParameters(categoryParameters);

            queryList.add(categoryQuery);

        }
        return queryList;
    }

    public void setCategoryIds(List<Integer> categoryIds) {
        this.categoryIds = categoryIds;
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

    public void setChildrenParametersAsString(String childrenParametersAsString) {
        this.childrenParametersAsString = childrenParametersAsString;
    }

    public void setParentsChildrenParametersAsString(String parentsChildrenParametersAsString) {
        this.parentsChildrenParametersAsString = parentsChildrenParametersAsString;
    }

    public void setParentParametersAsString(String parentParametersAsString) {
        this.parentParametersAsString = parentParametersAsString;
    }

    public void setCategoryParametersAsString(String categoryParametersAsString) {
        this.categoryParametersAsString = categoryParametersAsString;
    }
}
