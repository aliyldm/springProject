package com.example.buttondemo.controller;

import com.example.buttondemo.model.GpuInfo;
import com.example.buttondemo.model.GpuComment;
import com.example.buttondemo.model.User;
import com.example.buttondemo.service.GpuScraperService;
import com.example.buttondemo.repository.GpuCommentRepository;
import com.example.buttondemo.repository.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/api/gpu")
public class GpuController {
    private static final Logger logger = LoggerFactory.getLogger(GpuController.class);

    private final GpuScraperService gpuScraperService;
    private final GpuCommentRepository gpuCommentRepository;
    private final UserRepository userRepository;

    public GpuController(GpuScraperService gpuScraperService,
                        GpuCommentRepository gpuCommentRepository,
                        UserRepository userRepository) {
        this.gpuScraperService = gpuScraperService;
        this.gpuCommentRepository = gpuCommentRepository;
        this.userRepository = userRepository;
    }

    @GetMapping
    public String showGpuList(Model model) {
        List<GpuInfo> gpus = gpuScraperService.getAllGpus();
        
        // Mimari listesini al
        List<String> architectures = gpus.stream()
            .map(GpuInfo::getArchitecture)
            .distinct()
            .filter(arch -> arch != null && !arch.isEmpty())
            .sorted()
            .collect(Collectors.toList());
        
        // Bellek tipi listesini al
        List<String> memoryTypes = gpus.stream()
            .map(GpuInfo::getMemoryType)
            .distinct()
            .filter(type -> type != null && !type.isEmpty())
            .sorted()
            .collect(Collectors.toList());

        model.addAttribute("gpus", gpus);
        model.addAttribute("architectures", architectures);
        model.addAttribute("memoryTypes", memoryTypes);
        
        return "gpu-list";
    }

    @GetMapping("/{id}")
    public String showGpuDetails(@PathVariable Long id, Model model, Authentication authentication) {
        Optional<GpuInfo> gpuOpt = gpuScraperService.getGpuById(id);
        if (gpuOpt.isPresent()) {
            GpuInfo gpu = gpuOpt.get();
            List<GpuComment> comments = gpuCommentRepository.findByGpuIdOrderByCreatedAtDesc(id);
            
            model.addAttribute("gpu", gpu);
            model.addAttribute("comments", comments);
            model.addAttribute("currentUser", authentication.getName());
            
            // Log the number of comments and likes for debugging
            logger.info("GPU {} has {} comments and {} likes", 
                gpu.getName(), 
                comments.size(), 
                gpu.getLikes() != null ? gpu.getLikes().size() : 0);
            
            return "gpu-detail";
        }
        return "redirect:/api/gpu";
    }

    @PostMapping("/{id}/comment")
    public String addComment(@PathVariable Long id,
                           @RequestParam String content,
                           Authentication authentication,
                           RedirectAttributes redirectAttributes) {
        try {
            Optional<GpuInfo> gpuOpt = gpuScraperService.getGpuById(id);
            Optional<User> userOpt = userRepository.findByUsername(authentication.getName());
            
            if (gpuOpt.isPresent() && userOpt.isPresent() && !content.trim().isEmpty()) {
                GpuInfo gpu = gpuOpt.get();
                User user = userOpt.get();
                
                GpuComment comment = new GpuComment();
                comment.setContent(content.trim());
                comment.setGpu(gpu);
                comment.setUser(user);
                
                gpuCommentRepository.save(comment);
                
                logger.info("New comment added by {} for GPU {}", user.getUsername(), gpu.getName());
                redirectAttributes.addFlashAttribute("success", "Yorumunuz başarıyla eklendi!");
            } else {
                redirectAttributes.addFlashAttribute("error", "Yorum eklenirken bir hata oluştu.");
            }
        } catch (Exception e) {
            logger.error("Error adding comment for GPU {}: {}", id, e.getMessage());
            redirectAttributes.addFlashAttribute("error", "Yorum eklenirken bir hata oluştu.");
        }
        
        return "redirect:/api/gpu/" + id;
    }

    @PostMapping("/{id}/like")
    public String toggleLike(@PathVariable Long id,
                           Authentication authentication,
                           RedirectAttributes redirectAttributes) {
        try {
            Optional<GpuInfo> gpuOpt = gpuScraperService.getGpuById(id);
            Optional<User> userOpt = userRepository.findByUsername(authentication.getName());
            
            if (gpuOpt.isPresent() && userOpt.isPresent()) {
                GpuInfo gpu = gpuOpt.get();
                User user = userOpt.get();
                
                if (gpu.getLikes() == null) {
                    gpu.setLikes(new java.util.HashSet<>());
                }
                
                boolean wasLiked = gpu.getLikes().contains(user);
                if (wasLiked) {
                    gpu.getLikes().remove(user);
                    logger.info("User {} removed like from GPU {}", user.getUsername(), gpu.getName());
                } else {
                    gpu.getLikes().add(user);
                    logger.info("User {} liked GPU {}", user.getUsername(), gpu.getName());
                }
                
                gpuScraperService.saveGpu(gpu);
                redirectAttributes.addFlashAttribute("success", wasLiked ? "Beğeni kaldırıldı!" : "GPU beğenildi!");
            } else {
                redirectAttributes.addFlashAttribute("error", "Beğeni işlemi yapılırken bir hata oluştu.");
            }
        } catch (Exception e) {
            logger.error("Error toggling like for GPU {}: {}", id, e.getMessage());
            redirectAttributes.addFlashAttribute("error", "Beğeni işlemi yapılırken bir hata oluştu.");
        }
        
        return "redirect:/api/gpu/" + id;
    }

    @PostMapping("/scrape")
    public String scrapeGpuData(RedirectAttributes redirectAttributes) {
        try {
            gpuScraperService.scrapeAndSaveGpuInfo();
            redirectAttributes.addFlashAttribute("success", "GPU verileri başarıyla güncellendi!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "GPU verileri güncellenirken bir hata oluştu: " + e.getMessage());
        }
        return "redirect:/api/gpu";
    }
} 