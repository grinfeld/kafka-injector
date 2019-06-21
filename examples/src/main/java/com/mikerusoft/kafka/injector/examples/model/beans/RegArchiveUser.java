package com.mikerusoft.kafka.injector.examples.model.beans;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@Data
@EqualsAndHashCode(callSuper = true)
public class RegArchiveUser extends ArchiveUser {
    private long id;
    private String firstName;
    private String lastName;

    public RegArchiveUser(List<NormalizedDevice> devices, long id, String firstName, String lastName) {
        super(devices);
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
    }
}