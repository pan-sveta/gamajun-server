package app.stepanek.gamajun.security;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;


import java.util.Date;
import java.util.List;

@Data
public class ZuulResponse {
    @JsonProperty("aud")
    List<String> audience;

    @JsonProperty("exp")
    Date expiration;

    @JsonProperty("user_name")
    String username;

    @JsonProperty("authorities")
    List<String> authorities;

    @JsonProperty("client_id")
    String clientId;

    @JsonProperty("scope")
    List<String> scope;
}
