package com.epicode.NearMissHub.controllers;

// REST controller: it receives HTTP requests, delegates to services/repositories, and returns DTOs.

import com.epicode.NearMissHub.payloads.response.KpiCountResponse;
import com.epicode.NearMissHub.repositories.NearMissReportRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/kpi")
public class KpiController {

    private final NearMissReportRepository reports;

    public KpiController(NearMissReportRepository reports) {
        this.reports = reports;
    }

    @GetMapping("/reports/by-status")
        public List<KpiCountResponse> byStatus() {
        return reports.countByStatus().stream()
                .map(r -> new KpiCountResponse(String.valueOf(r[0]), (Long) r[1]))
                .toList();
    }

    @GetMapping("/reports/by-category")
        public List<KpiCountResponse> byCategory() {
        return reports.countByCategoryName().stream()
                .map(r -> new KpiCountResponse(String.valueOf(r[0]), (Long) r[1]))
                .toList();
    }
}
