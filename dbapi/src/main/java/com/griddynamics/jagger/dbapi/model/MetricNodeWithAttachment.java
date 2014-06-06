package com.griddynamics.jagger.dbapi.model;

/**
 * MetricNode with attachment
 * @param <V>
 */
public class MetricNodeWithAttachment<V> extends MetricNode {

    private V attachment;

    public V getAttachment() {
        return attachment;
    }

    public void setAttachment(V attachment) {
        this.attachment = attachment;
    }
}
