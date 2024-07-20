package com.ms.assessment.config;

import com.ms.assessment.service.PricingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

@Configuration
@EnableScheduling
public class SchedulerConfig {

    @Autowired
    PricingService pricingService;

    @Value("${scheduler.fixedRate}")
    private long rate;

    @Scheduled(fixedRateString = "${scheduler.fixedRate}")
    public void fixedRateSceduler(){
        pricingService.fetchAndSavePricing();;
    }
}
