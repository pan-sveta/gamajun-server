package app.stepanek.gamajun.security;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.core.convert.converter.Converter;
import org.springframework.http.*;
import org.springframework.http.client.support.BasicAuthenticationInterceptor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.core.DefaultOAuth2AuthenticatedPrincipal;
import org.springframework.security.oauth2.core.OAuth2AuthenticatedPrincipal;
import org.springframework.security.oauth2.server.resource.introspection.*;
import org.springframework.util.Assert;
import org.springframework.web.client.RestOperations;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;


import java.net.URI;
import java.time.Instant;
import java.util.*;

public class ZuulTokenIntrospector implements OpaqueTokenIntrospector {
    private final Log logger = LogFactory.getLog(this.getClass());
    private final RestOperations restOperations;
    private Converter<String, RequestEntity<?>> requestEntityConverter;

    public ZuulTokenIntrospector(String introspectionUri, String clientId, String clientSecret) {
        Assert.notNull(introspectionUri, "introspectionUri cannot be null");
        Assert.notNull(clientId, "clientId cannot be null");
        Assert.notNull(clientSecret, "clientSecret cannot be null");
        this.requestEntityConverter = this.defaultRequestEntityConverter(URI.create(introspectionUri));
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.getInterceptors().add(new BasicAuthenticationInterceptor(clientId, clientSecret));
        this.restOperations = restTemplate;
    }

    @Override
    public OAuth2AuthenticatedPrincipal introspect(String token) {
        ZuulResponse response = makeRequest(token);
        return mapPrincipal(response);
    }

    protected OAuth2AuthenticatedPrincipal mapPrincipal(ZuulResponse zuulResponse) {
        return new DefaultOAuth2AuthenticatedPrincipal(mapClaims(zuulResponse), mapAuthorities(zuulResponse));
    }

    private ZuulResponse makeRequest(String token) {
        RequestEntity<?> requestEntity = this.requestEntityConverter.convert(token);
        if (requestEntity == null) {
            throw new OAuth2IntrospectionException("requestEntityConverter returned a null entity");
        } else {
            ResponseEntity<ZuulResponse> response = this.makeRequest(requestEntity);

            if (response.getStatusCode() != HttpStatus.OK) {
                this.logger.trace("Introspection endpoint returned non-OK status code");
                throw new OAuth2IntrospectionException("Introspection endpoint responded with HTTP status code " + response.getStatusCode());
            }

            ZuulResponse zuulResponse = response.getBody();

            if (zuulResponse == null){
                this.logger.trace("Introspection endpoint returned empty or non-parsable response");
                throw new OAuth2IntrospectionException("Introspection endpoint returned empty or non-parsable response");
            }
            return zuulResponse;
        }
    }

    private Collection<GrantedAuthority> mapAuthorities(ZuulResponse response) {
        Collection<GrantedAuthority> authorities = new ArrayList();

        Iterator<String> it;
        if (response.getAuthorities() != null) {
            it = response.getAuthorities().iterator();

            while(it.hasNext()) {
                String authority = it.next();
                authorities.add(new SimpleGrantedAuthority(authority));
            }
        }

        return authorities;
    }

    private Map<String, Object> mapClaims(ZuulResponse response) {

        Map<String, Object> claims = new HashMap<>();

        Iterator<String> it;
        if (response.getAudience() != null) {
            List<String> audiences = new ArrayList<>();
            it = response.getAudience().iterator();

            while(it.hasNext()) {
                String audience = (String) it.next();
                audiences.add(audience);
            }

            claims.put("aud", Collections.unmodifiableList(audiences));
        }

        Instant iat;
        if (response.getExpiration() != null) {
            iat = response.getExpiration().toInstant();
            claims.put("exp", iat);
        }

        if (response.getUsername() != null) {
            claims.put("user_name", response.getUsername());
        }

        if (response.getClientId() != null) {
            claims.put("client_id", response.getClientId());
        }

        if (response.getScope() != null) {
            List<String> scopes = Collections.unmodifiableList(response.getScope());
            claims.put("scope", scopes);
        }

        return claims;
    }

    private ResponseEntity<ZuulResponse> makeRequest(RequestEntity<?> requestEntity) {
        try {
            return this.restOperations.exchange(requestEntity, ZuulResponse.class);
        } catch (Exception exp) {
            throw new BadOpaqueTokenException("Provided token isn't active");
        }
    }

    private Converter<String, RequestEntity<?>> defaultRequestEntityConverter(URI introspectionUri) {
        return (token) -> {
            HttpHeaders headers = this.requestHeaders();

            UriComponentsBuilder builder = UriComponentsBuilder.fromUri(introspectionUri);
            builder.queryParam("token",token);

            URI uri = builder.build().toUri();

            return new RequestEntity(null, headers, HttpMethod.GET, uri);
        };
    }

    private HttpHeaders requestHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        return headers;
    }


}
