package com.mssiz.rest.entities;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import org.springframework.http.HttpMethod;

import java.time.LocalDateTime;

@Entity
@Table(name = "AUDIT")
public class AuditRequest extends Request {
    @JsonProperty
    private final String username;
    @JsonProperty
    private final LocalDateTime datetime;
    @JsonProperty
    private final Boolean access;

    public AuditRequest() {
        super();
        this.username = null;
        this.datetime = LocalDateTime.now();
        this.access = null;
    }

    public AuditRequest(String username, String url, String body, HttpMethod method, Boolean access) {
        super(url, body, method);
        this.username = username;
        this.datetime = LocalDateTime.now();
        this.access = access;
    }
}