package com.ail.home.transfer.persistence;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import lombok.Data;

@MappedSuperclass
@Data
public abstract class EntityBase {

	@Column(name = "created_at", nullable = false)
	private LocalDateTime createdAt;
}
