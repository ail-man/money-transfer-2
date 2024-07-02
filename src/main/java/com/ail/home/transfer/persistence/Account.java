package com.ail.home.transfer.persistence;

import java.math.BigDecimal;
import java.util.UUID;

import org.hibernate.annotations.JdbcType;
import org.hibernate.type.descriptor.jdbc.UUIDJdbcType;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Version;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity(name = "accounts")
@Getter
@Setter
@EqualsAndHashCode(callSuper = true, exclude = { "customer" })
@ToString(callSuper = true, exclude = { "customer" })
public class Account extends AccountBase {

	@Id
	@JdbcType(UUIDJdbcType.class)
	@Column(name = "id", nullable = false)
	private UUID id;

	@Version
	private int version;

	@Column(name = "balance", nullable = false)
	private BigDecimal balance;

	@Column(name = "currency", nullable = false)
	private String currency;

	@JdbcType(UUIDJdbcType.class)
	@Column(name = "customer_id", nullable = false)
	private UUID customerId;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "customer_id", insertable = false, updatable = false)
	private Customer customer;
}
