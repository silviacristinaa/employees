package com.github.silviacristinaa.employees.exceptions;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
public class GlobalExceptionHandlerTest {
	
	private static final String CONFLICT = "Conflict";
	private static final String NOT_FOUND_MSG = "Not found";
	
	@InjectMocks
	private GlobalExceptionHandler globalExceptionHandler;

	@Test
	void whenConflictExceptionReturnResponseEntity() {
		ResponseEntity<ErrorMessage> response = globalExceptionHandler
				.handleMethodArgumentConflictException(new ConflictException("Cpf already registered in the system"));
		
		assertNotNull(response);
		assertNotNull(response.getBody());
		assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
		assertEquals(ResponseEntity.class, response.getClass());
		assertEquals(ErrorMessage.class, response.getBody().getClass());
		assertEquals(CONFLICT, response.getBody().getMessage());
		assertEquals("Cpf already registered in the system", response.getBody().getErrors().get(0));
	}
	
	@Test
	void whenNotFoundExceptionReturnResponseEntity() {
		ResponseEntity<ErrorMessage> response = globalExceptionHandler
				.handleMethodArgumentNotFoundException(
						new NotFoundException(String.format("Employee %s not found", 1l)));
		
		assertNotNull(response);
		assertNotNull(response.getBody());
		assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
		assertEquals(ResponseEntity.class, response.getClass());
		assertEquals(ErrorMessage.class, response.getBody().getClass());
		assertEquals(NOT_FOUND_MSG, response.getBody().getMessage());
		assertEquals("Employee 1 not found", response.getBody().getErrors().get(0));
	}
}