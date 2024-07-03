package com.ail.home.transfer.utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Slf4j
public final class ObjectMerger {

	private static final ObjectMapper OBJECT_MAPPER = JsonMapper.builder()
		.addModule(new JavaTimeModule())
		.build();

	/**
	 * Merges data from obj2 to obj1.
	 * <p>
	 * IMPORTANT! Should not be used with objects which have cross-references.
	 * Otherwise, it will be StackOverflowError due to infinite recursion.
	 *
	 * @param obj1
	 * @param obj2
	 * @param clazz
	 * @param <T>
	 * @param <U>
	 * @return
	 */
	public static <T, U> T mergeObjects(T obj1, U obj2, Class<T> clazz) {
		try {
			final ObjectNode mainNode = OBJECT_MAPPER.convertValue(obj1, ObjectNode.class);
			log.debug("merging object mainNode: {}", obj1);
			final ObjectNode updateNode = OBJECT_MAPPER.convertValue(obj2, ObjectNode.class);
			log.debug("merging object updateNode: {}", obj2);
			final ObjectNode mergedNode = deepMerge(mainNode, updateNode);
			log.debug("merging object mergedNode: {}", obj2);
			return OBJECT_MAPPER.treeToValue(mergedNode, clazz);
		} catch (Exception e) {
			throw new RuntimeException("Failed to merge objects", e);
		}
	}

	public static ObjectNode deepMerge(ObjectNode mainNode, ObjectNode updateNode) {
		updateNode.fields().forEachRemaining(entry -> {
			String fieldName = entry.getKey();
			JsonNode jsonNode = entry.getValue();
			if (mainNode.has(fieldName)) {
				if (jsonNode.isObject()) {
					deepMerge((ObjectNode) mainNode.get(fieldName), (ObjectNode) jsonNode);
				} else {
					mainNode.replace(fieldName, jsonNode);
				}
			} else {
				mainNode.set(fieldName, jsonNode);
			}
		});
		return mainNode;
	}
}
