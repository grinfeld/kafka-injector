package com.mikerusoft.kafka.injector.examples.model.beans;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(builderClassName = "Builder")
public class StamMessage {
    private NormalizedDevice owner;
    private String messageId;
    private String messageType;

    private boolean isGroupMessage;
    private Date messageTime;
    private NormalizedDevice sender;
    private List<NormalizedDevice> recipients;
    private String direction;
    private String subject;
    private String text;
    private List<StamMessageAttachment> attachment;
    private String messageStatus;
    private CallInfo callInfo;
    private GroupData groupData;
    private String threadID;
    private String threadName;
}
