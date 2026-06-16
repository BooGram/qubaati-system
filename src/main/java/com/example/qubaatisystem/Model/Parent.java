package com.example.qubaatisystem.Model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;

@Entity
@Table(name = "parent")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Parent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, length = 120)
    private String fullName;

    @Column(nullable = false, length = 30)
    private String phoneNumber;

    // Parent belongs to one User (owning side)
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    // Parent has many Students / children (inverse side)
    @OneToMany(mappedBy = "parent", fetch = FetchType.LAZY)
    private Set<Student> children;
}
