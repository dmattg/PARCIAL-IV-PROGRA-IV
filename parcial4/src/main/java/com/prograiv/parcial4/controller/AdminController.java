package com.prograiv.parcial4.controller;

import java.security.Principal;
import com.prograiv.parcial4.model.Usuario;
import com.prograiv.parcial4.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // Listar usuarios
    @GetMapping("/usuarios")
    public String listarUsuarios(Model model) {
        model.addAttribute("usuarios", usuarioRepository.findAll());
        return "admin_usuarios"; // Vista nueva que crearemos
    }

    // Formulario crear/editar
    @GetMapping("/usuario/nuevo")
    public String formUsuario(Model model) {
        model.addAttribute("usuario", new Usuario());
        return "admin_form";
    }

    @GetMapping("/usuario/editar/{id}")
    public String editarUsuario(@PathVariable Long id, Model model) {
        model.addAttribute("usuario", usuarioRepository.findById(id).orElse(null));
        return "admin_form";
    }

    // Guardar usuario (Lógica de password temporal)
    @PostMapping("/usuario/guardar")
    public String guardarUsuario(@ModelAttribute Usuario usuario, 
                                 @RequestParam(value = "esTemporal", defaultValue = "false") boolean esTemporal) {
        
        // Si es edición, necesitamos recuperar el usuario original para no perder datos
        Usuario usuarioExistente = null;
        if (usuario.getId() != null) {
            usuarioExistente = usuarioRepository.findById(usuario.getId()).orElse(null);
        }

        // Lógica de contraseña
        if (!usuario.getPassword().isEmpty()) {
            // Si escribió contraseña, la encriptamos
            usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));
            // Si el admin marcó "Temporal", activamos la bandera
            usuario.setPasswordResetRequired(esTemporal);
        } else if (usuarioExistente != null) {
            // Si lo dejó vacío, mantenemos la anterior
            usuario.setPassword(usuarioExistente.getPassword());
        }

        // Si es nuevo, habilitamos por defecto
        if (usuario.getId() == null) usuario.setEnabled(true);

        usuarioRepository.save(usuario);
        return "redirect:/admin/usuarios";
    }

    // Deshabilitar/Habilitar (Mejor que borrar)
    @GetMapping("/usuario/toggle/{id}")
    public String toggleEstado(@PathVariable Long id, Principal principal) {
        Usuario u = usuarioRepository.findById(id).orElse(null);
    
    // R1: Prevenir que el ADMIN se auto-deshabilite
    if (u != null && u.getUsername().equals(principal.getName())) {
        // Redirigir con mensaje de error (aunque el botón debería estar deshabilitado)
        return "redirect:/admin/usuarios?error=selfdisable";
    }

    if (u != null) {
        u.setEnabled(!u.isEnabled());
        usuarioRepository.save(u);
    }
    return "redirect:/admin/usuarios";
    }
}