package com.prograiv.parcial4.controller;

import com.prograiv.parcial4.model.Usuario;
import com.prograiv.parcial4.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;

@Controller
public class AppController {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // --- 1. LOGIN ---
    @GetMapping("/login")
    public String login() {
        return "login";
    }

    // --- 2. HOME (Con lógica de bloqueo por contraseña temporal) ---
    @GetMapping("/home")
    public String home(Model model, Principal principal) {
        String username = principal.getName();
        Usuario usuario = usuarioRepository.findByUsername(username).orElse(null);

        // SEGURIDAD: Si el usuario existe y está obligado a cambiar contraseña...
        if (usuario != null && usuario.isPasswordResetRequired()) {
            // ... lo redirigimos forzosamente a la pantalla de cambio de clave
            return "redirect:/cambiar-password-obligatorio";
        }

        model.addAttribute("usuario", usuario);
        
        // Pasamos una bandera 'isAdmin' a la vista para mostrar botones de administrador
        boolean isAdmin = usuario != null && "ROLE_ADMIN".equals(usuario.getRol());
        model.addAttribute("isAdmin", isAdmin);

        return "home";
    }

    // --- 3. VER PERFIL ---
    @GetMapping("/perfil")
    public String verPerfil(Model model, Principal principal) {
        String username = principal.getName();
        Usuario usuario = usuarioRepository.findByUsername(username).orElse(null);
        model.addAttribute("usuario", usuario);
        return "perfil";
    }

    // --- 4. ACTUALIZAR PERFIL (Con subida de imagen física) ---
    @PostMapping("/perfil/actualizar")
    public String actualizarPerfil(@ModelAttribute Usuario usuarioForm,
                                   @RequestParam("imagen") MultipartFile imagen,
                                   Principal principal) {
        
        // Buscamos al usuario actual en la BD
        Usuario usuarioActual = usuarioRepository.findByUsername(principal.getName()).orElse(null);
        
        if (usuarioActual != null) {
            // Actualizamos datos básicos
            usuarioActual.setEmail(usuarioForm.getEmail());
            usuarioActual.setFechaNacimiento(usuarioForm.getFechaNacimiento());

            // Lógica para cambiar contraseña (solo si escribió una nueva)
            if (usuarioForm.getPassword() != null && !usuarioForm.getPassword().isEmpty()) {
                usuarioActual.setPassword(passwordEncoder.encode(usuarioForm.getPassword()));
            }

            // Lógica para subir la imagen a la carpeta 'uploads'
            if (!imagen.isEmpty()) {
                try {
                    // 1. Definir nombre único para que no se sobreescriban
                    String nombreArchivo = System.currentTimeMillis() + "_" + imagen.getOriginalFilename();
                    
                    // 2. Definir la ruta absoluta: Carpeta del proyecto + /uploads/ + nombre
                    Path rutaAbsoluta = Paths.get("uploads").resolve(nombreArchivo).toAbsolutePath();
                    
                    // 3. Guardar el archivo físicamente
                    Files.copy(imagen.getInputStream(), rutaAbsoluta, StandardCopyOption.REPLACE_EXISTING);
                    
                    // 4. Guardar SOLO el nombre en la base de datos
                    usuarioActual.setAvatar(nombreArchivo);
                    
                } catch (IOException e) {
                    e.printStackTrace(); // Muestra error en consola si falla la subida
                }
            }

            usuarioRepository.save(usuarioActual);
        }

        return "redirect:/home";
    }

    // --- 5. RUTAS PARA CAMBIO DE CONTRASEÑA OBLIGATORIO ---
    
    // Vista del formulario forzoso
    @GetMapping("/cambiar-password-obligatorio")
    public String vistaCambioObligatorio() {
        return "cambiar_pass";
    }

    // Proceso del cambio forzoso
    @PostMapping("/actualizar-password-obligatorio")
    public String procesarCambioObligatorio(@RequestParam String newPassword, Principal principal) {
        Usuario usuario = usuarioRepository.findByUsername(principal.getName()).orElse(null);
        
        if (usuario != null && !newPassword.isEmpty()) {
            usuario.setPassword(passwordEncoder.encode(newPassword));
            usuario.setPasswordResetRequired(false); 
            usuarioRepository.save(usuario);
        }
        return "redirect:/home";
    }
}