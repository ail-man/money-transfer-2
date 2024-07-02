package com.ail.home.transfer.persistence;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.hibernate.annotations.JdbcType;
import org.hibernate.type.descriptor.jdbc.UUIDJdbcType;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Version;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity(name = "customers")
@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class Customer extends CustomerBase {

	@Id
	@JdbcType(UUIDJdbcType.class)
	@Column(name = "id", nullable = false)
	private UUID id;

	@Version
	private int version;

	@OneToMany(mappedBy = "customer", fetch = FetchType.LAZY)
	private List<Account> accounts = new ArrayList<>();

	@JsonIgnore
	public String getEmailDomain() {
		final String email = getInfo().getEmail();
		if (email != null) {
			return email.split("@")[1];
		}
		return null;
	}
}
