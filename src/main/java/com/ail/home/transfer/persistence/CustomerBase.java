package com.ail.home.transfer.persistence;

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
public abstract class CustomerBase extends EntityBase {

	@Column(name = "enabled", nullable = false)
	private boolean enabled;

	@Type(JsonType.class)
	@Column(name = "info")
	private CustomerInfo info;
}
