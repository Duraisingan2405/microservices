package com.microservice.productservice;

import com.microservice.productservice.dto.ProductRequest;
import com.microservice.productservice.repository.ProductRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;
import org.testcontainers.utility.DockerImageName;

import java.math.BigDecimal;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
class ProductServiceApplicationTests {

	@Container
	static MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:4.4.2");
	@Autowired
	private MockMvc mockMvc;
	@Autowired
	private ObjectMapper objectMapper;
	@Autowired
	private ProductRepository productRepository;

	@DynamicPropertySource
	static void setProperties(DynamicPropertyRegistry dymDynamicPropertyRegistry) {
		dymDynamicPropertyRegistry.add("spring.data.mongodb.uri", mongoDBContainer::getReplicaSetUrl);
	}

	static {
		mongoDBContainer.start();
	}

//	@Test
//	void shouldCreateProduct() throws Exception {
//		ProductRequest productRequest = getProductRequest();
//		String productRequestString = objectMapper.writeValueAsString(productRequest);
//		mockMvc.perform(MockMvcRequestBuilders.post("/api/products")
//						.contentType(MediaType.APPLICATION_JSON)
//						.content(productRequestString))
//				.andExpect(status().isCreated());
//		Assertions.assertEquals(1, productRepository.findAll().size());
//	}

	private ProductRequest getProductRequest() {
		return ProductRequest.builder()
				.name("iPhone 13")
				.description("iPhone 13")
				.price(BigDecimal.valueOf(1200))
				.build();
	}
}

//	    Testcontainers is an open-source Java library that provides lightweight, throwaway instances of common databases,
//		message brokers, or other external services that your Spring Boot application might depend on.
//		It's often used in testing scenarios to create and manage these services as Docker containers, making it easier
//		to write integration tests for your Spring
