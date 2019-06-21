package com.mikerusoft.kafka.injector.examples.model.beans;

import com.mikerusoft.kafka.injector.core.utils.Pair;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ArchiveStatusReportRequestBuilder {
    private String storage;
    private String networkType;
    private String sourceType;
    private List<Pair<String, String>> extraStorageParams;

    private ArchiveRawMessageForReport archiveRawMessageForReport;
    private RegArchiveUser owner;
    private ReportData reportData;
}
