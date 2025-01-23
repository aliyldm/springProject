package com.example.buttondemo.service;

import com.example.buttondemo.model.GpuInfo;
import com.example.buttondemo.repository.GpuRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class GpuScraperService {

    private static final Logger logger = LoggerFactory.getLogger(GpuScraperService.class);
    private final GpuRepository gpuRepository;
    private final ObjectMapper objectMapper;

    public GpuScraperService(GpuRepository gpuRepository) {
        this.gpuRepository = gpuRepository;
        this.objectMapper = new ObjectMapper();
    }

    public void scrapeAndSaveGpuInfo() throws IOException {
        try {
            // JSON dosyasını oku
            ClassPathResource resource = new ClassPathResource("gpu_data.json");
            JsonNode rootNode = objectMapper.readTree(resource.getInputStream());
            JsonNode gpusNode = rootNode.get("gpus");
            
            List<GpuInfo> gpuList = new ArrayList<>();
            
            for (JsonNode gpuNode : gpusNode) {
                GpuInfo gpu = new GpuInfo();
                gpu.setName(gpuNode.get("name").asText());
                gpu.setArchitecture(gpuNode.get("architecture").asText());
                gpu.setBoostClock(gpuNode.get("boost_clock").asText());
                gpu.setBaseClock(gpuNode.get("base_clock").asText());
                gpu.setMemorySize(gpuNode.get("memory_size").asText());
                gpu.setMemoryType(gpuNode.get("memory_type").asText());
                gpu.setTdp(gpuNode.get("tdp").asText());
                gpu.setLaunchDate(gpuNode.get("release_date").asText());
                
                gpuList.add(gpu);
                logger.info("GPU verisi okundu: {}", gpu.getName());
            }
            
            // Tüm GPU'ları kaydet
            gpuRepository.saveAll(gpuList);
            logger.info("Toplam {} GPU verisi başarıyla kaydedildi", gpuList.size());
            
        } catch (IOException e) {
            logger.error("GPU verileri okunurken hata oluştu", e);
            throw e;
        }
    }

    public List<GpuInfo> getAllGpus() {
        return gpuRepository.findAll();
    }

    public Optional<GpuInfo> getGpuById(Long id) {
        return gpuRepository.findById(id);
    }

    public GpuInfo saveGpu(GpuInfo gpu) {
        return gpuRepository.save(gpu);
    }
} 