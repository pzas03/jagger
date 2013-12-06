package com.griddynamics.jagger.webclient.client.handler;

import com.griddynamics.jagger.webclient.client.components.control.model.SessionInfoLeaf;
import com.sencha.gxt.widget.core.client.event.CheckChangeEvent;

/**
 * Created with IntelliJ IDEA.
 * User: amikryukov
 * Date: 11/26/13
 */
public class SessionInfoLeafHandler extends TreeAwareHandler<SessionInfoLeaf> {

    @Override
    public void onCheckChange(CheckChangeEvent<SessionInfoLeaf> event) {
        tree.enableTree();
    }

}
