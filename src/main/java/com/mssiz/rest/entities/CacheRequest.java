package com.mssiz.rest.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import org.springframework.http.HttpMethod;

@Entity
@Table(name = "CACHE")
public class CacheRequest extends Request{
    public CacheRequest() {
        super();
    }

    public CacheRequest(String url, String body, HttpMethod method) {
        super(url, body, method);
    }
}
