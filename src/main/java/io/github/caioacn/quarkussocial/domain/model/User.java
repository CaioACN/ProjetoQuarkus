package io.github.caioacn.quarkussocial.domain.model;


import lombok.Data;

import javax.persistence.*;


@Entity

@Table(name ="users",schema = "public")
@Data
public class User  {
@Id

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id")
    private Long id;
    @Column(name="name")
    private String name;
    @Column(name="age")
    private Integer age;

}
