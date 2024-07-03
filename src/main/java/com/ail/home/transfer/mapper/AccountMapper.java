package com.ail.home.transfer.mapper;

import org.mapstruct.CollectionMappingStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.ReportingPolicy;

import com.ail.home.transfer.dto.AccountDTO;
import com.ail.home.transfer.dto.AccountData;
import com.ail.home.transfer.persistence.Account;

@Mapper(collectionMappingStrategy = CollectionMappingStrategy.ADDER_PREFERRED,
	nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS,
	unmappedTargetPolicy = ReportingPolicy.IGNORE,
	componentModel = "spring")
public interface AccountMapper extends BaseMapper {

	AccountDTO map(Account source);

	Account map(AccountData source);

	@Mapping(target = "currency", ignore = true)
	void map(AccountData source, @MappingTarget Account target);
}
