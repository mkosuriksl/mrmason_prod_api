package com.application.mrmason.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.application.mrmason.entity.CementMaster;

@Repository
public interface CementMasterRepository extends JpaRepository<CementMaster, String> {

}