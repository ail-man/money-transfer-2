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
import java.math.BigInteger;
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
import com.ail.home.transfer.dto.AccountDTO;
import com.ail.home.transfer.dto.AccountData;
import com.ail.home.transfer.persistence.Account;
import com.ail.home.transfer.persistence.AccountInfo;
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
	private static final String PARAM_CURRENCY = "currency";
	private static final String PARAM_CUSTOMER_ID = "customerId";
	private static final String PARAM_LIMIT = "limit";
	private static final String PARAM_OFFSET = "offset";

	@SpyBean
	private CustomerRepoDsl customerRepoDsl;

	@SpyBean
	private CustomerHistoryRepoDsl customerHistoryRepoDsl;

	@SpyBean
	private AccountRepoDsl accountRepoDsl;

	@SpyBean
	private AccountHistoryRepoDsl accountHistoryRepoDsl;

	@BeforeAll
	public void init() throws Exception {
		// records are initialized in such way that we can simplify testing by comparing the amount of returning records
		final InputStream customersSrc = CustomerControllerTest.class.getResourceAsStream("/database/test_customers.json");
		final List<Customer> customers = getMapper().readValue(customersSrc, new TypeReference<>() { });
		customerRepoDsl.getRepo().saveAllAndFlush(customers);
		final InputStream accountsSrc = AccountControllerTest.class.getResourceAsStream("/database/test_accounts.json");
		final List<Account> accounts = getMapper().readValue(accountsSrc, new TypeReference<>() { });
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

		uri = defaultUriBuilder(ACCOUNTS_PATH)
			.build()
			.toUri();

		getMockMvc().perform(get(uri).accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(header().string(XHeaders.TOTAL_COUNT, "3"));

		uri = defaultUriBuilder(ACCOUNTS_PATH)
			.queryParam(PARAM_ENABLED, "true")
			.build()
			.toUri();

		getMockMvc().perform(get(uri).accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(header().string(XHeaders.TOTAL_COUNT, "2"));
	}

	@Test
	void testLimitFilterAndOffsetFilterAndLinkHeader() throws Exception {
		final URI uri = defaultUriBuilder(ACCOUNTS_PATH)
			.queryParam(PARAM_LIMIT, 2)
			.build()
			.toUri();

		final MockHttpServletResponse servletResponse = getMockMvc().perform(get(uri).accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(header().string(XHeaders.TOTAL_COUNT, "3"))
			.andExpect(header().exists(HttpHeaders.LINK))
			.andReturn()
			.getResponse();

		final URI nextUri = getLinkFromHeader(servletResponse, ACCOUNTS_PATH);
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
		final URI uri = defaultUriBuilder(ACCOUNTS_PATH)
			.queryParam(PARAM_IBAN, "US02120300000000202051")
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
		assertThat(account.getInfo().getIban()).isEqualTo("US02120300000000202051");
	}

	@Test
	void testCurrencyFilter() throws Exception {
		URI uri = defaultUriBuilder(ACCOUNTS_PATH)
			.queryParam(PARAM_CURRENCY, "USD", "EUR")
			.build()
			.toUri();

		String responseBody = getMockMvc().perform(get(uri).accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(header().string(XHeaders.TOTAL_COUNT, "3"))
			.andExpect(header().doesNotExist(HttpHeaders.LINK))
			.andReturn()
			.getResponse()
			.getContentAsString();

		List<AccountDTO> accounts = getMapper().readValue(responseBody, new TypeReference<>() { });
		assertThat(accounts).hasSize(3);

		uri = defaultUriBuilder(ACCOUNTS_PATH)
			.queryParam(PARAM_CURRENCY, "EUR", "CNY")
			.build()
			.toUri();

		responseBody = getMockMvc().perform(get(uri).accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(header().string(XHeaders.TOTAL_COUNT, "2"))
			.andExpect(header().doesNotExist(HttpHeaders.LINK))
			.andReturn()
			.getResponse()
			.getContentAsString();

		accounts = getMapper().readValue(responseBody, new TypeReference<>() { });
		assertThat(accounts).hasSize(2);

		uri = defaultUriBuilder(ACCOUNTS_PATH)
			.queryParam(PARAM_CURRENCY, "USD", "CNY")
			.build()
			.toUri();

		responseBody = getMockMvc().perform(get(uri).accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(header().string(XHeaders.TOTAL_COUNT, "1"))
			.andExpect(header().doesNotExist(HttpHeaders.LINK))
			.andReturn()
			.getResponse()
			.getContentAsString();

		accounts = getMapper().readValue(responseBody, new TypeReference<>() { });
		assertThat(accounts).hasSize(1);
	}

	@Test
	void testCustomerIdFilter() throws Exception {
		final URI uri = defaultUriBuilder(ACCOUNTS_PATH)
			.queryParam(PARAM_CUSTOMER_ID, "aa69e678-b866-471f-80c2-91a42da4bd6f")
			.build()
			.toUri();

		final String responseBody = getMockMvc().perform(get(uri).accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(header().string(XHeaders.TOTAL_COUNT, "2"))
			.andExpect(header().doesNotExist(HttpHeaders.LINK))
			.andReturn()
			.getResponse()
			.getContentAsString();

		List<AccountDTO> accounts = getMapper().readValue(responseBody, new TypeReference<>() { });
		assertThat(accounts).hasSize(2);
		assertThat(accounts.get(0).getCustomerId()).isEqualTo(UUID.fromString("aa69e678-b866-471f-80c2-91a42da4bd6f"));
		assertThat(accounts.get(1).getCustomerId()).isEqualTo(UUID.fromString("aa69e678-b866-471f-80c2-91a42da4bd6f"));
	}

	@Test
	void testCreateThenUpdateAccount() throws Exception {
		AccountInfo accountInfo = AccountInfo.builder()
			.iban("AT483200000012345864")
			.build();
		final LocalDateTime expiresAt = localDateTimeNow().plusYears(5);
		AccountData accountData = AccountData.builder()
			.customerId(UUID.fromString("f7309de5-5312-4bee-9160-903cc51085bb"))
			.enabled(true)
			.currency("EUR")
			.info(accountInfo)
			.expiresAt(expiresAt)
			.build();
		String json = getJsonSerializationService().toJson(accountData);

		final URI uri = defaultUriBuilder(ACCOUNTS_PATH)
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

		AccountDTO account = getJsonSerializationService().fromJson(responseBody, AccountDTO.class);
		assertThat(account.getId()).isNotNull();
		assertThat(account.getVersion()).isZero();
		assertThat(account.getCustomerId()).isEqualTo(UUID.fromString("f7309de5-5312-4bee-9160-903cc51085bb"));
		assertThat(account.getEnabled()).isTrue();
		assertThat(account.getBalance()).isEqualTo(new BigInteger("0"));
		assertThat(account.getCurrency()).isEqualTo("EUR");
		final LocalDateTime timestamp = localDateTimeNow();
		assertThat(account.getCreatedAt()).isBefore(timestamp);
		assertThat(account.getUpdatedAt()).isEqualTo(account.getCreatedAt());
		assertThat(account.getExpiresAt()).isEqualTo(expiresAt);
		AccountInfo actualAccountInfo = account.getInfo();
		assertThat(actualAccountInfo).isNotNull();
		assertThat(actualAccountInfo.getIban()).isEqualTo("AT483200000012345864");
		assertThat(actualAccountInfo.getCardNumber()).isNull();

		// UPDATE

		accountInfo = AccountInfo.builder()
			.iban(null)
			.cardNumber("5555555555554444")
			.build();
		final LocalDateTime expiresAt2 = expiresAt.plusYears(3);
		accountData = AccountData.builder()
			.id(account.getId())
			.version(0)
			.enabled(false)
			.expiresAt(expiresAt2)
			.customerId(UUID.fromString("9d537a44-82df-4d0e-bdfb-7d36b9772f6b"))
			.info(accountInfo)
			.build();
		json = getJsonSerializationService().toJson(accountData);

		responseBody =
			getMockMvc().perform(put(uri).contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON).content(json))
				.andExpect(status().isOk())
				.andReturn()
				.getResponse()
				.getContentAsString();

		account = getJsonSerializationService().fromJson(responseBody, AccountDTO.class);
		assertThat(account.getId()).isNotNull();
		assertThat(account.getVersion()).isEqualTo(1);
		assertThat(account.getCustomerId()).isEqualTo(UUID.fromString("9d537a44-82df-4d0e-bdfb-7d36b9772f6b"));
		assertThat(account.getEnabled()).isFalse();
		assertThat(account.getBalance()).isEqualTo(new BigInteger("0"));
		assertThat(account.getCurrency()).isEqualTo("EUR");
		assertThat(account.getCreatedAt()).isBefore(timestamp);
		assertThat(account.getUpdatedAt()).isAfter(timestamp);
		assertThat(account.getExpiresAt()).isEqualTo(expiresAt2);
		actualAccountInfo = account.getInfo();
		assertThat(actualAccountInfo).isNotNull();
		assertThat(actualAccountInfo.getIban()).isNull();
		assertThat(actualAccountInfo.getCardNumber()).isEqualTo("5555555555554444");

		responseBody =
			getMockMvc().perform(put(uri).contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON).content(json))
				.andExpect(status().isConflict())
				.andReturn()
				.getResponse()
				.getContentAsString();

		assertThat(responseBody).contains("Version 0 does not match the current entity version 1");
	}

}
