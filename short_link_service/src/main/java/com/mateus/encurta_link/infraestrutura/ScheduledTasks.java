package com.mateus.encurta_link.infraestrutura;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.mateus.encurta_link.service.ShortLinkService;

@Component
public class ScheduledTasks {
    
    @Autowired
    private ShortLinkService shortLinkService;

    @Scheduled(cron = "0 0 * * * *")
    public void execute() {
        shortLinkService.removeExpiredLinks();
    }
}
