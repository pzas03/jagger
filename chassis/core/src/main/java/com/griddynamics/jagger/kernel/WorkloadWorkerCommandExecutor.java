package com.griddynamics.jagger.kernel;

import com.griddynamics.jagger.coordinator.Command;
import com.griddynamics.jagger.coordinator.CommandExecutor;
import com.griddynamics.jagger.coordinator.NodeContext;
import com.griddynamics.jagger.storage.KeyValueStorage;

import java.io.Serializable;

/**
 * Created with IntelliJ IDEA.
 * User: mnovozhilov
 * Date: 5/22/14
 * Time: 11:30 AM
 * To change this template use File | Settings | File Templates.
 */

abstract public class WorkloadWorkerCommandExecutor<C extends Command<R>, R extends Serializable> implements CommandExecutor <C,R> {

    @Override
    public R execute(C command, NodeContext nodeContext) {
        KeyValueStorage keyValueStorage = nodeContext.getService(KeyValueStorage.class);
        if (keyValueStorage != null)
            nodeContext.getService(KeyValueStorage.class).setSessionId(command.getSessionId());
        return doExecute(command, nodeContext);
    }

    abstract public R doExecute(C command, NodeContext nodeContext);
}
