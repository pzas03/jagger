package com.griddynamics.jagger.engine.e1.aggregator.session.model;

import javax.persistence.*;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;


/**
 * Created with IntelliJ IDEA.
 * User: mnovozhilov
 * Date: 1/17/14
 * Time: 4:59 PM
 * To change this template use File | Settings | File Templates.
 */
@Entity
public class Tags {

    private String name;
    private String description;
    private Set<SessionData> sessions = new HashSet<SessionData>();

    public Tags() {
    }

    @Id
    @Column(unique = true, nullable = false)
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Column
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }


//    @JoinTable(name = "tag_session",joinColumns = {
//            @JoinColumn(name = "name", nullable = false, updatable = false) },
//            inverseJoinColumns = {
//                    @JoinColumn(name = "id", nullable = false, updatable = false) })




    @ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinTable(name ="tag_session")
    public Set<SessionData> getSessionId(){
        return sessions;
    }

    public void setSessions(Set<SessionData> sessions) {
        this.sessions = sessions;
    }
}
