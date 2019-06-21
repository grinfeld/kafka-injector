package com.mikerusoft.kafka.injector.examples.model;

import com.mikerusoft.kafka.injector.core.generate.model.SpecificDataGenerator;
import com.mikerusoft.kafka.injector.examples.model.beans.ArchiveStatusReportRequest;
import com.mikerusoft.kafka.injector.examples.model.beans.ArchiveStatusReportRequestBuilder;
import com.mikerusoft.kafka.injector.examples.model.beans.wrappers.ArchiveStatusReportRequestWrapper;
import com.mikerusoft.kafka.injector.core.utils.Pair;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

public class ArchiveStatusReportRequestGenerator extends SpecificDataGenerator<ArchiveStatusReportRequestWrapper, ArchiveStatusReportRequestGenerator.Builder> {

    @Override
    protected Builder createBuilder() {
        return new Builder();
    }

    @Override
    protected ArchiveStatusReportRequestWrapper generateObject(Builder builder) {
        ArchiveStatusReportRequestWrapper request = new ArchiveStatusReportRequestWrapper();
        request.setKafkafied(builder.isKafkafied());
        if (builder.getRequests() == null || builder.getRequests().isEmpty())
            return request;

        request.setBody(convertRequests(builder.getRequests()));

        return request;
    }

    private List<ArchiveStatusReportRequest> convertRequests(List<ArchiveStatusReportRequestBuilder> requests) {
        return requests.stream().map(this::convertRequest).collect(Collectors.toList());
    }

    private ArchiveStatusReportRequest convertRequest(ArchiveStatusReportRequestBuilder r) {
        ArchiveStatusReportRequest request = new ArchiveStatusReportRequest();
        request.setArchiveRawMessageForReport(r.getArchiveRawMessageForReport());
        request.setNetworkType(r.getNetworkType());
        request.setSourceType(r.getSourceType());
        request.setOwner(r.getOwner());
        request.setStorage(r.getStorage());
        request.setReportData(r.getReportData());
        if (r.getExtraStorageParams() != null)
            request.setExtraStorageParams(r.getExtraStorageParams().stream().collect(Collectors.toMap(Pair::getLeft, Pair::getRight, (k1,k2) -> k1)));
        return request;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Builder {
        private List<ArchiveStatusReportRequestBuilder> requests;
        private boolean kafkafied;
    }

}
