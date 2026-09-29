package com.application.mrmason.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.application.mrmason.entity.ElectricalMaster;

@Repository
public interface ElectricalMasterRepository
        extends JpaRepository<ElectricalMaster, String> {

   

}