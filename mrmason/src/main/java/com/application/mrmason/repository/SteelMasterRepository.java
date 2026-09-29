package com.application.mrmason.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.application.mrmason.entity.SteelMaster;

@Repository
public interface SteelMasterRepository extends JpaRepository<SteelMaster, String> {

}