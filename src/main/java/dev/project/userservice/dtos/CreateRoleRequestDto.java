package dev.project.userservice.dtos;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateRoleRequestDto {
    private String name;

    // explicit getter/setter in case Lombok is not processed in the environment
    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
