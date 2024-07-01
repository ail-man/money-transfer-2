package com.ail.home.transfer.persistence;

import java.util.UUID;

import org.hibernate.annotations.JdbcType;
import org.hibernate.type.descriptor.jdbc.UUIDJdbcType;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Data;

@Embeddable
@Data
public class CompositeHistoryId {

	@JdbcType(UUIDJdbcType.class)
	@Column(name = "id", nullable = false)
	private UUID id;

	@Column(name = "version", nullable = false)
	private Integer version;
}
