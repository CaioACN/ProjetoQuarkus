package io.github.caioacn.quarkussocial.rest.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;


@Data
public class CreateUserRequest  {

    @NotBlank(message = "Name is Required")
    private String name;
    @NotNull(message = "Name is Required")
    private Integer age;
}
