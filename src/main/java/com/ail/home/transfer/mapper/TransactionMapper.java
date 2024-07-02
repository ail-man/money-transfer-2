package com.ail.home.transfer.mapper;

import org.mapstruct.CollectionMappingStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.ReportingPolicy;

import com.ail.home.transfer.dto.TransactionDTO;
import com.ail.home.transfer.dto.TransactionData;
import com.ail.home.transfer.persistence.Transaction;

@Mapper(collectionMappingStrategy = CollectionMappingStrategy.ADDER_PREFERRED,
	nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS,
	unmappedTargetPolicy = ReportingPolicy.IGNORE,
	componentModel = "spring")
public interface TransactionMapper extends BaseMapper {

	TransactionDTO map(Transaction source);

	Transaction map(TransactionData source);
}
