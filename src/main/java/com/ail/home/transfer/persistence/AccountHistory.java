package com.ail.home.transfer.persistence;

import com.fasterxml.jackson.annotation.JsonUnwrapped;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "accounts_history")
@Setter
@Getter
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class AccountHistory extends AccountBase {

	@EmbeddedId
	@JsonUnwrapped
	private CompositeHistoryId compositeHistoryId;
}
