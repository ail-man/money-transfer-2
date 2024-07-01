package com.ail.home.transfer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.client.RestOperations;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ail.home.transfer.service.JsonSerializationService;

import lombok.Getter;

@SpringBootTest(classes = TestConfig.class)
@ActiveProfiles("test")
@AutoConfigureMockMvc
@Getter
@DirtiesContext
public class SpringTestContextInitialization {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper mapper;

	@SpyBean
	private JsonSerializationService jsonSerializationService;

	@MockBean
	private RestOperations restOperations;

	@Autowired
	private ApplicationEventPublisher applicationEventPublisher;
}
