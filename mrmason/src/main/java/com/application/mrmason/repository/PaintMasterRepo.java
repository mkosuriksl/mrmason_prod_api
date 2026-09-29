package com.application.mrmason.repository;

import com.application.mrmason.entity.PaintMaster;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface PaintMasterRepo extends JpaRepository<PaintMaster,String> {

   // List<PaintMaster> findByIdAndBrand(int colorCode, String brand);

}
