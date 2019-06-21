package com.mikerusoft.kafka.injector.examples.model.beans.wrappers;

import com.mikerusoft.kafka.injector.examples.model.beans.StamMessage;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class StamMessageWrapper extends IncomingDataWrapper<StamMessage> {
    public StamMessageWrapper(String networkType, String sourceType, StamMessage body, boolean kafkafied) {
        super(networkType, sourceType, body, kafkafied);
    }
}
