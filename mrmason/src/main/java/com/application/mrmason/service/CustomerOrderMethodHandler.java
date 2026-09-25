package com.application.mrmason.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import com.application.mrmason.dto.*;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.application.mrmason.entity.AdminMaterialMaster;
import com.application.mrmason.entity.CustomerOrderDetailsEntity;
import com.application.mrmason.entity.CustomerOrderHdrEntity;
import com.application.mrmason.entity.CustomerRegistration;
import com.application.mrmason.entity.MaterialMaster;
import com.application.mrmason.entity.UserType;
import com.application.mrmason.enums.OrderStatus;
import com.application.mrmason.enums.RegSource;
import com.application.mrmason.exceptions.ResourceNotFoundException;
import com.application.mrmason.repository.AdminMaterialMasterRepository;
import com.application.mrmason.repository.CustomerRegistrationRepo;
import com.application.mrmason.repository.MaterialMasterRepository;
import com.application.mrmason.security.AuthDetailsProvider;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.transaction.Transactional;

@Service
public class CustomerOrderMethodHandler {

	@Autowired
	private CustomerOrderHdrRepo orderHdrRepo;
	@Autowired
	private CustomerOrderDetailsRepo orderDetailsRepo;
	@Autowired
	CustomerRegistrationRepo customerRegisterRepository;
	@PersistenceContext
	private EntityManager entityManager;
	@Autowired
	private ModelMapper modelMapper;
	@Autowired
	private MaterialMasterRepository materialMasterRepository;

	public CustomerOrderHdrEntity createCustomerOrderMethod(CustomerOrderRequestDto dto) {

		String loggedInUserEmail = AuthDetailsProvider.getLoggedEmail();
		Optional<CustomerRegistration> login = customerRegisterRepository
				.findByUserEmailAndUserTypeAndRegSource(loggedInUserEmail, UserType.EC, RegSource.MRMASON);
		String userId = login.get().getUserid();
		if (login.isEmpty()) {
			throw new ResourceNotFoundException("Access denied. This API is restricted to customer users only.");
		}

//		List<AdminMaterialMaster> storeList = adminMaterialMasterRepository.findByUpdatedBy(dto.getUserId());
//		if (storeList.isEmpty()) {
//		    throw new ResourceNotFoundException("UserId not found for id: " + dto.getUserId());
//		}
		// pick first if needed
//		AdminMaterialMaster store = storeList.get(0);

		// 🔹 Step 1: Find existing PENDING order (fetch details eagerly)
		Optional<CustomerOrderHdrEntity> existingOrderOpt = orderHdrRepo
				.findWithDetailsByUpdatedByAndStatus(login.get().getUserid(), OrderStatus.PENDING);

		CustomerOrderHdrEntity orderHdr;
		String baseOrderId;
		int counter;

		if (existingOrderOpt.isPresent()) {
			// ✅ Reuse existing header
			orderHdr = existingOrderOpt.get();
			baseOrderId = orderHdr.getOrderId();

			// Get max counter from existing orderlines
			int maxCounter = orderHdr.getCustomerOrderDetailsEntities().stream()
					.map(CustomerOrderDetailsEntity::getOrderlineId).map(id -> id.substring(id.lastIndexOf("_") + 1))
					.mapToInt(Integer::parseInt).max().orElse(0);

			counter = maxCounter + 1;
		} else {
			// ✅ Create new header
			orderHdr = new CustomerOrderHdrEntity();

			Date date = new Date();
			orderHdr.setStatus(OrderStatus.PENDING);
            orderHdr.setOrderDate(date);
			orderHdr.setOrderUpdatedBy(userId);
			orderHdr.setOrderUpdatedDate(date);
			orderHdr.setUpdatedBy(login.get().getUserid());
			orderHdr.setUpdatedDate(new Date());
			orderHdr.setDeliveryDate(dto.getDeliveryDate());
			orderHdr.setDeliveryLocation(dto.getDeliveryLocation());
			orderHdr.setPincode(dto.getPincode());
//			orderHdr.setSkuIdUserId(dto.getOrderDetailsList().get(0).getSkuIdUserId());

			orderHdrRepo.save(orderHdr);

			baseOrderId = orderHdr.getOrderId();
			counter = 1;
		}

		// 🔹 Step 2: Create and save new details
		List<CustomerOrderDetailsDto> orderDetailsList = dto.getOrderDetailsList();
		List<CustomerOrderDetailsEntity> detailEntities = new ArrayList<>();

		for (CustomerOrderDetailsDto orderDetails : orderDetailsList) {

			List<MaterialMaster> stockList = materialMasterRepository
					.findByMsCatmsSubCatmsBrandSkuIdS(orderDetails.getSkuIdUserId());

			if (stockList.isEmpty()) {
				throw new ResourceNotFoundException("SkuIdUserId not found for item: " + orderDetails.getSkuIdUserId());
			}

			// Optional: pick the correct one based on updatedBy / location
			MaterialMaster stockEntity = stockList.get(0); // or filter by updatedBy

			String uniqueKey = stockEntity.getMsCatmsSubCatmsBrandSkuId();

			// Prevent duplicates in same order
			boolean exists = orderHdr.getCustomerOrderDetailsEntities() != null && orderHdr
					.getCustomerOrderDetailsEntities().stream().anyMatch(d -> d.getSkuIdUserId().equals(uniqueKey));

			if (exists) {
				throw new IllegalArgumentException("Item already exists in order: " + orderDetails.getSkuIdUserId());
			}

			CustomerOrderDetailsEntity orderDetailsEntity = new CustomerOrderDetailsEntity();

			orderDetailsEntity.setSkuIdUserId(stockEntity.getMsCatmsSubCatmsBrandSkuId());
			orderDetailsEntity.setMsUserId(stockEntity.getUpdatedBy());
			orderDetailsEntity.setMrp(orderDetails.getMrp());
			orderDetailsEntity.setDiscount(orderDetails.getDiscount());
			orderDetailsEntity.setGst(orderDetails.getGst());
			orderDetailsEntity.setTotal(orderDetails.getTotal());
			orderDetailsEntity.setShape(orderDetails.getShape());
			orderDetailsEntity.setWidth(orderDetails.getWidth());
			orderDetailsEntity.setSize(orderDetails.getSize());
			orderDetailsEntity.setThickness(orderDetails.getThickness());
			orderDetailsEntity.setOrderQty(orderDetails.getOrderQty());
			orderDetailsEntity.setDeliveryDate(orderHdr.getDeliveryDate());
			orderDetailsEntity.setDeliveryLocation(orderHdr.getDeliveryLocation());
			orderDetailsEntity.setPincode(orderHdr.getPincode());

//		    orderDetailsEntity.setPrescriptionRequired(orderDetails.getPrescriptionRequired());

			orderDetailsEntity.setBrand(orderDetails.getBrand());
			orderDetailsEntity.setMaterialCategory(stockEntity.getMaterialCategory());
			orderDetailsEntity.setMaterialSubCategory(stockEntity.getMaterialSubCategory());
			orderDetailsEntity.setModelName(stockEntity.getModelName());
			orderDetailsEntity.setOrderlineId(baseOrderId + "_" + String.format("%03d", counter++));
			orderDetailsEntity.setStatus(OrderStatus.PENDING);
			orderDetailsEntity.setUpdatedBy(login.get().getUserid());
			orderDetailsEntity.setUpdatedDate(new Date());

			orderDetailsEntity.setCustomerOrderOrderHdrEntity(orderHdr);

			detailEntities.add(orderDetailsEntity);
			orderDetailsRepo.save(orderDetailsEntity);
		}

		// 🔹 Step 3: Attach details to header
		if (orderHdr.getCustomerOrderDetailsEntities() == null) {
			orderHdr.setCustomerOrderDetailsEntities(new ArrayList<>());
		}
		orderHdr.getCustomerOrderDetailsEntities().addAll(detailEntities);

		return orderHdr;
	}

	public UpdateCustomerOrderResponseDto updateOrderDetails(UpdateCustomerOrderRequestDto dto) {
		String orderId = dto.getOrderId();

		String loggedInUserEmail = AuthDetailsProvider.getLoggedEmail();
		Optional<CustomerRegistration> login = customerRegisterRepository
				.findByUserEmailAndUserTypeAndRegSource(loggedInUserEmail, UserType.EC, RegSource.MRMASON);

		if (login.isEmpty()) {
			throw new ResourceNotFoundException("Access denied. This API is restricted to customer users only.");
		}

		String userId = login.get().getUserid();
		Date currentDate = new Date();

		// 1️⃣ Update Header audit details if header exists
		CustomerOrderHdrEntity orderHdr = orderHdrRepo.findById(orderId)
				.orElseThrow(() -> new RuntimeException("OrderId not found: " + orderId));

		orderHdr.setUpdatedBy(userId);
		orderHdr.setUpdatedDate(currentDate);
		orderHdr.setOrderUpdatedBy(userId);
		orderHdr.setOrderUpdatedDate(currentDate);
		orderHdrRepo.save(orderHdr);

		List<CustomerOrderDetailsEntity> updatedEntities = new ArrayList<>();

		// 2️⃣ Update Line Items
		for (CustomerOrderDetailsDto details : dto.getOrderDetailsList()) {
			String orderlineId = details.getOrderlineId();

			if (orderlineId == null || !orderlineId.contains("_")) {
				throw new RuntimeException("Invalid orderlineId format: " + orderlineId);
			}

			String prefix = orderlineId.split("_")[0];
			if (!prefix.equals(orderId)) {
				throw new RuntimeException(
						"Mismatch: orderlineId '" + orderlineId + "' does not belong to orderId '" + orderId + "'");
			}

			CustomerOrderDetailsEntity entity = orderDetailsRepo.findByOrderlineId(orderlineId)
					.orElseThrow(() -> new RuntimeException("OrderLineId not found: " + orderlineId));

			if (details.getOrderQty() != null) entity.setOrderQty(details.getOrderQty());
			if (details.getTotal() != null) entity.setTotal(details.getTotal());
			if (details.getMrp() != null) entity.setMrp(details.getMrp());
			if (details.getDiscount() != null) entity.setDiscount(details.getDiscount());
			if (details.getGst() != null) entity.setGst(details.getGst());
			if (details.getBrand() != null) entity.setBrand(details.getBrand());
			if (details.getShape() != null) entity.setShape(details.getShape());
			if (details.getWidth() != null) entity.setWidth(details.getWidth());
			if (details.getSize() != null) entity.setSize(details.getSize());
			if (details.getThickness() != null) entity.setThickness(details.getThickness());


			entity.setUpdatedBy(userId);
			entity.setUpdatedDate(currentDate);

			CustomerOrderDetailsEntity saved = orderDetailsRepo.save(entity);
			updatedEntities.add(saved);
		}
		List<CustomerOrderDetailsDto> dtoList = updatedEntities.stream()
				.map(e -> modelMapper.map(e, CustomerOrderDetailsDto.class))
				.collect(Collectors.toList());

		return new UpdateCustomerOrderResponseDto(orderId, userId, currentDate, dtoList);
	}


	@Transactional
	public GenericResponse<List<CustomerGetOrderResponseDTO>> getOrderDetail(String customerId, String orderid,
			String orderlineId, String skuIdUserId, String fromDate, String toDate, String msUserId, Integer page,
			Integer size) {

		// 1️⃣ Fetch all matching records without pagination
		List<CustomerOrderDetailsEntity> allEntities = fetchFilteredOrderDetailsWithoutPagination(customerId, orderid,
				orderlineId, skuIdUserId, fromDate, toDate, msUserId);

		if (allEntities.isEmpty()) {
			return new GenericResponse<>("No records found", false, null);
		}

		// 2️⃣ Group by orderId
		Map<String, List<CustomerOrderDetailsEntity>> groupedByOrderId = allEntities.stream()
				.collect(Collectors.groupingBy(e -> e.getCustomerOrderOrderHdrEntity().getOrderId(), LinkedHashMap::new,
						Collectors.toList()));

		List<CustomerGetOrderResponseDTO> allOrders = new ArrayList<>();
		for (Map.Entry<String, List<CustomerOrderDetailsEntity>> entry : groupedByOrderId.entrySet()) {
			String orderIdKey = entry.getKey();
			List<CustomerOrderDetailsEntity> orderDetailsEntities = entry.getValue();

			CustomerOrderHdrEntity headerEntity = orderDetailsEntities.get(0).getCustomerOrderOrderHdrEntity();

			CustomerGetOrderResponseDTO dto = new CustomerGetOrderResponseDTO();
			dto.setOrderId(orderIdKey);
			dto.setDeliveryDate(headerEntity.getDeliveryDate());
			dto.setDeliveryLocation(headerEntity.getDeliveryLocation());
			dto.setPincode(headerEntity.getPincode());
			dto.setCustomerId(headerEntity.getUpdatedBy());
			dto.setUpdatedBy(headerEntity.getUpdatedBy());
			dto.setUpdatedDate(headerEntity.getUpdatedDate());
			dto.setOrderDetailsList(orderDetailsEntities.stream()
					.map(e -> modelMapper.map(e, CustomerOrderDetailsDto.class)).collect(Collectors.toList()));

			allOrders.add(dto);
		}

		int totalOrders = allOrders.size();

		// 3️⃣ Apply pagination on grouped orders
		int fromIndex = page * size;
		int toIndex = Math.min(fromIndex + size, totalOrders);
		if (fromIndex > toIndex)
			fromIndex = toIndex;

		List<CustomerGetOrderResponseDTO> pagedOrders = allOrders.subList(fromIndex, toIndex);

		// 4️⃣ Set paging info
		pagedOrders.forEach(dto -> {
			dto.setCurrentPage(page);
			dto.setPageSize(size);
			dto.setTotalElements(totalOrders);
			dto.setTotalPages((int) Math.ceil((double) totalOrders / size));
		});

		return new GenericResponse<>("Customer Order Method details retrieved successfully.", true, pagedOrders);
	}

	private List<CustomerOrderDetailsEntity> fetchFilteredOrderDetailsWithoutPagination(String customerId,
			String orderid, String orderlineId, String skuIdUserId, String fromDate, String toDate, String msUserId) {

		String loggedInUserEmail = AuthDetailsProvider.getLoggedEmail();
		Optional<CustomerRegistration> login = customerRegisterRepository
				.findByUserEmailAndUserTypeAndRegSource(loggedInUserEmail, UserType.EC, RegSource.MRMASON);
		if (login.isEmpty()) {
			throw new ResourceNotFoundException("Access denied. This API is restricted to customer users only.");
		}
		String cId = login.get().getUserid();

		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		CriteriaQuery<CustomerOrderDetailsEntity> cq = cb.createQuery(CustomerOrderDetailsEntity.class);
		Root<CustomerOrderDetailsEntity> root = cq.from(CustomerOrderDetailsEntity.class);
		Join<CustomerOrderDetailsEntity, CustomerOrderHdrEntity> hdrJoin = root.join("customerOrderOrderHdrEntity",
				JoinType.INNER);

		List<Predicate> predicates = new ArrayList<>();

		if (customerId != null && !customerId.trim().isEmpty())
			predicates.add(cb.equal(hdrJoin.get("updatedBy"), customerId));

		if (orderid != null && !orderid.trim().isEmpty())
			predicates.add(cb.equal(hdrJoin.get("orderId"), orderid));

		if (orderlineId != null && !orderlineId.trim().isEmpty())
			predicates.add(cb.equal(root.get("orderlineId"), orderlineId));

		if (skuIdUserId != null && !skuIdUserId.trim().isEmpty())
			predicates.add(cb.equal(root.get("skuIdUserId"), skuIdUserId));

		if (msUserId != null && !msUserId.trim().isEmpty())
			predicates.add(cb.equal(root.get("msUserId"), msUserId));

		if (fromDate != null && toDate != null) {
			predicates.add(cb.between(hdrJoin.get("orderDate"), java.sql.Date.valueOf(fromDate),
					java.sql.Date.valueOf(toDate)));
		} else if (fromDate != null) {
			predicates.add(cb.greaterThanOrEqualTo(hdrJoin.get("orderDate"), java.sql.Date.valueOf(fromDate)));
		} else if (toDate != null) {
			predicates.add(cb.lessThanOrEqualTo(hdrJoin.get("orderDate"), java.sql.Date.valueOf(toDate)));
		}

		predicates.add(cb.equal(root.get("updatedBy"), cId));

		cq.where(predicates.toArray(new Predicate[0]));
		cq.orderBy(cb.desc(root.get("orderlineId")));

		return entityManager.createQuery(cq).getResultList();
	}

	public String deleteOrderLineAndHeader(String orderId, String orderlineId) {
		CustomerOrderDetailsEntity detail = orderDetailsRepo.findByOrderlineIdAndOrderId(orderlineId, orderId)
				.orElseThrow(() -> new EntityNotFoundException(
						"Order line not found with orderId: " + orderId + " and orderlineId: " + orderlineId));
		orderDetailsRepo.delete(detail);
		List<CustomerOrderDetailsEntity> remaining = orderDetailsRepo.findByOrderId(orderId);
		if (remaining.isEmpty()) {
			orderHdrRepo.deleteById(orderId);
			return "Order line and order header deleted successfully.";
		}

		return "Order line deleted successfully. Header retained as it has other lines.";
	}

	public GenericResponse<List<CustomerGetOrderResponseDTO>> getOrderDetailByCustomerId(String customerId,
			Integer page, Integer size) {

		// 1️⃣ Fetch all records for given customerId
		List<CustomerOrderDetailsEntity> allEntities = orderDetailsRepo
				.findByCustomerOrderOrderHdrEntity_UpdatedBy(customerId);

		if (allEntities.isEmpty()) {
			return new GenericResponse<>("No records found for customerId: " + customerId, false, null);
		}

		// 2️⃣ Group by orderId
		Map<String, List<CustomerOrderDetailsEntity>> groupedByOrderId = allEntities.stream()
				.collect(Collectors.groupingBy(e -> e.getCustomerOrderOrderHdrEntity().getOrderId(), LinkedHashMap::new,
						Collectors.toList()));

		List<CustomerGetOrderResponseDTO> allOrders = new ArrayList<>();

		// 3️⃣ Build DTO for each order group
		for (Map.Entry<String, List<CustomerOrderDetailsEntity>> entry : groupedByOrderId.entrySet()) {
			String orderIdKey = entry.getKey();
			List<CustomerOrderDetailsEntity> orderDetailsEntities = entry.getValue();

			CustomerGetOrderResponseDTO dto = new CustomerGetOrderResponseDTO();
			dto.setOrderId(orderIdKey);
			dto.setCustomerId(orderDetailsEntities.get(0).getCustomerOrderOrderHdrEntity().getUpdatedBy());
			dto.setDeliveryDate(orderDetailsEntities.get(0).getDeliveryDate());
			dto.setDeliveryLocation(orderDetailsEntities.get(0).getDeliveryLocation());
			dto.setUpdatedBy(orderDetailsEntities.get(0).getUpdatedBy());
			dto.setUpdatedDate(orderDetailsEntities.get(0).getUpdatedDate());
			dto.setPincode(orderDetailsEntities.get(0).getPincode());
			dto.setOrderDetailsList(orderDetailsEntities.stream()
					.map(e -> modelMapper.map(e, CustomerOrderDetailsDto.class)).collect(Collectors.toList()));

			allOrders.add(dto);
		}

		// 4️⃣ Apply pagination on grouped orders
		int totalOrders = allOrders.size();
		int fromIndex = page * size;
		int toIndex = Math.min(fromIndex + size, totalOrders);
		if (fromIndex > toIndex)
			fromIndex = toIndex;

		List<CustomerGetOrderResponseDTO> pagedOrders = allOrders.subList(fromIndex, toIndex);

		// 5️⃣ Set pagination metadata
		pagedOrders.forEach(dto -> {
			dto.setCurrentPage(page);
			dto.setPageSize(size);
			dto.setTotalElements(totalOrders);
			dto.setTotalPages((int) Math.ceil((double) totalOrders / size));
		});

		return new GenericResponse<>("Customer orders retrieved successfully.", true, pagedOrders);
	}

}
