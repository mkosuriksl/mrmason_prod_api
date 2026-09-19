package com.application.mrmason.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.application.mrmason.entity.ServicePersonLoginDAO;
import com.application.mrmason.enums.RegSource;

@Repository
public interface ServicePersonLoginDAO extends JpaRepository<ServicePersonLoginDAO, Long> {

	ServicePersonLoginDAO findByEmail(String email);

	@Query("SELECT u FROM ServicePersonLoginDAO u WHERE LOWER(u.email) = LOWER(:email)")
	List<ServicePersonLoginDAO> findByEmailOne(@Param("email") String email);

	Optional<ServicePersonLoginDAO> findByEmailAndRegSource(String email, RegSource regSource);

	@Query("SELECT s FROM ServicePersonLoginDAO s WHERE (s.email = :email OR s.mobile = :mobile) AND s.regSource = :regSource")
	Optional<ServicePersonLoginDAO> findByEmailOrMobileAndRegSource(String email, String mobile, RegSource regSource);

	@Query("SELECT s FROM ServicePersonLoginDAO s WHERE (s.email = :contact OR s.mobile = :contact) AND s.regSource = :regSource")
	ServicePersonLoginDAO findByEmailOrMobileAndRegSource(String contact, RegSource regSource);

	ServicePersonLoginDAO findByMobile(String mobile);

	Optional<ServicePersonLoginDAO> findByMobileAndRegSource(String mobile, RegSource regSource);
	
	@Query("SELECT s FROM ServicePersonLoginDAO s WHERE LOWER(s.email) = LOWER(:email) AND s.eOtp = :eOtp AND s.regSource = :regSource")
	ServicePersonLoginDAO findByEmailEOtpAndRegSourceIgnoreCase(@Param("email") String email, @Param("eOtp") String eOtp, @Param("regSource") RegSource regSource);

	@Query("SELECT s FROM ServicePersonLoginDAO s WHERE LOWER(s.mobile) = LOWER(:mobile) AND s.mOtp = :mOtp AND s.regSource = :regSource")
	ServicePersonLoginDAO findByMobileMOtpAndRegSourceIgnoreCase(@Param("mobile") String mobile, @Param("mOtp") String mOtp, @Param("regSource") RegSource regSource);

}
