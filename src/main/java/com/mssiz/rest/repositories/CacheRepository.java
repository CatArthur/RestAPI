package com.mssiz.rest.repositories;

import com.mssiz.rest.entities.CacheRequest;
import org.springframework.data.repository.CrudRepository;
public interface CacheRepository extends CrudRepository<CacheRequest, Long> {}