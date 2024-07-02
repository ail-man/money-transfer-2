package com.ail.home.transfer.persistence;

import java.util.UUID;

import org.hibernate.annotations.JdbcType;
import org.hibernate.type.descriptor.jdbc.UUIDJdbcType;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Version;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity(name = "accounts")
@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class Account extends AccountBase {

	@Id
	@JdbcType(UUIDJdbcType.class)
	@Column(name = "id", nullable = false)
	private UUID id;

	@Version
	private Integer version;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	private Customer customer;
}
