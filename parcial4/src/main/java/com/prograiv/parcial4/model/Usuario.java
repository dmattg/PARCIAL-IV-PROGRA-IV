package com.prograiv.parcial4.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;

@Entity
@Table(name = "usuarios")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Usuario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(unique = true, nullable = false)
    private String email;

    private LocalDate fechaNacimiento;
    private String avatar; 
    private String rol;
    
    // Para deshabilitar usuarios sin borrarlos
    private boolean enabled = true; 
    
    // Si es TRUE, el sistema lo obligará a cambiar clave al entrar
    private boolean passwordResetRequired = false; 
}