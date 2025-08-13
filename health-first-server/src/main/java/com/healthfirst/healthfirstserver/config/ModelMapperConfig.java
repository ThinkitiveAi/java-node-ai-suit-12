package com.healthfirst.healthfirstserver.config;

import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration class for ModelMapper.
 * Configures and provides a ModelMapper bean with custom mappings.
 */
@Configuration
public class ModelMapperConfig {

    /**
     * Creates and configures a ModelMapper instance.
     * 
     * @return a configured ModelMapper instance
     */
    @Bean
    public ModelMapper modelMapper() {
        ModelMapper modelMapper = new ModelMapper();
        
        // Configure the ModelMapper instance
        modelMapper.getConfiguration()
            // Use strict matching strategy to avoid ambiguity
            .setMatchingStrategy(MatchingStrategies.STRICT)
            // Skip null values during mapping
            .setSkipNullEnabled(true)
            // Ensure all destination properties are mapped or explicitly ignored
            .setAmbiguityIgnored(false)
            // Enable field matching
            .setFieldMatchingEnabled(true)
            // Allow private fields to be accessed
            .setFieldAccessLevel(org.modelmapper.config.Configuration.AccessLevel.PRIVATE);
        
        // Add any custom type mappings here if needed
        // modelMapper.typeMap(Source.class, Destination.class).addMappings(...);
        
        return modelMapper;
    }
}
