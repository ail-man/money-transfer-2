package com.ail.home.transfer.utils;

import java.beans.FeatureDescriptor;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Slf4j
public final class BeanUtils {

	/**
	 * Copies non-null properties from source object to target object.
	 *
	 * @param source the source object to copy non-null properties from.
	 * @param target the source object to copy non-null properties to.
	 */
	public static void copyNonNullProperties(final Object source, final Object target) {
		final BeanWrapper src = new BeanWrapperImpl(source);
		final Set<String> nullProperties = Arrays.stream(src.getPropertyDescriptors())
			.map(FeatureDescriptor::getName)
			.filter(name -> src.getPropertyValue(name) == null)
			.collect(Collectors.toSet());
		final String[] nullPropertiesArray = nullProperties.toArray(value -> new String[nullProperties.size()]);
		org.springframework.beans.BeanUtils.copyProperties(source, target, nullPropertiesArray);
	}

	/**
	 * Creates and returns a cloned instance of an object with all its attributes.
	 *
	 * @param original the source to be cloned.
	 * @return the cloned object.
	 */
	@SuppressWarnings("unchecked")
	public static <T> T clone(final T original) {
		if (original != null) {
			try {
				final Constructor<T> constructor = (Constructor<T>) original.getClass().getConstructor();
				final T clone = constructor.newInstance();
				org.springframework.beans.BeanUtils.copyProperties(original, clone);
				return clone;
			} catch (final InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
				log.error("Error cloning instance of {}", original.getClass().getSimpleName(), e);
			}
		}
		return null;
	}

	/**
	 * Performs a deep copy of the object.
	 *
	 * @param obj object to copy.
	 * @param mapper object mapper instance.
	 * @param type type to which an object must be copied.
	 * @return deep copy of the object.
	 */
	public static <T> T deepCopy(final Object obj, final ObjectMapper mapper, final Class<T> type) {
		try {
			final String json = mapper.writer().writeValueAsString(obj);
			return mapper.reader().readValue(json, type);
		} catch (final IOException e) {
			throw new UncheckedIOException(e);
		}
	}
}
