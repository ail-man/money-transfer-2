package com.ail.home.transfer.mapper;

import java.util.UUID;

public interface BaseMapper {

	default String map(final UUID source) {
		return source.toString();
	}
}
