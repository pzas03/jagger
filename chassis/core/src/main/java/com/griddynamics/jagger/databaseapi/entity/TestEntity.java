package com.griddynamics.jagger.databaseapi.entity;

/**
 * Created with IntelliJ IDEA.
 * User: kgribov
 * Date: 12/5/13
 * Time: 12:26 PM
 * To change this template use File | Settings | File Templates.
 */
public class TestEntity {
    private Long id;
    private String name;
    private String description;
    private String load;
    private String terminationStrategy;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLoad() {
        return load;
    }

    public void setLoad(String load) {
        this.load = load;
    }

    public String getTerminationStrategy() {
        return terminationStrategy;
    }

    public void setTerminationStrategy(String terminationStrategy) {
        this.terminationStrategy = terminationStrategy;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
