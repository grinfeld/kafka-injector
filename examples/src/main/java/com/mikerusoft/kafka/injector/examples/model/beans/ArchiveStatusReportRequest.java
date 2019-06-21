package com.mikerusoft.kafka.injector.examples.model.beans;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ArchiveStatusReportRequest {
    private String storage;
    private String networkType;
    private String sourceType;
    private Map<String, String> extraStorageParams;

    private ArchiveRawMessageForReport archiveRawMessageForReport;
    private RegArchiveUser owner;
    private ReportData reportData;
}
