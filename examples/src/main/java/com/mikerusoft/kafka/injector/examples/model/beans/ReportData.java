package com.mikerusoft.kafka.injector.examples.model.beans;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReportData {
    private ReportStatus reportStatus;
    private String senderFormattedValue;
    private List<String> recipientsFormattedValue;
}
