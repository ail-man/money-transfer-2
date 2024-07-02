package com.ail.home.transfer.controller;

import static com.ail.home.transfer.utils.Utils.localDateTimeNow;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.web.util.UriComponentsBuilder;

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

	@Autowired
	private CustomerRepoDsl customerRepoDsl;

	@Autowired
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

		uri = defaultUriBuilder()
			.build()
			.toUri();

		getMockMvc().perform(get(uri).accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(header().string(XHeaders.TOTAL_COUNT, "3"));

		uri = defaultUriBuilder()
			.queryParam(PARAM_ENABLED, "true")
			.build()
			.toUri();

		getMockMvc().perform(get(uri).accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(header().string(XHeaders.TOTAL_COUNT, "2"));
	}

	@Test
	void testLimitFilterAndOffsetFilterAndLinkHeader() throws Exception {
		final URI uri = defaultUriBuilder()
			.queryParam(PARAM_LIMIT, 2)
			.build()
			.toUri();

		final MockHttpServletResponse servletResponse = getMockMvc().perform(get(uri).accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(header().string(XHeaders.TOTAL_COUNT, "3"))
			.andExpect(header().exists(HttpHeaders.LINK))
			.andReturn()
			.getResponse();

		final URI nextUri = getLinkFromHeader(servletResponse);
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
		final URI uri = defaultUriBuilder()
			.queryParam(PARAM_EMAIL, "test1@com.com")
			.build()
			.toUri();

		final String responseBody = getMockMvc().perform(get(uri).accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(header().string(XHeaders.TOTAL_COUNT, "1"))
			.andExpect(header().doesNotExist(HttpHeaders.LINK))
			.andReturn()
			.getResponse()
			.getContentAsString();

		final List<CustomerDTO> customers = getMapper().readValue(responseBody, new TypeReference<>() { });
		assertThat(customers).hasSize(1);
		final CustomerDTO customer = customers.getFirst();
		assertThat(customer.getId()).isEqualTo(UUID.fromString("aa69e678-b866-471f-80c2-91a42da4bd6f"));
		assertThat(customer.getInfo().getEmail()).isEqualTo("test1@com.com");
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

		final URI uri = defaultUriBuilder()
			.build()
			.toUri();

		// CREATE

		String responseBody =
			getMockMvc().perform(post(uri).contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)
					.content(json))
				.andExpect(status().isCreated())
				.andExpect(header().exists(HttpHeaders.LOCATION))
				.andExpect(header().string(XHeaders.ENTITY_VERSION, "0"))
				.andReturn()
				.getResponse()
				.getContentAsString();

		CustomerDTO updatedCustomer = getJsonSerializationService().fromJson(responseBody, CustomerDTO.class);
		assertThat(updatedCustomer.getId()).isNotNull();
		assertThat(updatedCustomer.getVersion()).isZero();
		assertThat(updatedCustomer.getEnabled()).isFalse();
		final LocalDateTime timestamp = localDateTimeNow();
		assertThat(updatedCustomer.getCreatedAt()).isBefore(timestamp);
		assertThat(updatedCustomer.getUpdatedAt()).isEqualTo(updatedCustomer.getCreatedAt());
		CustomerInfo actualCustomerInfo = updatedCustomer.getInfo();
		assertThat(actualCustomerInfo).isNotNull();
		assertThat(actualCustomerInfo.getEmail()).isEqualTo(customerInfo.getEmail());
		assertThat(actualCustomerInfo.getPhone()).isEqualTo(customerInfo.getPhone());
		assertThat(actualCustomerInfo.getFirstName()).isEqualTo(customerInfo.getFirstName());
		assertThat(actualCustomerInfo.getLastName()).isEqualTo(customerInfo.getLastName());

		// UPDATE

		customerInfo = CustomerInfo.builder()
			.email("test-updated@ail-man.com")
			.phone("0987654321")
			.firstName("Sofia")
			.lastName("Chandler")
			.build();
		customerData = CustomerData.builder()
			.enabled(true)
			.info(customerInfo)
			.build();
		json = getJsonSerializationService().toJson(customerData);

		final String url = String.join("/", CUSTOMERS_PATH, updatedCustomer.getId().toString());

		responseBody =
			getMockMvc().perform(put(url).contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)
					.header(XHeaders.ENTITY_VERSION, 0)
					.content(json))
				.andExpect(status().isOk())
				.andExpect(header().string(XHeaders.ENTITY_VERSION, "1"))
				.andReturn()
				.getResponse()
				.getContentAsString();

		updatedCustomer = getJsonSerializationService().fromJson(responseBody, CustomerDTO.class);
		assertThat(updatedCustomer.getId()).isNotNull();
		assertThat(updatedCustomer.getVersion()).isEqualTo(1);
		assertThat(updatedCustomer.getEnabled()).isTrue();
		assertThat(updatedCustomer.getCreatedAt()).isBefore(timestamp);
		assertThat(updatedCustomer.getUpdatedAt()).isAfter(timestamp);
		actualCustomerInfo = updatedCustomer.getInfo();
		assertThat(actualCustomerInfo).isNotNull();
		assertThat(actualCustomerInfo.getEmail()).isEqualTo(customerInfo.getEmail());
		assertThat(actualCustomerInfo.getPhone()).isEqualTo(customerInfo.getPhone());
		assertThat(actualCustomerInfo.getFirstName()).isEqualTo(customerInfo.getFirstName());
		assertThat(actualCustomerInfo.getLastName()).isEqualTo(customerInfo.getLastName());
	}

	private UriComponentsBuilder defaultUriBuilder() {
		return UriComponentsBuilder.fromUriString(CUSTOMERS_PATH);
	}

	private URI getLinkFromHeader(final MockHttpServletResponse servletResponse) throws URISyntaxException {
		// validate URI: <http://localhost/customers?limit=2&offset=2>; rel=\"next\"
		final String linkHeaderValue = servletResponse.getHeader(HttpHeaders.LINK);
		assertThat(linkHeaderValue).isNotBlank();
		final String link = linkHeaderValue.replaceAll("<|>;\\srel=\"next\"", "");
		final URI nextUri = new URI(link);
		assertThat(nextUri).hasPath(CUSTOMERS_PATH);
		return nextUri;
	}
}
