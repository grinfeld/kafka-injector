package com.mikerusoft.kafka.injector.examples.model.beans;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CallInfo {
    private Timestamp startTime;
    private Timestamp endTime;
    private String callStatus;
    private long callDurationInMillis;
}
