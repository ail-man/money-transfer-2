package com.ail.home.transfer.utils;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import org.junit.jupiter.api.Test;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

class BeanUtilsTest {
	@Test
	void testCopyNonNullProperties() {
		final Bean1 source = new Bean1("3", 4, null);

		final Object f3b = new Object();
		final Bean2 target = new Bean2("1", "4", f3b);

		BeanUtils.copyNonNullProperties(source, target);

		assertThat(target).isEqualTo(new Bean2("3", "4", f3b));
	}

	@AllArgsConstructor
	@Getter
	@Setter
	@ToString
	@EqualsAndHashCode
	private static class Bean1 {
		private String f1;
		private Integer f2;
		private Object f3a;
	}


	@AllArgsConstructor
	@Getter
	@Setter
	@ToString
	@EqualsAndHashCode
	private static class Bean2 {
		private String f1;
		private String f2;
		private Object f3b;
	}
}
