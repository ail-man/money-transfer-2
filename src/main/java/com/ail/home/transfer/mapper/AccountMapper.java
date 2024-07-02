package com.ail.home.transfer.mapper;

import org.mapstruct.CollectionMappingStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.ReportingPolicy;

import com.ail.home.transfer.persistence.Account;

@Mapper(collectionMappingStrategy = CollectionMappingStrategy.ADDER_PREFERRED,
	nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS,
	unmappedTargetPolicy = ReportingPolicy.IGNORE,
	componentModel = "spring")
public interface AccountMapper extends BaseMapper {

	com.ail.home.transfer.dto.AccountDTO map(Account source);
}
