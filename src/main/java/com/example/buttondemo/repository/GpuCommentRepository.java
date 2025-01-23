package com.example.buttondemo.repository;

import com.example.buttondemo.model.GpuComment;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface GpuCommentRepository extends JpaRepository<GpuComment, Long> {
    List<GpuComment> findByGpuIdOrderByCreatedAtDesc(Long gpuId);
} 