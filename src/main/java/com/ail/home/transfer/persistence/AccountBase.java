package com.ail.home.transfer.persistence;

import java.time.LocalDateTime;

import org.hibernate.annotations.Type;

import io.hypersistence.utils.hibernate.type.json.JsonType;
import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@MappedSuperclass
@Setter
@Getter
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public abstract class AccountBase extends EntityBase {

	@Column(name = "enabled")
	private Boolean enabled = false;

	@Column(name = "expires_at")
	private LocalDateTime expiresAt;

	@Type(JsonType.class)
	@Column(name = "info")
	private AccountInfo info;
}
