package blackjack.domain;

import java.util.Arrays;

public enum HitRequest {
    YES("y"),
    NO("n");

    private final String name;

    HitRequest(String name) {
        this.name = name;
    }

    public static HitRequest find(String requestName) {
        return Arrays.stream(HitRequest.values())
            .filter(value -> value.name.equals(requestName))
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException("[ERROR] 유효하지 않은 요청입니다."));
    }

    public boolean isGoingOn() {
        return this == YES;
    }
}
