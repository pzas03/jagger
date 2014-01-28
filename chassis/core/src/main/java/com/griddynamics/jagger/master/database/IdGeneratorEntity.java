package com.griddynamics.jagger.master.database;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

/**
 * Created with IntelliJ IDEA.
 * User: kgribov
 * Date: 1/20/14
 * Time: 12:20 PM
 * To change this template use File | Settings | File Templates.
 */

//A table to generate ids for entities. Is required for batching processing.
@Entity
public class IdGeneratorEntity {

    @Id
    private String tableName;

    @Column
    private Long idValue;

    public IdGeneratorEntity(String tableName, Long idValue) {
        this.tableName = tableName;
        this.idValue = idValue;
    }

    public IdGeneratorEntity() {
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public Long getIdValue() {
        return idValue;
    }

    public void setIdValue(Long idValue) {
        this.idValue = idValue;
    }
}
