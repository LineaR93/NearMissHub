package com.epicode.NearMissHub.payloads.response;

// API payload (request/response DTO). Bean Validation annotations trigger clean 400 errors.

public class KpiCountResponse {
    public String key;
    public long count;

    public KpiCountResponse(String key, long count) {
        this.key = key;
        this.count = count;
    }
}
