package com.midpoint.demo.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ObjectModification {
    private List<ItemDelta> itemDelta;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ItemDelta {
        private String modificationType;
        private String path;
        private List<Object> value;
    }

    public static ObjectModification replace(String path, Object value) {
        return replace(Map.of(path, value));
    }

    public static ObjectModification replace(Map<String, Object> updates) {
        List<ItemDelta> deltas = updates.entrySet().stream()
                .map(entry -> ItemDelta.builder()
                        .modificationType("replace")
                        .path(entry.getKey())
                        .value(List.of(entry.getValue()))
                        .build())
                .toList();
        return ObjectModification.builder()
                .itemDelta(deltas)
                .build();
    }
}
