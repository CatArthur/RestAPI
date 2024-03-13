package com.mssiz.rest;

import com.mssiz.rest.entities.AuditRequest;
import com.mssiz.rest.entities.CacheRequest;
import com.mssiz.rest.entities.Request;
import com.mssiz.rest.repositories.AuditRepository;
import com.mssiz.rest.repositories.CacheRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.client.RestTemplate;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;


@org.springframework.web.bind.annotation.RestController
public class RestController {
    static final String PLACEHOLDER_URL = "http://jsonplaceholder.typicode.com";
    static final Logger log = LoggerFactory.getLogger(RestController.class);
    final CacheRepository cacheRepository;
    final AuditRepository auditRepository;

    public RestController(
            CacheRepository cacheRepository,
            AuditRepository auditRepository
    ) {
        this.cacheRepository = cacheRepository;
        this.auditRepository = auditRepository;
    }

    @GetMapping(value = "/api/{module}", produces = MediaType.APPLICATION_JSON_VALUE)
    public String getAll(
            @PathVariable String module,
            HttpServletRequest request,
            Principal principal
    ) {
        String user = principal == null ? null :principal.getName();
        String query = request.getQueryString();
        String requestUrl = PLACEHOLDER_URL + '/' + module + (query == null ? "": '?' + query);

        auditRepository.save(new AuditRequest(user, requestUrl, null, HttpMethod.GET, true));
        final RestTemplate restTemplate = new RestTemplate();
        return restTemplate.getForObject(requestUrl, String.class);
    }

    @GetMapping(value = "/api/{module}/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public String getSingle(
            @PathVariable String module,
            @PathVariable String id,
            Principal principal
    ) {
        String user = principal == null ? null :principal.getName();
        String requestUrl = PLACEHOLDER_URL + '/' + module + '/' + id;

        auditRepository.save(new AuditRequest(user, requestUrl, null, HttpMethod.GET, true));
        final RestTemplate restTemplate = new RestTemplate();
        return restTemplate.getForObject(requestUrl, String.class);
    }

    @PostMapping(value = "/api/{module}", produces = MediaType.APPLICATION_JSON_VALUE)
    public String postSingle(
            @PathVariable String module,
            @RequestBody String body,
            Principal principal
    ) {
        String user = principal == null ? null :principal.getName();
        String requestUrl = PLACEHOLDER_URL + '/' + module;
        cacheRepository.save(new CacheRequest(requestUrl, body, HttpMethod.POST));
        auditRepository.save(new AuditRequest(user, requestUrl, body, HttpMethod.POST, true));
        return body;
    }

    @PutMapping(value = "/api/{module}/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public String putSingle(
            @PathVariable String module,
            @PathVariable String id,
            @RequestBody String body,
            Principal principal
    ) {
        String user = principal == null ? null :principal.getName();
        String requestUrl = PLACEHOLDER_URL + '/' + module + '/' + id;
        cacheRepository.save(new CacheRequest(requestUrl, body, HttpMethod.PUT));
        auditRepository.save(new AuditRequest(user, requestUrl, body, HttpMethod.PUT, true));
        return body;
    }

    @DeleteMapping(value = "/api/{module}/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public String deleteSingle(
            @PathVariable String module,
            @PathVariable String id,
            Principal principal
    ) {
        String user = principal == null ? null :principal.getName();
        String requestUrl = PLACEHOLDER_URL + '/' + module + '/' + id;
        cacheRepository.save(new CacheRequest(requestUrl, null, HttpMethod.DELETE));
        auditRepository.save(new AuditRequest(user, requestUrl, null, HttpMethod.DELETE, true));
        return "{}";
    }

    @PostMapping(value = "/cache", produces = MediaType.APPLICATION_JSON_VALUE)
    public String updateCache() {
        Iterable<CacheRequest> requests = cacheRepository.findAll();
        StringBuilder responses = new StringBuilder();
        for (Request request: requests) {
            String response = request.send();
            responses.append(response).append(",\n");
        }
        responses.deleteCharAt(responses.length() - 2);
        cacheRepository.deleteAll();
        return "{\"message\": \"Cache updated successfully\", \"responses\":\n[\n" + responses + "]}";
    }

    @GetMapping(value = "/audit", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<AuditRequest> getAudit() {
        Iterable<AuditRequest> requests = auditRepository.findAll();
        List<AuditRequest> requestsInfo = new ArrayList<>();
        for (AuditRequest request: requests) {
            requestsInfo.add(request);
            log.info(request.toString());
        }
        return requestsInfo;
    }
}