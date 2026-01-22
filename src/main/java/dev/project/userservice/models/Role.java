package dev.project.userservice.models;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import jakarta.persistence.Entity;
import jakarta.persistence.Column;
import jakarta.persistence.FetchType;
import jakarta.persistence.ManyToMany;
import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@JsonDeserialize(as = Role.class)
public class Role extends BaseModel {
    // property is 'name' in code but map to existing DB column 'role'
    @Column(name = "role")
    private String name;

//    @ManyToMany(mappedBy = "roles", fetch = FetchType.LAZY)
//    private Set<User> users = new HashSet<>();

    // explicit getter/setter in case Lombok isn't processed by the environment
    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    // explicit id getter/setter to make static analysis and tests happy
    @Override
    public Long getId() {
        return super.getId();
    }

    @Override
    public void setId(Long id) {
        super.setId(id);
    }
    //public void setUsers(Set<User> users) { this.users = users; }
}
