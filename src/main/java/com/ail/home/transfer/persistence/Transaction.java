package com.ail.home.transfer.persistence;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import org.hibernate.annotations.JdbcType;
import org.hibernate.annotations.Type;
import org.hibernate.type.descriptor.jdbc.UUIDJdbcType;

import io.hypersistence.utils.hibernate.type.json.JsonType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity(name = "transactions")
@Getter
@Setter
@EqualsAndHashCode(exclude = { "fromAccount", "toAccount" })
@ToString(exclude = { "fromAccount", "toAccount" })
public class Transaction {

	@Id
	@JdbcType(UUIDJdbcType.class)
	@Column(name = "id", nullable = false)
	private UUID id;

	@Column(name = "timestamp", nullable = false)
	private LocalDateTime timestamp;

	@Column(name = "amount", nullable = false)
	private BigDecimal amount;

	@Column(name = "currency", nullable = false)
	private String currency;

	@JdbcType(UUIDJdbcType.class)
	@Column(name = "from_account_id", nullable = false)
	private UUID fromAccountId;

	@JdbcType(UUIDJdbcType.class)
	@Column(name = "to_account_id", nullable = false)
	private UUID toAccountId;

	@Type(JsonType.class)
	@Column(name = "info")
	private TransactionInfo info;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "from_account_id", insertable = false, updatable = false)
	private Account fromAccount;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "to_account_id", insertable = false, updatable = false)
	private Account toAccount;

}
