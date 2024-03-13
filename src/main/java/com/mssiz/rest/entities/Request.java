package com.mssiz.rest.entities;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRawValue;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;

@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class Request {
    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private Long id;

    @JsonProperty
    protected final String url;

    @JsonRawValue
    @Column(columnDefinition="TEXT")
    protected final String body;

    protected final HttpMethod method;

    @JsonGetter("method")
    public String getMethodName(){
        return method.name();
    }

    public Request() {
        this.url = null;
        this.body = null;
        this.method = HttpMethod.GET;
    }

    public Request(String url, String body, HttpMethod method) {
        this.url = url;
        this.body = body;
        this.method = method;
    }

    public String send(){
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> entity = new HttpEntity<>(body, headers);
        final RestTemplate restTemplate = new RestTemplate();
        return restTemplate.exchange(url, method, entity, String.class).getBody();
    }
}
