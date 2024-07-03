package com.ail.home.transfer.controller;

import static com.ail.home.transfer.controller.ControllerTestUtils.defaultUriBuilder;
import static com.ail.home.transfer.utils.Utils.localDateTimeNow;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.InputStream;
import java.math.BigDecimal;
import java.net.URI;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import com.ail.home.transfer.SpringTestContextInitialization;
import com.ail.home.transfer.dto.TransactionDTO;
import com.ail.home.transfer.dto.TransactionData;
import com.ail.home.transfer.persistence.Account;
import com.ail.home.transfer.persistence.Customer;
import com.ail.home.transfer.persistence.TransactionInfo;
import com.ail.home.transfer.repository.AccountHistoryRepoDsl;
import com.ail.home.transfer.repository.AccountRepoDsl;
import com.ail.home.transfer.repository.CustomerHistoryRepoDsl;
import com.ail.home.transfer.repository.CustomerRepoDsl;
import com.ail.home.transfer.repository.TransactionRepoDsl;
import com.fasterxml.jackson.core.type.TypeReference;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class TransactionControllerTest extends SpringTestContextInitialization {

	private static final String TRANSACTIONS_PATH = "/api/v1/transactions";

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

	@SpyBean
	private TransactionRepoDsl transactionRepoDsl;

	@BeforeAll
	public void init() throws Exception {
		// records are initialized in such way that we can simplify testing by comparing the amount of returning records
		final InputStream customersSrc = CustomerControllerTest.class.getResourceAsStream("/database/test_customers.json");
		final List<Customer> customers = getMapper().readValue(customersSrc, new TypeReference<>() { });
		customerRepoDsl.getRepo().saveAllAndFlush(customers);
		final InputStream accountsSrc = TransactionControllerTest.class.getResourceAsStream("/database/test_accounts.json");
		final List<Account> accounts = getMapper().readValue(accountsSrc, new TypeReference<>() { });
		accountRepoDsl.getRepo().saveAllAndFlush(accounts);
	}

	@AfterAll
	public void cleanUp() {
		transactionRepoDsl.getRepo().deleteAll();
		accountHistoryRepoDsl.getRepo().deleteAll();
		accountRepoDsl.getRepo().deleteAll();
		customerHistoryRepoDsl.getRepo().deleteAll();
		customerRepoDsl.getRepo().deleteAll();
	}
	//
	//	@Test
	//	void testHeaderXTotalCount() throws Exception {
	//		URI uri;
	//
	//		uri = defaultUriBuilder(TRANSACTIONS_PATH)
	//			.build()
	//			.toUri();
	//
	//		getMockMvc().perform(get(uri).accept(MediaType.APPLICATION_JSON))
	//			.andExpect(status().isOk())
	//			.andExpect(header().string(XHeaders.TOTAL_COUNT, "3"));
	//
	//		uri = defaultUriBuilder(TRANSACTIONS_PATH)
	//			.queryParam(PARAM_ENABLED, "true")
	//			.build()
	//			.toUri();
	//
	//		getMockMvc().perform(get(uri).accept(MediaType.APPLICATION_JSON))
	//			.andExpect(status().isOk())
	//			.andExpect(header().string(XHeaders.TOTAL_COUNT, "2"));
	//	}
	//
	//	@Test
	//	void testLimitFilterAndOffsetFilterAndLinkHeader() throws Exception {
	//		final URI uri = defaultUriBuilder(TRANSACTIONS_PATH)
	//			.queryParam(PARAM_LIMIT, 2)
	//			.build()
	//			.toUri();
	//
	//		final MockHttpServletResponse servletResponse = getMockMvc().perform(get(uri).accept(MediaType.APPLICATION_JSON))
	//			.andExpect(status().isOk())
	//			.andExpect(header().string(XHeaders.TOTAL_COUNT, "3"))
	//			.andExpect(header().exists(HttpHeaders.LINK))
	//			.andReturn()
	//			.getResponse();
	//
	//		final URI nextUri = getLinkFromHeader(servletResponse, TRANSACTIONS_PATH);
	//		assertThat(nextUri)
	//			.hasParameter(PARAM_LIMIT, "2")
	//			.hasParameter(PARAM_OFFSET, "2");
	//
	//		getMockMvc().perform(get(nextUri).accept(MediaType.APPLICATION_JSON))
	//			.andExpect(status().isOk())
	//			.andExpect(header().string(XHeaders.TOTAL_COUNT, "3"))
	//			.andExpect(header().doesNotExist(HttpHeaders.LINK));
	//	}
	//
	//	@Test
	//	void testEmailFilter() throws Exception {
	//		final URI uri = defaultUriBuilder(TRANSACTIONS_PATH)
	//			.queryParam(PARAM_IBAN, "US02120300000000202051")
	//			.build()
	//			.toUri();
	//
	//		final String responseBody = getMockMvc().perform(get(uri).accept(MediaType.APPLICATION_JSON))
	//			.andExpect(status().isOk())
	//			.andExpect(header().string(XHeaders.TOTAL_COUNT, "1"))
	//			.andExpect(header().doesNotExist(HttpHeaders.LINK))
	//			.andReturn()
	//			.getResponse()
	//			.getContentAsString();
	//
	//		final List<AccountDTO> accounts = getMapper().readValue(responseBody, new TypeReference<>() { });
	//		assertThat(accounts).hasSize(1);
	//		final AccountDTO account = accounts.getFirst();
	//		assertThat(account.getId()).isEqualTo(UUID.fromString("2492ed49-d04f-43a0-a40e-276ae82191ff"));
	//		assertThat(account.getInfo().getIban()).isEqualTo("US02120300000000202051");
	//	}
	//
	//	@Test
	//	void testCurrencyFilter() throws Exception {
	//		URI uri = defaultUriBuilder(TRANSACTIONS_PATH)
	//			.queryParam(PARAM_CURRENCY, "USD", "EUR")
	//			.build()
	//			.toUri();
	//
	//		String responseBody = getMockMvc().perform(get(uri).accept(MediaType.APPLICATION_JSON))
	//			.andExpect(status().isOk())
	//			.andExpect(header().string(XHeaders.TOTAL_COUNT, "3"))
	//			.andExpect(header().doesNotExist(HttpHeaders.LINK))
	//			.andReturn()
	//			.getResponse()
	//			.getContentAsString();
	//
	//		List<AccountDTO> accounts = getMapper().readValue(responseBody, new TypeReference<>() { });
	//		assertThat(accounts).hasSize(3);
	//
	//		uri = defaultUriBuilder(TRANSACTIONS_PATH)
	//			.queryParam(PARAM_CURRENCY, "EUR", "CNY")
	//			.build()
	//			.toUri();
	//
	//		responseBody = getMockMvc().perform(get(uri).accept(MediaType.APPLICATION_JSON))
	//			.andExpect(status().isOk())
	//			.andExpect(header().string(XHeaders.TOTAL_COUNT, "2"))
	//			.andExpect(header().doesNotExist(HttpHeaders.LINK))
	//			.andReturn()
	//			.getResponse()
	//			.getContentAsString();
	//
	//		accounts = getMapper().readValue(responseBody, new TypeReference<>() { });
	//		assertThat(accounts).hasSize(2);
	//
	//		uri = defaultUriBuilder(TRANSACTIONS_PATH)
	//			.queryParam(PARAM_CURRENCY, "USD", "CNY")
	//			.build()
	//			.toUri();
	//
	//		responseBody = getMockMvc().perform(get(uri).accept(MediaType.APPLICATION_JSON))
	//			.andExpect(status().isOk())
	//			.andExpect(header().string(XHeaders.TOTAL_COUNT, "1"))
	//			.andExpect(header().doesNotExist(HttpHeaders.LINK))
	//			.andReturn()
	//			.getResponse()
	//			.getContentAsString();
	//
	//		accounts = getMapper().readValue(responseBody, new TypeReference<>() { });
	//		assertThat(accounts).hasSize(1);
	//	}
	//
	//	@Test
	//	void testCustomerIdFilter() throws Exception {
	//		final URI uri = defaultUriBuilder(TRANSACTIONS_PATH)
	//			.queryParam(PARAM_CUSTOMER_ID, "aa69e678-b866-471f-80c2-91a42da4bd6f")
	//			.build()
	//			.toUri();
	//
	//		final String responseBody = getMockMvc().perform(get(uri).accept(MediaType.APPLICATION_JSON))
	//			.andExpect(status().isOk())
	//			.andExpect(header().string(XHeaders.TOTAL_COUNT, "2"))
	//			.andExpect(header().doesNotExist(HttpHeaders.LINK))
	//			.andReturn()
	//			.getResponse()
	//			.getContentAsString();
	//
	//		List<AccountDTO> accounts = getMapper().readValue(responseBody, new TypeReference<>() { });
	//		assertThat(accounts).hasSize(2);
	//		assertThat(accounts.get(0).getCustomerId()).isEqualTo(UUID.fromString("aa69e678-b866-471f-80c2-91a42da4bd6f"));
	//		assertThat(accounts.get(1).getCustomerId()).isEqualTo(UUID.fromString("aa69e678-b866-471f-80c2-91a42da4bd6f"));
	//	}

	@Test
	void testCreateTransaction() throws Exception {
		final TransactionInfo transactionInfo = TransactionInfo.builder()
			.comment("Thank you very much!")
			.build();
		final UUID fromAccountId = UUID.fromString("cf255190-0474-471c-8530-8a5162bc7a54");
		final UUID toAccountId = UUID.fromString("71367d92-2d45-402b-8984-f6481ac98689");
		final TransactionData transactionData = TransactionData.builder()
			.amount(new BigDecimal("100000000"))
			.currency("EUR")
			.fromAccountId(fromAccountId)
			.toAccountId(toAccountId)
			.info(transactionInfo)
			.build();
		String json = getJsonSerializationService().toJson(transactionData);

		final URI uri = defaultUriBuilder(TRANSACTIONS_PATH)
			.build()
			.toUri();

		final LocalDateTime before = localDateTimeNow();
		final String responseBody =
			getMockMvc().perform(post(uri).contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)
					.content(json))
				.andExpect(status().isCreated())
				.andExpect(header().exists(HttpHeaders.LOCATION))
				.andReturn()
				.getResponse()
				.getContentAsString();
		final LocalDateTime after = localDateTimeNow();

		TransactionDTO transaction = getJsonSerializationService().fromJson(responseBody, TransactionDTO.class);
		assertThat(transaction.getId()).isNotNull();
		assertThat(transaction.getTimestamp()).isBetween(before, after);
		assertThat(transaction.getAmount()).isEqualTo("100000000");
		assertThat(transaction.getCurrency()).isEqualTo("EUR");
		assertThat(transaction.getFromAccountId()).isEqualTo(fromAccountId);
		assertThat(transaction.getToAccountId()).isEqualTo(toAccountId);

		final Account fromAccount = accountRepoDsl.getRepo().findById(fromAccountId).get();
		assertThat(fromAccount.getUpdatedAt()).isBetween(before, after);
		assertThat(fromAccount.getBalance()).isEqualTo(new BigDecimal("-99994848.88"));

		final Account toAccount = accountRepoDsl.getRepo().findById(toAccountId).get();
		assertThat(toAccount.getUpdatedAt()).isBetween(before, after);
		assertThat(toAccount.getBalance()).isEqualTo(new BigDecimal("100000030.15"));
	}

	@ParameterizedTest
	@MethodSource("testDataForCreateTransactionForDifferentCurrencies")
	void testCreateTransactionForDifferentCurrencies(final UUID toAccountId, final String transactionCurrency) throws Exception {
		final TransactionInfo transactionInfo = TransactionInfo.builder()
			.comment("Thank you very much!")
			.build();
		final UUID fromAccountId = UUID.fromString("cf255190-0474-471c-8530-8a5162bc7a54");
		final TransactionData transactionData = TransactionData.builder()
			.amount(new BigDecimal("100000000"))
			.currency(transactionCurrency)
			.fromAccountId(fromAccountId)
			.toAccountId(toAccountId)
			.info(transactionInfo)
			.build();
		String json = getJsonSerializationService().toJson(transactionData);

		final URI uri = defaultUriBuilder(TRANSACTIONS_PATH)
			.build()
			.toUri();

		getMockMvc().perform(post(uri).contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)
				.content(json))
			.andExpect(status().isConflict());
	}

	private static Stream<Arguments> testDataForCreateTransactionForDifferentCurrencies() {
		return Stream.of(
			Arguments.of(UUID.fromString("71367d92-2d45-402b-8984-f6481ac98689"), "USD"),
			Arguments.of(UUID.fromString("2492ed49-d04f-43a0-a40e-276ae82191ff"), "EUR"),
			Arguments.of(UUID.fromString("2492ed49-d04f-43a0-a40e-276ae82191ff"), "USD")
		);
	}
}
