package com.application.mrmason.service.impl;

import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;

import com.application.mrmason.dto.UpdateSiteMeasurementStatusRequestDTO;
import com.application.mrmason.dto.UpdateSiteMeasurementStatusResponseDTO;
import com.application.mrmason.entity.AdminDetails;
import com.application.mrmason.entity.CustomerRegistration;
import com.application.mrmason.entity.SiteMeasurement;
import com.application.mrmason.entity.UpdateSiteMeasurementStatus;
import com.application.mrmason.entity.User;
import com.application.mrmason.entity.UserType;
import com.application.mrmason.enums.RegSource;
import com.application.mrmason.exceptions.ResourceNotFoundException;
import com.application.mrmason.repository.AdminDetailsRepo;
import com.application.mrmason.repository.CustomerRegistrationRepo;
import com.application.mrmason.repository.SiteMeasurementRepository;
import com.application.mrmason.repository.UpdateSiteMeasurementStatusRepository;
import com.application.mrmason.repository.UserDAO;
import com.application.mrmason.security.AuthDetailsProvider;
import com.application.mrmason.service.SiteMeasurementService;
import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.property.UnitValue;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

@Service
@Slf4j
public class SiteMeasurementServiceImpl implements SiteMeasurementService {

	@Autowired
	private SiteMeasurementRepository repository;

	@PersistenceContext
	private EntityManager entityManager;

	@Autowired
	CustomerRegistrationRepo repo;

	@Autowired
	public AdminDetailsRepo adminRepo;

	@Autowired
	UserDAO userDAO;
	
	@Autowired
	private UpdateSiteMeasurementStatusRepository updateSMSR;
	
	@Autowired
	private EmailServiceImpl emailService;

	@Override
	public List<SiteMeasurement> addSiteMeasurement(List<SiteMeasurement> measurements, RegSource regSource) {
		String loggedInUserEmail = AuthDetailsProvider.getLoggedEmail();
		Collection<? extends GrantedAuthority> loggedInRole = AuthDetailsProvider.getLoggedRole();

		List<String> roleNames = loggedInRole.stream().map(GrantedAuthority::getAuthority)
				.map(role -> role.replace("ROLE_", "")).collect(Collectors.toList());

		if (roleNames.equals("Developer")) {
			throw new ResourceNotFoundException("Restricted role: " + roleNames);
		}

		UserType userType = UserType.valueOf(roleNames.get(0));

		// User identity variables
		String userId;

		if (userType == UserType.EC) {
			// EC User
			CustomerRegistration customer = repo.findByUserEmailAndUserType(loggedInUserEmail, userType)
					.orElseThrow(() -> new ResourceNotFoundException("Customer not found: " + loggedInUserEmail));

			userId = customer.getUserid();

		} else if (userType == UserType.Adm) {
			// Admin User
			AdminDetails admin = adminRepo.findByEmailAndUserType(loggedInUserEmail, userType)
					.orElseThrow(() -> new ResourceNotFoundException("Admin not found: " + loggedInUserEmail));

			userId = admin.getEmail();// or any other logic you want to follow

		} else {
			// Other user types
			User user = userDAO.findByEmailAndUserTypeAndRegSource(loggedInUserEmail, userType, regSource)
					.orElseThrow(() -> new ResourceNotFoundException("User not found: " + loggedInUserEmail));

			userId = user.getBodSeqNo(); // modify if needed
		}
		List<SiteMeasurement> savedList = new ArrayList<>();
		Date currentDate = new Date();

		for(SiteMeasurement measurement: measurements) {
			measurement.setUpdatedBy(userId);
			measurement.setUpdatedDate(new Date());
			measurement.setRequestDate(new Date());
			measurement.setStatus("NEW");

			SiteMeasurement saved = repository.save(measurement);
			savedList.add(saved);

			CustomerRegistration customer = repo.findByUserids(saved.getCustomerId())
					.orElseThrow(() -> new ResourceNotFoundException("Customer not found for ID: " + saved.getCustomerId()));

			String LocalDateTime = "";
			String body = "<div style='font-family: Arial, sans-serif; color: #333; max-width: 600px;'>"
					+ "<p>Dear <b>" + customer.getCustomerName() + "</b>,</p>"
					+ "<p>Your site measurement request has been successfully recorded. Below are the details:</p>"
					+ "<table border='1' cellpadding='8' cellspacing='0' style='border-collapse: collapse; width: 100%; border: 1px solid #ddd;'>"
					+ "  <tr style='background-color: #f2f2f2;'><th style='text-align: left;'>Field</th><th style='text-align: left;'>Details</th></tr>"
					+ "  <tr><td><b>Customer Name</b></td><td>" + customer.getCustomerName() + "</td></tr>"
					+ "  <tr><td><b>Service Request Id</b></td><td>" + (saved.getServiceRequestId() != null ? saved.getServiceRequestId() : "N/A") + "</td></tr>"
					+ "  <tr><td><b>Location</b></td><td>" + (saved.getLocation() != null ? saved.getLocation() : "N/A") + "</td></tr>"
					+ "  <tr><td><b>Status</b></td><td>" + saved.getStatus() + "</td></tr>"
					+ "  <tr><td><b>Expected Start Date</b></td><td>" + saved.getExpectedStartDate() + "</td></tr>"
					+ "  <tr><td><b>West Site Length</b></td><td>" + measurement.getWestSiteLength() + "</td></tr>"
					+ "  <tr><td><b>South Site Length</b></td><td>" + measurement.getSouthSiteLength() + "</td></tr>"
					+ "  <tr><td><b>North Site Length</b></td><td>" + measurement.getNorthSiteLength() + "</td></tr>"
					+ "  <tr><td><b>East Site Length</b></td><td>" + measurement.getEastSiteLength() + "</td></tr>"
					+ "  <tr><td><b>Expected Bed Rooms</b></td><td>" + (saved.getExpectedBedRooms() != null ? saved.getExpectedBedRooms() : "N/A") + "</td></tr>"
					+ "  <tr><td><b>Expected Attached Bathrooms</b></td><td>" + (saved.getExpectedAttachedBathRooms() != null ? saved.getExpectedAttachedBathRooms() : "N/A") + "</td></tr>"
					+ "  <tr><td><b>Expected Additional Bathrooms</b></td><td>" + (saved.getExpectedAdditionalBathRooms() != null ? saved.getExpectedAdditionalBathRooms() : "N/A") + "</td></tr>"
					+ "  <tr><td><b>Request Date</b></td><td>" + saved.getRequestDate() + "</td></tr>"
					+ "  <tr><td><b>Updated By</b></td><td>" + saved.getUpdatedBy() + "</td></tr>"
					+ "</table>"
					+ "<p style='margin-top: 15px;'>Please find the attached PDF document for complete details and specifications.</p>"
					+ "<p>Regards,<br><b>Mr Mason Team</b></p>"
					+ "</div>";
			// Generate PDF
			byte[] pdf = generateSiteMeasurementPdf(saved, customer);

		/*String subject = "Site Measurement Added Successfully";
		String body = "Dear " + customer.getCustomerName() + ",<br><br>" +
				"Your site measurement has been successfully recorded. See attached PDF.<br><br>Regards,<br>Mr Mason Team";*/

			emailService.sendEmailWithAttachment(customer.getUserEmail(),
					"Site Measurement Added Successfully",
					body,
					pdf,
					"SiteMeasurement.pdf");
		}

	    return savedList;
	}
	public byte[] generateSiteMeasurementPdf(SiteMeasurement measurement, CustomerRegistration customer) {
	    try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {

	        PdfWriter writer = new PdfWriter(outputStream);
	        PdfDocument pdf = new PdfDocument(writer);
	        Document document = new Document(pdf);

	        PdfFont font = PdfFontFactory.createFont(StandardFonts.HELVETICA);
	        float fontSize = 5f;

	        // Header
	        document.add(new Paragraph("Site Measurement Report").setFont(font).setFontSize(14).setBold());
	        document.add(new Paragraph("Customer: " + customer.getCustomerName()).setFont(font).setFontSize(fontSize));
	        document.add(new Paragraph("Service Request ID: " + measurement.getServiceRequestId())
	                .setFont(font).setFontSize(fontSize));
	        document.add(new Paragraph(" ")); // Spacer

	        // Table
	        Table table = new Table(UnitValue.createPercentArray(18)).useAllAvailableWidth();
	        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");

	        // Headers
	        String[] headers = {
	                "Service Request ID", "East Site Length", "West Site Length", "South Site Length", "North Site Length",
	                "Location", "Expected BedRooms", "Expected Attached BathRooms", "Expected Additional BathRooms",
	                "Expected Start Date", "Updated Date", "Updated By", "Customer ID", "User ID",
	                "Building Type", "No Of Floors", "Request Date", "Status"
	        };

	        for (String h : headers) {
	            table.addHeaderCell(new Cell().add(new Paragraph(h)).setFont(font).setFontSize(fontSize).setBold());
	        }

	        // Data row
	        table.addCell(new Cell().add(new Paragraph(measurement.getServiceRequestId())).setFont(font).setFontSize(fontSize));
	        table.addCell(new Cell().add(new Paragraph(measurement.getEastSiteLength())).setFont(font).setFontSize(fontSize));
	        table.addCell(new Cell().add(new Paragraph(measurement.getWestSiteLength())).setFont(font).setFontSize(fontSize));
	        table.addCell(new Cell().add(new Paragraph(measurement.getSouthSiteLength())).setFont(font).setFontSize(fontSize));
	        table.addCell(new Cell().add(new Paragraph(measurement.getNorthSiteLength())).setFont(font).setFontSize(fontSize));
	        table.addCell(new Cell().add(new Paragraph(measurement.getLocation())).setFont(font).setFontSize(fontSize));
	        table.addCell(new Cell().add(new Paragraph(measurement.getExpectedBedRooms())).setFont(font).setFontSize(fontSize));
	        table.addCell(new Cell().add(new Paragraph(measurement.getExpectedAttachedBathRooms())).setFont(font).setFontSize(fontSize));
	        table.addCell(new Cell().add(new Paragraph(measurement.getExpectedAdditionalBathRooms())).setFont(font).setFontSize(fontSize));
	        table.addCell(new Cell().add(new Paragraph(sdf.format(measurement.getExpectedStartDate()))).setFont(font).setFontSize(fontSize));
	        table.addCell(new Cell().add(new Paragraph(sdf.format(measurement.getUpdatedDate()))).setFont(font).setFontSize(fontSize));
	        table.addCell(new Cell().add(new Paragraph(measurement.getUpdatedBy())).setFont(font).setFontSize(fontSize));
	        table.addCell(new Cell().add(new Paragraph(measurement.getCustomerId())).setFont(font).setFontSize(fontSize));
//	        table.addCell(new Cell().add(new Paragraph(measurement.getUserId() == null ? "null" : measurement.getUserId())).setFont(font).setFontSize(fontSize));
	        table.addCell(new Cell().add(new Paragraph(measurement.getBuildingType())).setFont(font).setFontSize(fontSize));
	        table.addCell(new Cell().add(new Paragraph(measurement.getNoOfFloors())).setFont(font).setFontSize(fontSize));
	        table.addCell(new Cell().add(new Paragraph(sdf.format(measurement.getRequestDate()))).setFont(font).setFontSize(fontSize));
	        table.addCell(new Cell().add(new Paragraph(measurement.getStatus())).setFont(font).setFontSize(fontSize));

	        // Add to document
	        document.add(table);

	        // Footer
	        document.add(new Paragraph("\nThank you,\nMr Mason Team").setFont(font).setFontSize(fontSize));

	        document.close();
	        return outputStream.toByteArray();

	    } catch (Exception e) {
	        throw new RuntimeException("Failed to generate PDF", e);
	    }
	}

	@Override
	public SiteMeasurement updateSiteMeasurement(SiteMeasurement measurement, RegSource regSource) {
		String loggedInUserEmail = AuthDetailsProvider.getLoggedEmail();
		Collection<? extends GrantedAuthority> loggedInRole = AuthDetailsProvider.getLoggedRole();

		List<String> roleNames = loggedInRole.stream().map(GrantedAuthority::getAuthority)
				.map(role -> role.replace("ROLE_", "")).collect(Collectors.toList());

		if (roleNames.equals("Developer")) {
			throw new ResourceNotFoundException("Restricted role: " + roleNames);
		}

		UserType userType = UserType.valueOf(roleNames.get(0));

		// User identity variables
		String userId;

		if (userType == UserType.EC) {
			// EC User
			CustomerRegistration customer = repo.findByUserEmailAndUserType(loggedInUserEmail, userType)
					.orElseThrow(() -> new ResourceNotFoundException("Customer not found: " + loggedInUserEmail));

			userId = customer.getUserid();

		} else if (userType == UserType.Adm) {
			// Admin User
			AdminDetails admin = adminRepo.findByEmailAndUserType(loggedInUserEmail, userType)
					.orElseThrow(() -> new ResourceNotFoundException("Admin not found: " + loggedInUserEmail));

			userId = admin.getEmail();// or any other logic you want to follow

		} else {
			// Other user types
			User user = userDAO.findByEmailAndUserTypeAndRegSource(loggedInUserEmail, userType, regSource)
					.orElseThrow(() -> new ResourceNotFoundException("User not found: " + loggedInUserEmail));

			userId = user.getBodSeqNo(); // modify if needed
		}

		SiteMeasurement existingMeasurement = repository.findByServiceRequestId(measurement.getServiceRequestId());
		if (existingMeasurement != null) {
			existingMeasurement.setEastSiteLength(measurement.getEastSiteLength());
			existingMeasurement.setWestSiteLength(measurement.getWestSiteLength());
			existingMeasurement.setSouthSiteLength(measurement.getSouthSiteLength());
			existingMeasurement.setNorthSiteLength(measurement.getNorthSiteLength());
			existingMeasurement.setLocation(measurement.getLocation());
			existingMeasurement.setExpectedBedRooms(measurement.getExpectedBedRooms());
			existingMeasurement.setExpectedAttachedBathRooms(measurement.getExpectedAttachedBathRooms());
			existingMeasurement.setExpectedAdditionalBathRooms(measurement.getExpectedAdditionalBathRooms());
			existingMeasurement.setExpectedStartDate(measurement.getExpectedStartDate());
//			existingMeasurement.setUserId(measurement.getUserId());
			existingMeasurement.setUpdatedBy(userId);
			existingMeasurement.setNoOfFloors(measurement.getNoOfFloors());
			existingMeasurement.setBuildingType(measurement.getBuildingType());
			SiteMeasurement saved =  repository.save(existingMeasurement);
			CustomerRegistration customer = repo.findByUserids(existingMeasurement.getCustomerId())
	                .orElseThrow(() -> new ResourceNotFoundException("Customer not found for ID: " + saved.getCustomerId()));

	        // Prepare email
	        String subject = "Site Measurement Updated Successfully";
	        String body = "Dear " + customer.getCustomerName() + ",<br><br>" +
	                      "Your site measurement has been updated. See attached PDF.<br><br>Regards,<br>Mr Mason Team";

	        // Generate PDF
	        byte[] pdf = generateSiteMeasurementPdf(saved, customer);

	        // Send email
	        emailService.sendEmailWithAttachment(customer.getUserEmail(), subject, body, pdf, "UpdatedSiteMeasurement.pdf");

	        return saved;
		}
		return null;
	}

//    @Override
//	public List<SiteMeasurement> getSiteMeasurement(String serviceRequestId, String eastSiteLegth, String location) {
//
//	    CriteriaBuilder cb = entityManager.getCriteriaBuilder();
//	    CriteriaQuery<SiteMeasurement> query = cb.createQuery(SiteMeasurement.class);
//	    Root<SiteMeasurement> root = query.from(SiteMeasurement.class);
//
//	    List<Predicate> predicates = new ArrayList<>();
//
//	    if (serviceRequestId != null && !serviceRequestId.trim().isEmpty()) {
//	        predicates.add(cb.equal(root.get("serviceRequestId"), serviceRequestId));
//	    }
//	    if (eastSiteLegth != null && !eastSiteLegth.trim().isEmpty()) {
//	        predicates.add(cb.equal(root.get("eastSiteLegth"), eastSiteLegth));
//	    }
//	    if (location != null && !location.trim().isEmpty()) {
//	        predicates.add(cb.equal(root.get("location"), location));
//	    }
//
//	    query.select(root);
//	    if (!predicates.isEmpty()) {
//	        query.where(cb.and(predicates.toArray(new Predicate[0]))); // ✅ FIX: using AND instead of OR
//	    }
//
//	    return entityManager.createQuery(query).getResultList();
//    }
//    @Override
//    public Page<SiteMeasurement> getSiteMeasurement(String serviceRequestId, String eastSiteLegth, String location,String userId,String updatedBy, Pageable pageable) {
//        
//    	String loggedInUserEmail = AuthDetailsProvider.getLoggedEmail();
//	    Collection<? extends GrantedAuthority> loggedInRole = AuthDetailsProvider.getLoggedRole();
//
//    	CriteriaBuilder cb = entityManager.getCriteriaBuilder();
//        CriteriaQuery<SiteMeasurement> query = cb.createQuery(SiteMeasurement.class);
//        Root<SiteMeasurement> root = query.from(SiteMeasurement.class);
//
//        List<Predicate> predicates = new ArrayList<>();
//
//        if (serviceRequestId != null && !serviceRequestId.trim().isEmpty()) {
//            predicates.add(cb.equal(root.get("serviceRequestId"), serviceRequestId));
//        }
//        if (eastSiteLegth != null && !eastSiteLegth.trim().isEmpty()) {
//            predicates.add(cb.equal(root.get("eastSiteLegth"), eastSiteLegth));
//        }
//        if (location != null && !location.trim().isEmpty()) {
//            predicates.add(cb.equal(root.get("location"), location));
//        }
//        if (userId != null && !userId.trim().isEmpty()) {
//            predicates.add(cb.equal(root.get("userId"), userId));
//        }
//        if (updatedBy != null && !updatedBy.trim().isEmpty()) {
//            predicates.add(cb.equal(root.get("updatedBy"), updatedBy));
//        }
//
//        query.select(root);
//        if (!predicates.isEmpty()) {
//            query.where(cb.and(predicates.toArray(new Predicate[0])));
//        }
//
//        TypedQuery<SiteMeasurement> typedQuery = entityManager.createQuery(query);
//        typedQuery.setFirstResult((int) pageable.getOffset());
//        typedQuery.setMaxResults(pageable.getPageSize());
//
//        // For total count
//        CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
//        Root<SiteMeasurement> countRoot = countQuery.from(SiteMeasurement.class);
//
//        List<Predicate> countPredicates = new ArrayList<>();
//        if (serviceRequestId != null && !serviceRequestId.trim().isEmpty()) {
//            countPredicates.add(cb.equal(countRoot.get("serviceRequestId"), serviceRequestId));
//        }
//        if (eastSiteLegth != null && !eastSiteLegth.trim().isEmpty()) {
//            countPredicates.add(cb.equal(countRoot.get("eastSiteLegth"), eastSiteLegth));
//        }
//        if (location != null && !location.trim().isEmpty()) {
//            countPredicates.add(cb.equal(countRoot.get("location"), location));
//        }
//        if (userId != null && !userId.trim().isEmpty()) {
//        	countPredicates.add(cb.equal(countRoot.get("userId"), userId));
//        }
//        if (updatedBy != null && !updatedBy.trim().isEmpty()) {
//            countPredicates.add(cb.equal(countRoot.get("updatedBy"), updatedBy));
//        }
//
//        countQuery.select(cb.count(countRoot));
//        if (!countPredicates.isEmpty()) {
//            countQuery.where(cb.and(countPredicates.toArray(new Predicate[0])));
//        }
//
//        Long total = entityManager.createQuery(countQuery).getSingleResult();
//
//        return new PageImpl<>(typedQuery.getResultList(), pageable, total);
//    }

	@Override
	public Page<SiteMeasurement> getSiteMeasurement(String serviceRequestId, String eastSiteLegth, String location,
			String userId, Date fromRequestDate, Date toRequestDate,String expectedFromMonth,
	        String expectedToMonth, List<String> customerIds,Pageable pageable) {
		// Step 4: Build Criteria Query
		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		CriteriaQuery<SiteMeasurement> query = cb.createQuery(SiteMeasurement.class);
		Root<SiteMeasurement> root = query.from(SiteMeasurement.class);

		List<Predicate> predicates = new ArrayList<>();

		if (serviceRequestId != null && !serviceRequestId.trim().isEmpty()) {
			predicates.add(cb.equal(root.get("serviceRequestId"), serviceRequestId));
		}
		if (eastSiteLegth != null && !eastSiteLegth.trim().isEmpty()) {
			predicates.add(cb.equal(root.get("eastSiteLength"), eastSiteLegth));
		}
		if (location != null && !location.trim().isEmpty()) {
			predicates.add(cb.equal(root.get("location"), location));
		}

		// Step 5: Use request-provided userId if present, else fallback to
		// loggedInUserId
		if (userId != null && !userId.trim().isEmpty()) {
			predicates.add(cb.equal(root.get("customerId"), userId));
		}
		if (customerIds != null && !customerIds.isEmpty()) {
		    predicates.add(root.get("customerId").in(customerIds));
		}

		if (fromRequestDate != null && toRequestDate != null) {
			predicates.add(cb.between(root.get("requestDate"), fromRequestDate, toRequestDate));
		} else if (fromRequestDate != null) {
			predicates.add(cb.greaterThanOrEqualTo(root.get("requestDate"), fromRequestDate));
		} else if (toRequestDate != null) {
			predicates.add(cb.lessThanOrEqualTo(root.get("requestDate"), toRequestDate));
		}
		
		Expression<Integer> monthExpr = cb.function("month", Integer.class, root.get("expectedStartDate"));
	    Expression<Integer> dayExpr = cb.function("day", Integer.class, root.get("expectedStartDate"));

	    if (expectedFromMonth != null && expectedToMonth != null) {
	        int fromMonth = Integer.parseInt(expectedFromMonth);
	        int toMonth = Integer.parseInt(expectedToMonth);

	        if (fromMonth <= toMonth) {
	            predicates.add(cb.between(monthExpr, fromMonth, toMonth));
	        } else {
	            // Handle year wrapping: e.g., Nov (11) to Feb (2)
	            predicates.add(cb.or(
	                cb.between(monthExpr, fromMonth, 12),
	                cb.between(monthExpr, 1, toMonth)
	            ));
	        }
	    } else if (expectedFromMonth != null) {
	        int fromMonth = Integer.parseInt(expectedFromMonth);
	        predicates.add(cb.equal(monthExpr, fromMonth));
	    } else if (expectedToMonth != null) {
	        int toMonth = Integer.parseInt(expectedToMonth);
	        predicates.add(cb.equal(monthExpr, toMonth));
	    }


		query.select(root);
		if (!predicates.isEmpty()) {
			query.where(cb.and(predicates.toArray(new Predicate[0])));
		}

		// Step 7: Pagination
		TypedQuery<SiteMeasurement> typedQuery = entityManager.createQuery(query);
		typedQuery.setFirstResult((int) pageable.getOffset());
		typedQuery.setMaxResults(pageable.getPageSize());

		// Step 8: Count query for total
		CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
		Root<SiteMeasurement> countRoot = countQuery.from(SiteMeasurement.class);

		List<Predicate> countPredicates = new ArrayList<>();
		if (serviceRequestId != null && !serviceRequestId.trim().isEmpty()) {
			countPredicates.add(cb.equal(countRoot.get("serviceRequestId"), serviceRequestId));
		}
		if (eastSiteLegth != null && !eastSiteLegth.trim().isEmpty()) {
			countPredicates.add(cb.equal(countRoot.get("eastSiteLength"), eastSiteLegth));
		}
		if (location != null && !location.trim().isEmpty()) {
			countPredicates.add(cb.equal(countRoot.get("location"), location));
		}
		if (userId != null && !userId.trim().isEmpty()) {
			countPredicates.add(cb.equal(countRoot.get("customerId"), userId));
		}

		if (fromRequestDate != null && toRequestDate != null) {
			countPredicates.add(cb.between(countRoot.get("requestDate"), fromRequestDate, toRequestDate));
		} else if (fromRequestDate != null) {
			countPredicates.add(cb.greaterThanOrEqualTo(countRoot.get("requestDate"), fromRequestDate));
		} else if (toRequestDate != null) {
			countPredicates.add(cb.lessThanOrEqualTo(countRoot.get("requestDate"), toRequestDate));
		}
		Expression<Integer> countMonthExpr = cb.function("month", Integer.class, countRoot.get("expectedStartDate"));
	    if (expectedFromMonth != null && expectedToMonth != null) {
	        int fromMonth = Integer.parseInt(expectedFromMonth);
	        int toMonth = Integer.parseInt(expectedToMonth);

	        if (fromMonth <= toMonth) {
	            countPredicates.add(cb.between(countMonthExpr, fromMonth, toMonth));
	        } else {
	            countPredicates.add(cb.or(
	                cb.between(countMonthExpr, fromMonth, 12),
	                cb.between(countMonthExpr, 1, toMonth)
	            ));
	        }
	    } else if (expectedFromMonth != null) {
	        int fromMonth = Integer.parseInt(expectedFromMonth);
	        countPredicates.add(cb.equal(countMonthExpr, fromMonth));
	    } else if (expectedToMonth != null) {
	        int toMonth = Integer.parseInt(expectedToMonth);
	        countPredicates.add(cb.equal(countMonthExpr, toMonth));
	    }

		countQuery.select(cb.count(countRoot));
		if (!countPredicates.isEmpty()) {
			countQuery.where(cb.and(countPredicates.toArray(new Predicate[0])));
		}

		Long total = entityManager.createQuery(countQuery).getSingleResult();

		// Step 9: Return paginated result
		return new PageImpl<>(typedQuery.getResultList(), pageable, total);
	}

	@Override
	public SiteMeasurement findByServiceRequestId(String serviceRequestId) {
		return repository.findByServiceRequestId(serviceRequestId);
	}

	@Override
	public UpdateSiteMeasurementStatusResponseDTO updateStatus(UpdateSiteMeasurementStatusRequestDTO dto, RegSource regSource) {
		String loggedInUserEmail = AuthDetailsProvider.getLoggedEmail();
		Collection<? extends GrantedAuthority> loggedInRole = AuthDetailsProvider.getLoggedRole();

		List<String> roleNames = loggedInRole.stream().map(GrantedAuthority::getAuthority)
				.map(role -> role.replace("ROLE_", "")).collect(Collectors.toList());

		if (roleNames.equals("Developer")) {
			throw new ResourceNotFoundException("Restricted role: " + roleNames);
		}

		UserType userType = UserType.valueOf(roleNames.get(0));

		// User identity variables
		String userId;

		if (userType == UserType.EC) {
			// EC User
			CustomerRegistration customer = repo.findByUserEmailAndUserType(loggedInUserEmail, userType)
					.orElseThrow(() -> new ResourceNotFoundException("Customer not found: " + loggedInUserEmail));

			userId = customer.getUserid();

		} else if (userType == UserType.Adm) {
			// Admin User
			AdminDetails admin = adminRepo.findByEmailAndUserType(loggedInUserEmail, userType)
					.orElseThrow(() -> new ResourceNotFoundException("Admin not found: " + loggedInUserEmail));

			userId = admin.getEmail();// or any other logic you want to follow

		} else {
			// Other user types
			User user = userDAO.findByEmailAndUserTypeAndRegSource(loggedInUserEmail, userType, regSource)
					.orElseThrow(() -> new ResourceNotFoundException("User not found: " + loggedInUserEmail));

			userId = user.getBodSeqNo(); // modify if needed
		}

		SiteMeasurement siteOpt = repository.findByServiceRequestId(dto.getServiceRequestId());
		if (siteOpt == null) {
			throw new RuntimeException("Invalid serviceRequestId. No SiteMeasurement found.");
		}

		// Save to update_site_measurement_status table
		UpdateSiteMeasurementStatus update = new UpdateSiteMeasurementStatus();
		update.setServiceRequestId(dto.getServiceRequestId());
		update.setUpdatedBy(userId);
		update.setStatus(dto.getStatus());
		update.setComments(dto.getComments());
		update.setUpdatedDate(new Date());
		updateSMSR.save(update);
		
		siteOpt.setStatus(dto.getStatus());
		siteOpt.setUpdatedBy(userId);
		siteOpt.setUpdatedDate(new Date());
		repository.save(siteOpt);

		// Build and return response DTO
		UpdateSiteMeasurementStatusResponseDTO response = new UpdateSiteMeasurementStatusResponseDTO();
		response.setServiceRequestId(update.getServiceRequestId());
		response.setUpdatedBy(update.getUpdatedBy());
		response.setStatus(update.getStatus());
		response.setComments(update.getComments());
		response.setUpdatedDate(update.getUpdatedDate());

		return response;
	}

	private String buildPlainTextEmailBody(CustomerRegistration customer, SiteMeasurement saved) {
		SimpleDateFormat formatter = new SimpleDateFormat("dd-MMM-yyyy HH:mm");
		String formattedDate = saved.getRequestDate() != null ? formatter.format(saved.getRequestDate()) : "N/A";

		Map<String, String> detailsMap = new LinkedHashMap<>();
		detailsMap.put("Customer Name", customer.getCustomerName() != null ? customer.getCustomerName() : "");
		detailsMap.put("Service Category", saved.getServiceRequestId() != null ? saved.getServiceRequestId() : "N/A");
		detailsMap.put("Location", saved.getLocation() != null ? saved.getLocation() : "N/A");
		detailsMap.put("Status", saved.getStatus() != null ? saved.getStatus() : "NEW");
		detailsMap.put("Request Date", formattedDate);
		detailsMap.put("Updated By", saved.getUpdatedBy() != null ? saved.getUpdatedBy() : "N/A");

		StringBuilder body = new StringBuilder();
		body.append("Dear ").append(detailsMap.get("Customer Name")).append(",\n\n");
		body.append("Your site measurement request has been successfully recorded. Below are the details:\n\n");

		for (Map.Entry<String, String> entry : detailsMap.entrySet()) {
			body.append(entry.getKey()).append(": ").append(entry.getValue()).append("\n");
		}

		body.append("\nPlease find the attached PDF document for complete details and specifications.\n\n");
		body.append("Regards,\nMr Mason Team");

		return body.toString();
	}

}