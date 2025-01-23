package com.example.buttondemo.repository;

import com.example.buttondemo.model.GpuInfo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GpuRepository extends JpaRepository<GpuInfo, Long> {
    // Özel sorgular buraya eklenebilir
} 