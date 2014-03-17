package com.griddynamics.jagger.webclient.server;

import com.griddynamics.jagger.webclient.client.dto.TaskDataDto;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by kgribov on 3/17/14.
 */
public class CommonUtils {
    public static Set<Long> getTestsIds(List<TaskDataDto> tests){
        Set<Long> taskIds = new HashSet<Long>();
        for (TaskDataDto tdd : tests) {
            taskIds.addAll(tdd.getIds());
        }
        return taskIds;
    }

    public static boolean containsAtLeastOne(Collection origin, Collection elements){
        boolean result = false;
        for (Object element : elements){
            if (origin.contains(element)){
                result = true;
                break;
            }
        }

        return result;
    }
}
