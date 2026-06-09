package com.midpoint.demo.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SearchQuery {
    private Query query;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Query {
        private Filter filter;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Filter {
        private String text;
        private Object type;
    }

    public static SearchQuery byUsername(String username) {
        SearchQueryBuilder builder = SearchQuery.builder();
        if (username != null) {
            builder.query(Query.builder()
                    .filter(Filter.builder()
                            .text("name = \"" + username + "\"")
                            .build())
                    .build());
        }
        return builder.build();
    }

}
