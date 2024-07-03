package com.ail.home.transfer.controller;

import static com.ail.home.transfer.controller.ControllerTestUtils.defaultUriBuilder;
import static com.ail.home.transfer.controller.ControllerTestUtils.getLinkFromHeader;
import static com.ail.home.transfer.utils.Utils.localDateTimeNow;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.InputStream;
import java.net.URI;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;

import com.ail.home.transfer.SpringTestContextInitialization;
import com.ail.home.transfer.dto.CustomerDTO;
import com.ail.home.transfer.dto.CustomerData;
import com.ail.home.transfer.persistence.Customer;
import com.ail.home.transfer.persistence.CustomerInfo;
import com.ail.home.transfer.repository.CustomerHistoryRepoDsl;
import com.ail.home.transfer.repository.CustomerRepoDsl;
import com.fasterxml.jackson.core.type.TypeReference;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class CustomerControllerTest extends SpringTestContextInitialization {

	private static final String CUSTOMERS_PATH = "/api/v1/customers";

	private static final String PARAM_ENABLED = "enabled";
	private static final String PARAM_EMAIL = "email";
	private static final String PARAM_LIMIT = "limit";
	private static final String PARAM_OFFSET = "offset";

	@SpyBean
	private CustomerRepoDsl customerRepoDsl;

	@SpyBean
	private CustomerHistoryRepoDsl customerHistoryRepoDsl;

	@BeforeAll
	public void init() throws Exception {
		// records are initialized in such way that we can simplify testing by comparing the amount of returning records
		final InputStream src = CustomerControllerTest.class.getResourceAsStream("/database/test_customers.json");
		final List<Customer> customers = getMapper().readValue(src, new TypeReference<>() { });
		customerRepoDsl.getRepo().saveAllAndFlush(customers);
	}

	@AfterAll
	public void cleanUp() {
		customerHistoryRepoDsl.getRepo().deleteAll();
		customerRepoDsl.getRepo().deleteAll();
	}

	@Test
	void testHeaderXTotalCount() throws Exception {
		URI uri;

		uri = defaultUriBuilder(CUSTOMERS_PATH)
			.build()
			.toUri();

		getMockMvc().perform(get(uri).accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(header().string(XHeaders.TOTAL_COUNT, "3"));

		uri = defaultUriBuilder(CUSTOMERS_PATH)
			.queryParam(PARAM_ENABLED, "true")
			.build()
			.toUri();

		getMockMvc().perform(get(uri).accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(header().string(XHeaders.TOTAL_COUNT, "2"));
	}

	@Test
	void testLimitFilterAndOffsetFilterAndLinkHeader() throws Exception {
		final URI uri = defaultUriBuilder(CUSTOMERS_PATH)
			.queryParam(PARAM_LIMIT, 2)
			.build()
			.toUri();

		final MockHttpServletResponse servletResponse = getMockMvc().perform(get(uri).accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(header().string(XHeaders.TOTAL_COUNT, "3"))
			.andExpect(header().exists(HttpHeaders.LINK))
			.andReturn()
			.getResponse();

		final URI nextUri = getLinkFromHeader(servletResponse, CUSTOMERS_PATH);
		assertThat(nextUri)
			.hasParameter(PARAM_LIMIT, "2")
			.hasParameter(PARAM_OFFSET, "2");

		getMockMvc().perform(get(nextUri).accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(header().string(XHeaders.TOTAL_COUNT, "3"))
			.andExpect(header().doesNotExist(HttpHeaders.LINK));
	}

	@Test
	void testEmailFilter() throws Exception {
		URI uri = defaultUriBuilder(CUSTOMERS_PATH)
			.queryParam(PARAM_EMAIL, "test1@com.com")
			.build()
			.toUri();

		String responseBody = getMockMvc().perform(get(uri).accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(header().string(XHeaders.TOTAL_COUNT, "1"))
			.andExpect(header().doesNotExist(HttpHeaders.LINK))
			.andReturn()
			.getResponse()
			.getContentAsString();

		List<CustomerDTO> customers = getMapper().readValue(responseBody, new TypeReference<>() { });
		assertThat(customers).hasSize(1);
		final CustomerDTO customer = customers.getFirst();
		assertThat(customer.getId()).isEqualTo(UUID.fromString("aa69e678-b866-471f-80c2-91a42da4bd6f"));
		assertThat(customer.getInfo().getEmail()).isEqualTo("test1@com.com");

		uri = defaultUriBuilder(CUSTOMERS_PATH)
			.queryParam(PARAM_EMAIL, "test1@com.com", "test2@de.de")
			.build()
			.toUri();

		responseBody = getMockMvc().perform(get(uri).accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(header().string(XHeaders.TOTAL_COUNT, "2"))
			.andExpect(header().doesNotExist(HttpHeaders.LINK))
			.andReturn()
			.getResponse()
			.getContentAsString();

		customers = getMapper().readValue(responseBody, new TypeReference<>() { });
		assertThat(customers).hasSize(2);

		uri = defaultUriBuilder(CUSTOMERS_PATH)
			.queryParam(PARAM_EMAIL, "test1@com.com", "test2@de.de", "test3@org.org")
			.build()
			.toUri();

		responseBody = getMockMvc().perform(get(uri).accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(header().string(XHeaders.TOTAL_COUNT, "3"))
			.andExpect(header().doesNotExist(HttpHeaders.LINK))
			.andReturn()
			.getResponse()
			.getContentAsString();

		customers = getMapper().readValue(responseBody, new TypeReference<>() { });
		assertThat(customers).hasSize(3);
	}

	@Test
	void testCreateThenUpdateCustomer() throws Exception {
		CustomerInfo customerInfo = CustomerInfo.builder()
			.email("test@ail-man.de")
			.phone("0123456789")
			.firstName("Daniel")
			.lastName("Smith")
			.build();
		CustomerData customerData = CustomerData.builder()
			.enabled(false)
			.info(customerInfo)
			.build();
		String json = getJsonSerializationService().toJson(customerData);

		final URI uri = defaultUriBuilder(CUSTOMERS_PATH)
			.build()
			.toUri();

		// CREATE

		String responseBody =
			getMockMvc().perform(post(uri).contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON).content(json))
				.andExpect(status().isCreated())
				.andExpect(header().exists(HttpHeaders.LOCATION))
				.andReturn()
				.getResponse()
				.getContentAsString();

		CustomerDTO customer = getJsonSerializationService().fromJson(responseBody, CustomerDTO.class);
		assertThat(customer.getId()).isNotNull();
		assertThat(customer.getVersion()).isZero();
		assertThat(customer.getEnabled()).isFalse();
		final LocalDateTime timestamp = localDateTimeNow();
		assertThat(customer.getCreatedAt()).isBefore(timestamp);
		assertThat(customer.getUpdatedAt()).isEqualTo(customer.getCreatedAt());
		CustomerInfo actualCustomerInfo = customer.getInfo();
		assertThat(actualCustomerInfo).isNotNull();
		assertThat(actualCustomerInfo.getEmail()).isEqualTo(customerInfo.getEmail());
		assertThat(actualCustomerInfo.getPhone()).isEqualTo(customerInfo.getPhone());
		assertThat(actualCustomerInfo.getFirstName()).isEqualTo(customerInfo.getFirstName());
		assertThat(actualCustomerInfo.getLastName()).isEqualTo(customerInfo.getLastName());

		// UPDATE

		customerInfo = CustomerInfo.builder()
			.email("test-updated@ail-man.com")
			.phone(null)
			.firstName("Sofia")
			.lastName("Chandler")
			.build();
		customerData = CustomerData.builder()
			.id(customer.getId())
			.version(0)
			.enabled(true)
			.info(customerInfo)
			.build();
		json = getJsonSerializationService().toJson(customerData);

		responseBody =
			getMockMvc().perform(put(uri).contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON).content(json))
				.andExpect(status().isOk())
				.andReturn()
				.getResponse()
				.getContentAsString();

		customer = getJsonSerializationService().fromJson(responseBody, CustomerDTO.class);
		assertThat(customer.getId()).isNotNull();
		assertThat(customer.getVersion()).isEqualTo(1);
		assertThat(customer.getEnabled()).isTrue();
		assertThat(customer.getCreatedAt()).isBefore(timestamp);
		assertThat(customer.getUpdatedAt()).isAfter(timestamp);
		actualCustomerInfo = customer.getInfo();
		assertThat(actualCustomerInfo).isNotNull();
		assertThat(actualCustomerInfo.getEmail()).isEqualTo(customerInfo.getEmail());
		assertThat(actualCustomerInfo.getPhone()).isNull();
		assertThat(actualCustomerInfo.getFirstName()).isEqualTo(customerInfo.getFirstName());
		assertThat(actualCustomerInfo.getLastName()).isEqualTo(customerInfo.getLastName());

		responseBody =
			getMockMvc().perform(put(uri).contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON).content(json))
				.andExpect(status().isConflict())
				.andReturn()
				.getResponse()
				.getContentAsString();

		assertThat(responseBody).contains("Version 0 does not match the current entity version 1");
	}

}
