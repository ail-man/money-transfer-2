package com.ail.home.transfer.mapper;

import org.mapstruct.CollectionMappingStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.ReportingPolicy;

import com.ail.home.transfer.dto.CustomerDTO;
import com.ail.home.transfer.dto.CustomerData;
import com.ail.home.transfer.persistence.Customer;

@Mapper(collectionMappingStrategy = CollectionMappingStrategy.ADDER_PREFERRED,
	nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS,
	unmappedTargetPolicy = ReportingPolicy.IGNORE,
	componentModel = "spring")
public interface CustomerMapper extends BaseMapper {

	CustomerDTO map(Customer source);

	@Mapping(target = "enabled", source = "source.enabled")
	@Mapping(target = "info.email", source = "source.info.email")
	@Mapping(target = "info.phone", source = "source.info.phone")
	@Mapping(target = "info.firstName", source = "source.info.firstName")
	@Mapping(target = "info.lastName", source = "source.info.lastName")
	Customer map(CustomerData source);
}
