package dev.andie.userapi.controller;

import dev.andie.userapi.dto.UsuarioDTO;
import dev.andie.userapi.exception.*;
import dev.andie.userapi.service.UsuarioService;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UsuarioController.class)
@DisplayName("Testes do UsuarioController")
class UsuarioControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UsuarioService service;

    private UsuarioDTO usuarioSetUpDTO;

    @BeforeEach
    void setUp() {
        usuarioSetUpDTO = new UsuarioDTO();
        usuarioSetUpDTO.setId(1L);
        usuarioSetUpDTO.setNome("Usuario setup test");
        usuarioSetUpDTO.setEmail("usuariosetuptest@email.com");
        usuarioSetUpDTO.setDataCriacao(LocalDateTime.now());
    }

    // ==================== TESTES DE GET /api/usuarios ====================

    @Test
    @DisplayName("GET /api/usuarios - Deve retornar lista de usuários com status 200")
    void deveRetornarListaDeUsuariosComSucesso() throws Exception {
        // Arrange
        List<UsuarioDTO> usuarios = new ArrayList<>();
        usuarios.add(usuarioSetUpDTO);

        Page<UsuarioDTO> page = new PageImpl<>(usuarios, PageRequest.of(0, 10), 1);
        when(service.listarTodos(any(Pageable.class))).thenReturn(page);

        // Act & Assert
        mockMvc.perform(get("/api/usuarios")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].nome").value(usuarioSetUpDTO.getNome()))
                .andExpect(jsonPath("$.content[0].email").value(usuarioSetUpDTO.getEmail()));

        verify(service, times(1)).listarTodos(any(Pageable.class));
    }

    @Test
    @DisplayName("GET /api/usuarios - Deve retornar lista vazia com status 200")
    void deveRetornarListaVazia() throws Exception {
        // Arrange - Adiciona o PageRequest ao PageImpl
        Page<UsuarioDTO> pageVazia = new PageImpl<>(Collections.emptyList(), PageRequest.of(0, 20), 0);
        when(service.listarTodos(any(Pageable.class))).thenReturn(pageVazia);

        // Act & Assert
        mockMvc.perform(get("/api/usuarios")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(0)));

        verify(service, times(1)).listarTodos(any(Pageable.class));
    }

    @Test
    @DisplayName("GET /api/usuarios - Deve aceitar parâmetros de paginação")
    void deveAceitarParametrosDePaginacao() throws Exception {

        List<UsuarioDTO> lista = new ArrayList<>();
        lista.add(usuarioSetUpDTO);

        // Arrange
        Page<UsuarioDTO> page = new PageImpl<>(lista, PageRequest.of(0, 5), 1);
        when(service.listarTodos(any())).thenReturn(page);

        // Act & Assert
        mockMvc.perform(get("/api/usuarios")
                        .param("page", "0")
                        .param("size", "5")
                        .param("sort", "nome,asc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.pageable.pageSize").value(5))
                .andExpect(jsonPath("$.pageable.pageNumber").value(0));

        verify(service, times(1)).listarTodos(any());
    }

    // ==================== TESTES DE GET /api/usuarios/{id} ====================

    @Test
    @DisplayName("GET /api/usuarios/{id} - Deve retornar usuário com status 200")
    void deveBuscarUsuarioPorIdComSucesso() throws Exception {
        // Arrange
        when(service.buscarPorId(1L)).thenReturn(usuarioSetUpDTO);

        // Act & Assert
        mockMvc.perform(get("/api/usuarios/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(usuarioSetUpDTO.getId()))
                .andExpect(jsonPath("$.nome").value(usuarioSetUpDTO.getNome()))
                .andExpect(jsonPath("$.email").value(usuarioSetUpDTO.getEmail()));

        verify(service, times(1)).buscarPorId(1L);
    }

    @Test
    @DisplayName("GET /api/usuarios/{id} - Deve retornar 404 quando usuário não existe")
    void deveRetornar404QuandoUsuarioNaoExiste() throws Exception {
        // Arrange
        when(service.buscarPorId(999L))
                .thenThrow(new ResourceNotFoundException("Usuário não encontrado com ID: 999"));

        // Act & Assert
        mockMvc.perform(get("/api/usuarios/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Usuário não encontrado com ID: 999"));

        verify(service, times(1)).buscarPorId(999L);
    }

    // ==================== TESTES DE POST /api/usuarios ====================

    @Test
    @DisplayName("POST /api/usuarios - Deve criar usuário com status 201")
    void deveCriarUsuarioComSucesso() throws Exception {
        // Arrange
        UsuarioDTO novoUsuario = new UsuarioDTO();
        novoUsuario.setNome(usuarioSetUpDTO.getNome());
        novoUsuario.setEmail(usuarioSetUpDTO.getEmail());

        // Criamos o objeto que simula a resposta do service
        UsuarioDTO usuarioCriado = new UsuarioDTO();
        usuarioCriado.setId(2L);
        usuarioCriado.setNome(novoUsuario.getNome());
        usuarioCriado.setEmail(novoUsuario.getEmail());
        usuarioCriado.setDataCriacao(LocalDateTime.now());

        when(service.criar(any(UsuarioDTO.class))).thenReturn(usuarioCriado);

        // Act & Assert
        mockMvc.perform(post("/api/usuarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(novoUsuario)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(2))
                .andExpect(jsonPath("$.nome").value(usuarioCriado.getNome()))
                .andExpect(jsonPath("$.email").value(usuarioCriado.getEmail()))
                .andExpect(jsonPath("$.dataCriacao").exists());

        verify(service, times(1)).criar(any(UsuarioDTO.class));
    }

    @Test
    @DisplayName("POST /api/usuarios - Deve retornar 400 quando email já existe")
    void deveRetornar400QuandoEmailJaExiste() throws Exception {
        // Arrange
        UsuarioDTO novoUsuario = new UsuarioDTO();
        novoUsuario.setNome(usuarioSetUpDTO.getNome());
        novoUsuario.setEmail(usuarioSetUpDTO.getEmail());

        when(service.criar(any(UsuarioDTO.class)))
                .thenThrow(new ValidationException("Email já cadastrado: " + usuarioSetUpDTO.getEmail()));

        // Act & Assert
        mockMvc.perform(post("/api/usuarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(novoUsuario)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Email já cadastrado: " + usuarioSetUpDTO.getEmail()));

        verify(service, times(1)).criar(any(UsuarioDTO.class));
    }

    @Test
    @DisplayName("POST /api/usuarios - Deve retornar 400 quando nome está vazio")
    void deveRetornar400QuandoNomeEstaVazio() throws Exception {
        // Arrange
        UsuarioDTO usuarioInvalido = new UsuarioDTO();
        usuarioInvalido.setNome("");
        usuarioInvalido.setEmail("teste@email.com");

        // Act & Assert
        mockMvc.perform(post("/api/usuarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(usuarioInvalido)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /api/usuarios - Deve retornar 400 quando email é inválido")
    void deveRetornar400QuandoEmailEInvalido() throws Exception {
        // Arrange
        UsuarioDTO usuarioInvalido = new UsuarioDTO();
        usuarioInvalido.setNome("Teste");
        usuarioInvalido.setEmail("emailinvalido");

        // Act & Assert
        mockMvc.perform(post("/api/usuarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(usuarioInvalido)))
                .andExpect(status().isBadRequest());
    }

    // ==================== TESTES DE PUT /api/usuarios/{id} ====================

    @Test
    @DisplayName("PUT /api/usuarios/{id} - Deve atualizar usuário com status 200")
    void deveAtualizarUsuarioComSucesso() throws Exception {
        // Arrange
        UsuarioDTO usuarioAtualizado = new UsuarioDTO();
        usuarioAtualizado.setId(1L);
        usuarioAtualizado.setNome(usuarioSetUpDTO.getNome());
        usuarioAtualizado.setEmail(usuarioSetUpDTO.getEmail());
        usuarioAtualizado.setDataCriacao(LocalDateTime.now());

        when(service.atualizar(eq(1L), any(UsuarioDTO.class))).thenReturn(usuarioAtualizado);

        // Act & Assert
        mockMvc.perform(put("/api/usuarios/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(usuarioAtualizado)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(usuarioAtualizado.getId()))
                .andExpect(jsonPath("$.nome").value(usuarioAtualizado.getNome()))
                .andExpect(jsonPath("$.email").value(usuarioAtualizado.getEmail()));

        verify(service, times(1)).atualizar(eq(1L), any(UsuarioDTO.class));
    }

    @Test
    @DisplayName("PUT /api/usuarios/{id} - Deve retornar 404 quando usuário não existe")
    void deveRetornar404AoAtualizarUsuarioInexistente() throws Exception {
        // Arrange
        UsuarioDTO usuarioAtualizado = new UsuarioDTO();
        usuarioAtualizado.setNome("UsuarioInexistente - test");
        usuarioAtualizado.setEmail("usuarioinexistente@email.com");

        when(service.atualizar(eq(999L), any(UsuarioDTO.class)))
                .thenThrow(new ResourceNotFoundException("Usuário não encontrado com ID: 999"));

        // Act & Assert
        mockMvc.perform(put("/api/usuarios/999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(usuarioAtualizado)))
                .andExpect(status().isNotFound());

        verify(service, times(1)).atualizar(eq(999L), any(UsuarioDTO.class));
    }

    // ==================== TESTES DE DELETE /api/usuarios/{id} ====================

    @Test
    @DisplayName("DELETE /api/usuarios/{id} - Deve deletar usuário com status 204")
    void deveDeletarUsuarioComSucesso() throws Exception {
        // Arrange
        doNothing().when(service).deletar(1L);

        // Act & Assert
        mockMvc.perform(delete("/api/usuarios/1"))
                .andExpect(status().isNoContent());

        verify(service, times(1)).deletar(1L);
    }

    @Test
    @DisplayName("DELETE /api/usuarios/{id} - Deve retornar 404 quando usuário não existe")
    void deveRetornar404AoDeletarUsuarioInexistente() throws Exception {
        // Arrange
        doThrow(new ResourceNotFoundException("Usuário não encontrado com ID: 999"))
                .when(service).deletar(999L);

        // Act & Assert
        mockMvc.perform(delete("/api/usuarios/999"))
                .andExpect(status().isNotFound());

        verify(service, times(1)).deletar(999L);
    }
}