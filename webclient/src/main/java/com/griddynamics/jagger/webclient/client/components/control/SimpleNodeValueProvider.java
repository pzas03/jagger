package com.griddynamics.jagger.webclient.client.components.control;

import com.griddynamics.jagger.webclient.client.components.control.model.SimpleNode;
import com.sencha.gxt.core.client.ValueProvider;

/**
 * Created with IntelliJ IDEA.
 * User: amikryukov
 * Date: 11/26/13
 */
public class SimpleNodeValueProvider implements ValueProvider<SimpleNode, String> {

    @Override
    public String getValue(SimpleNode object) {
        return object.getDisplayName();
    }

    @Override
    public void setValue(SimpleNode object, String value) {
        object.setDisplayName(value);
    }

    @Override
    public String getPath() {
        return "displayName";
    }
}
