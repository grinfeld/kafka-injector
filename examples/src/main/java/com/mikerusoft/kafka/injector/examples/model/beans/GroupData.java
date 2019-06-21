package com.mikerusoft.kafka.injector.examples.model.beans;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GroupData {
    private String name;
    private String id;
    private String type;
}
