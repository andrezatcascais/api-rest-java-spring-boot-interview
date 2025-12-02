package dev.andie.userapi.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Classe base para testes de integração.
 *
 * Características:
 * - @SpringBootTest: Sobe o contexto completo do Spring
 * - @AutoConfigureMockMvc: Configura MockMvc para testes HTTP
 * - @ActiveProfiles("test"): Usa configurações de teste
 * - @Transactional: Rollback automático após cada teste
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
public abstract class BaseIntegrationTest {

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected ObjectMapper objectMapper;
}