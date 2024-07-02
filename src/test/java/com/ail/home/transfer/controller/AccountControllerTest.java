package com.ail.home.transfer.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
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
import com.ail.home.transfer.dto.AccountDTO;
import com.ail.home.transfer.persistence.Account;
import com.ail.home.transfer.persistence.Customer;
import com.ail.home.transfer.repository.AccountHistoryRepoDsl;
import com.ail.home.transfer.repository.AccountRepoDsl;
import com.ail.home.transfer.repository.CustomerHistoryRepoDsl;
import com.ail.home.transfer.repository.CustomerRepoDsl;
import com.fasterxml.jackson.core.type.TypeReference;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class AccountControllerTest extends SpringTestContextInitialization {

	private static final String ACCOUNTS_PATH = "/api/v1/accounts";

	private static final String PARAM_ENABLED = "enabled";
	private static final String PARAM_IBAN = "iban";
	private static final String PARAM_LIMIT = "limit";
	private static final String PARAM_OFFSET = "offset";

	@Autowired
	private CustomerRepoDsl customerRepoDsl;

	@Autowired
	private CustomerHistoryRepoDsl customerHistoryRepoDsl;

	@Autowired
	private AccountRepoDsl accountRepoDsl;

	@Autowired
	private AccountHistoryRepoDsl accountHistoryRepoDsl;

	@BeforeAll
	public void init() throws Exception {
		// records are initialized in such way that we can simplify testing by comparing the amount of returning records
		final InputStream customersSrc = CustomerControllerTest.class.getResourceAsStream("/database/test_customers.json");
		final List<Customer> customers = getMapper().readValue(customersSrc, new TypeReference<>() { });
		customerRepoDsl.getRepo().saveAllAndFlush(customers);
		final InputStream accountsSrc = AccountControllerTest.class.getResourceAsStream("/database/test_accounts.json");
		final List<Account> accounts = getMapper().readValue(accountsSrc, new TypeReference<>() { });
		accounts.get(0).setCustomer(customers.get(0));
		accounts.get(1).setCustomer(customers.get(0));
		accounts.get(2).setCustomer(customers.get(1));
		accountRepoDsl.getRepo().saveAllAndFlush(accounts);
	}

	@AfterAll
	public void cleanUp() {
		accountHistoryRepoDsl.getRepo().deleteAll();
		accountRepoDsl.getRepo().deleteAll();
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
			.queryParam(PARAM_IBAN, "DE02120300000000202051")
			.build()
			.toUri();

		final String responseBody = getMockMvc().perform(get(uri).accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(header().string(XHeaders.TOTAL_COUNT, "1"))
			.andExpect(header().doesNotExist(HttpHeaders.LINK))
			.andReturn()
			.getResponse()
			.getContentAsString();

		final List<AccountDTO> accounts = getMapper().readValue(responseBody, new TypeReference<>() { });
		assertThat(accounts).hasSize(1);
		final AccountDTO account = accounts.getFirst();
		assertThat(account.getId()).isEqualTo(UUID.fromString("2492ed49-d04f-43a0-a40e-276ae82191ff"));
		assertThat(account.getInfo().getIban()).isEqualTo("DE02120300000000202051");
	}

	private UriComponentsBuilder defaultUriBuilder() {
		return UriComponentsBuilder.fromUriString(ACCOUNTS_PATH);
	}

	private URI getLinkFromHeader(final MockHttpServletResponse servletResponse) throws URISyntaxException {
		// validate URI: <http://localhost/accounts?limit=2&offset=2>; rel=\"next\"
		final String linkHeaderValue = servletResponse.getHeader(HttpHeaders.LINK);
		assertThat(linkHeaderValue).isNotBlank();
		final String link = linkHeaderValue.replaceAll("<|>;\\srel=\"next\"", "");
		final URI nextUri = new URI(link);
		assertThat(nextUri).hasPath(ACCOUNTS_PATH);
		return nextUri;
	}
}
