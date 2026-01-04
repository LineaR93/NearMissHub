package com.epicode.NearMissHub.controllers;

import com.epicode.NearMissHub.entities.ReportArea;
import com.epicode.NearMissHub.entities.ReportStatus;
import com.epicode.NearMissHub.entities.RiskLevel;
import com.epicode.NearMissHub.entities.Role;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;


@RestController
@RequestMapping("/meta")
public class MetaController {

    @GetMapping("/report-areas")
    public List<String> areas() {
        return Arrays.stream(ReportArea.values())
                .map(Enum::name)
                .toList();
    }

    @GetMapping("/risk-levels")
    public List<String> riskLevels() {
        return Arrays.stream(RiskLevel.values())
                .map(Enum::name)
                .toList();
    }

    @GetMapping("/roles")
    public List<String> roles() {
        return Arrays.stream(Role.values())
                .map(Enum::name)
                .toList();
    }

    @GetMapping("/report-statuses")
    public List<String> reportStatuses() {
        return Arrays.stream(ReportStatus.values())
                .map(Enum::name)
                .toList();
    }
}