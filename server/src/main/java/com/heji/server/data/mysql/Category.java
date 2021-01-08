package com.heji.server.data.mysql;

import javax.persistence.*;

@Entity
@Table(name = "hj_category")
public class Category {

    @Column(name = "type")
    private Integer type;
    @Id
    @Column(name = "name")
    private String name;

    @Column(name = "level")
    private Integer level;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getLevel() {
        return level;
    }

    public void setLevel(Integer level) {
        this.level = level;
    }

    public int getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }
}
