package com.example.buttondemo.service;

import com.example.buttondemo.model.User;
import com.example.buttondemo.repository.UserRepository;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Service
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final Map<String, UserDetails> inMemoryUsers = new HashMap<>();

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        
        // In-memory kullanıcıları oluştur
        createInMemoryUsers();
    }

    private void createInMemoryUsers() {
        // Süper admin
        org.springframework.security.core.userdetails.User superAdmin = new org.springframework.security.core.userdetails.User(
            "admin",
            passwordEncoder.encode("admin123"),
            Arrays.asList(
                new SimpleGrantedAuthority("ROLE_ADMIN"),
                new SimpleGrantedAuthority("ROLE_DATA_MANAGER"),
                new SimpleGrantedAuthority("PERM_READ"),
                new SimpleGrantedAuthority("PERM_WRITE"),
                new SimpleGrantedAuthority("PERM_DELETE"),
                new SimpleGrantedAuthority("PERM_UPDATE")
            )
        );
        inMemoryUsers.put("admin", superAdmin);

        // Veri yöneticisi
        org.springframework.security.core.userdetails.User dataManager = new org.springframework.security.core.userdetails.User(
            "manager",
            passwordEncoder.encode("manager123"),
            Arrays.asList(
                new SimpleGrantedAuthority("ROLE_DATA_MANAGER"),
                new SimpleGrantedAuthority("PERM_READ"),
                new SimpleGrantedAuthority("PERM_WRITE"),
                new SimpleGrantedAuthority("PERM_UPDATE")
            )
        );
        inMemoryUsers.put("manager", dataManager);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // Önce in-memory kullanıcıları kontrol et
        UserDetails inMemoryUser = inMemoryUsers.get(username);
        if (inMemoryUser != null) {
            return inMemoryUser;
        }

        // Veritabanından kullanıcıyı bul
        User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new UsernameNotFoundException("Kullanıcı bulunamadı: " + username));

        // Normal kullanıcı yetkileri
        return new org.springframework.security.core.userdetails.User(
            user.getUsername(),
            user.getPassword(),
            Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"))
        );
    }

    public User registerUser(User user) {
        // Kullanıcı adının benzersiz olduğunu kontrol et
        if (userRepository.findByUsername(user.getUsername()).isPresent() || 
            inMemoryUsers.containsKey(user.getUsername())) {
            throw new RuntimeException("Bu kullanıcı adı zaten kullanılıyor.");
        }

        // Şifreyi hashle
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    public boolean validateUser(String username, String password) {
        return userRepository.findByUsername(username)
                .map(user -> passwordEncoder.matches(password, user.getPassword()))
                .orElse(false);
    }

    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }
} 