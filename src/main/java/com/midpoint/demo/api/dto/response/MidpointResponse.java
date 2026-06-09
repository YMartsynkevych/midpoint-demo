package com.midpoint.demo.api.dto.response;

import com.midpoint.demo.domain.User;
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
