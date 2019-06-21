package com.mikerusoft.kafka.injector.examples.model.beans;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ArchiveRawMessageForReport {
    private NormalizedDevice owner;
    private String messageId;
    private String messageType;

    private Timestamp messageTime;
    private NormalizedDevice sender;
    private List<NormalizedDevice> recipients;
    private String direction;
    private String messageStatus;
}
