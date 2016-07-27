package com.home.services.domain;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Set;

/**
 * Created by vitaliy on 7/26/16.
 */
@Entity(name = "roles")
public class Role implements Serializable {


    private static final long serialVersionUID = 7185817225215157017L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String name;

    @OneToMany(cascade = CascadeType.ALL)
    @JoinTable(name="user_roles",
            joinColumns = {@JoinColumn(name="role_id", referencedColumnName="id")},
            inverseJoinColumns = {@JoinColumn(name="user_id", referencedColumnName="id")}
    )
    private Set<User> userRoles;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
