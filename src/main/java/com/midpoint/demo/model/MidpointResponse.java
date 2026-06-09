package com.midpoint.demo.model;

import lombok.Data;

import java.util.List;

@Data
public class MidpointResponse {
    private InnerObject object;

    @Data
    public static class InnerObject {
        private List<User> object;
    }
}
