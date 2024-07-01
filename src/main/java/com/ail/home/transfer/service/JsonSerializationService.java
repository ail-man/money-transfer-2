package com.ail.home.transfer.service;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ail.home.transfer.exceptions.JsonSerializationException;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class JsonSerializationService {

	private final ObjectMapper objectMapper;

	public String toJson(final Object obj) {
		if (obj == null) {
			return null;
		}
		try {
			return objectMapper.writeValueAsString(obj);
		} catch (final JsonProcessingException e) {
			throw new JsonSerializationException("Unable to serialize " + obj.getClass().getName() + " to JSON: " + e.getMessage(), e);
		}
	}

	public <T> T fromJson(final String jsonString, final Class<T> obj) {
		try {
			return objectMapper.readValue(jsonString, obj);
		} catch (final JsonProcessingException e) {
			throw new JsonSerializationException("Unable to deserialize" + obj.getName() + " from JSON: " + e.getMessage(), e);
		}
	}
}
