package dev.project.userservice.models;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import jakarta.persistence.Entity;
import jakarta.persistence.Column;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@JsonDeserialize(as = Role.class)
public class Role extends BaseModel {
    // property is 'name' in code but map to existing DB column 'role'
    @Column(name = "role")
    private String name;

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
}
