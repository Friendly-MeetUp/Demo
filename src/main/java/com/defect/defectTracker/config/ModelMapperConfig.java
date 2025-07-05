package com.defect.defectTracker.config;

import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

// Try using a newer version of ModelMapper (3.2.0 is from 2022)
// Current version as of 2024 is 3.2.0, but check for newer versions
@Configuration
public class ModelMapperConfig {

    // Uncomment and configure if you need to use ModelMapper
    @Bean
    public ModelMapper modelMapper() {
        return new ModelMapper();
    }

    // Additional configuration can be added here if needed
}