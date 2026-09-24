package com.application.mrmason.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class MaterialItemDto {

	private String skuId;
	private String brand;
	private String modelNo;
	private String modelName;
	private String materialSubCategory;
}