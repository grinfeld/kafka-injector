package com.mikerusoft.kafka.injector.examples.model;

import com.mikerusoft.kafka.injector.core.generate.model.SpecificDataGenerator;
import com.mikerusoft.kafka.injector.examples.model.beans.*;
import com.mikerusoft.kafka.injector.examples.model.beans.wrappers.StamMessageWrapper;
import lombok.Data;

import java.sql.Timestamp;
import java.util.List;

public class StamMessageGenerator extends SpecificDataGenerator<StamMessageWrapper, StamMessageGenerator.Builder> {

    @Override
    protected Builder createBuilder() {
        return new Builder();
    }

    @Override
    protected StamMessageWrapper generateObject(Builder builder) {
        if (builder == null)
            throw new NullPointerException("builder is null");

        StamMessage rawMessage = StamMessage.builder()
                .callInfo(builder.callInfo).direction(builder.direction)
                .groupData(builder.groupData).isGroupMessage(builder.isGroupMessage)
                .messageId(builder.messageId).messageStatus(builder.messageStatus)
                .messageTime(builder.messageTime).messageType(builder.messageType)
                .owner(builder.owner).sender(builder.sender).subject(builder.subject)
                .text(builder.text).threadID(builder.threadID).threadName(builder.threadName)
                .attachment(builder.attachment).recipients(builder.recipients)
            .build();

        return new StamMessageWrapper(builder.networkType, builder.sourceType, rawMessage, builder.kafkafied);
    }

    @Data
    public static class Builder {
        private boolean kafkafied;
        private String networkType;
        private String sourceType;
        private NormalizedDevice owner;
        private String messageId;
        private String messageType;
        private boolean isGroupMessage;
        private Timestamp messageTime;
        private NormalizedDevice sender;
        private String direction;
        private String subject;
        private String text;
        private String messageStatus;
        private CallInfo callInfo;
        private GroupData groupData;
        private List<StamMessageAttachment> attachment;
        private List<NormalizedDevice> recipients;
        private String threadID;
        private String threadName;
    }
}
