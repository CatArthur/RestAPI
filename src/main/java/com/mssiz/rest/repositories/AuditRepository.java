package com.mssiz.rest.repositories;

import com.mssiz.rest.entities.AuditRequest;
import org.springframework.data.repository.CrudRepository;
public interface AuditRepository extends CrudRepository<AuditRequest, Long> {}