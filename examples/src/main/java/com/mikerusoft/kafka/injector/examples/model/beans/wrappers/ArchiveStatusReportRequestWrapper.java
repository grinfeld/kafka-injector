package com.mikerusoft.kafka.injector.examples.model.beans.wrappers;

import com.mikerusoft.kafka.injector.examples.model.beans.ArchiveStatusReportRequest;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ArchiveStatusReportRequestWrapper {
    private List<ArchiveStatusReportRequest> body;
    private boolean kafkafied;
}
