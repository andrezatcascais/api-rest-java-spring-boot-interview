package dev.andie.userapi.integration;

import dev.andie.userapi.dto.UsuarioDTO;
import dev.andie.userapi.model.Usuario;
import dev.andie.userapi.repository.UsuarioRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@DisplayName("Testes de Integração - API de Usuários")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class UsuarioIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private UsuarioRepository repository;

    private UsuarioDTO usuarioValido;

    @BeforeEach
    void setUp() {
        // Limpa banco antes de cada teste
        repository.deleteAll();

        // Cria DTO válido para testes
        usuarioValido = new UsuarioDTO();
        usuarioValido.setNome("Andie Test");
        usuarioValido.setEmail("andietest@email.com");
    }

    @AfterEach
    void tearDown() {
        // Garante limpeza após cada teste
        repository.deleteAll();
    }

    // ==================== TESTES DE CRIAÇÃO ====================

    @Test
    @Order(1)
    @DisplayName("Deve criar usuário com sucesso e retornar 201")
    void deveCriarUsuarioComSucesso() throws Exception {
        // Act & Assert
        MvcResult result = mockMvc.perform(post("/api/usuarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(usuarioValido)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.nome").value("Andie Test"))
                .andExpect(jsonPath("$.email").value("andietest@email.com"))
                .andExpect(jsonPath("$.dataCriacao").exists())
                .andReturn();

        // Verifica se foi persistido no banco
        List<Usuario> usuarios = repository.findAll();
        assertThat(usuarios).hasSize(1);
        assertThat(usuarios.get(0).getNome()).isEqualTo("Andie Test");
        assertThat(usuarios.get(0).getEmail()).isEqualTo("andietest@email.com");
        assertThat(usuarios.get(0).getDataCriacao()).isNotNull();
    }

    @Test
    @Order(2)
    @DisplayName("Não deve criar usuário com email duplicado")
    void naoDeveCriarUsuarioComEmailDuplicado() throws Exception {
        // Arrange - Cria primeiro usuário
        criarUsuario("João Test", "joao@email.com");

        // Act & Assert - Tenta criar com mesmo email
        UsuarioDTO usuarioDuplicado = new UsuarioDTO();
        usuarioDuplicado.setNome("José Santos");
        usuarioDuplicado.setEmail("joao@email.com");

        mockMvc.perform(post("/api/usuarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(usuarioDuplicado)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(containsString("Email já cadastrado")));

        // Verifica que só existe 1 usuário no banco
        assertThat(repository.count()).isEqualTo(1);
    }

    @Test
    @Order(3)
    @DisplayName("Não deve criar usuário sem nome")
    void naoDeveCriarUsuarioSemNome() throws Exception {
        // Arrange
        UsuarioDTO usuarioSemNome = new UsuarioDTO();
        usuarioSemNome.setEmail("teste@email.com");

        // Act & Assert
        mockMvc.perform(post("/api/usuarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(usuarioSemNome)))
                .andDo(print())
                .andExpect(status().isBadRequest());

        // Verifica que nada foi salvo
        assertThat(repository.count()).isZero();
    }

    @Test
    @Order(4)
    @DisplayName("Não deve criar usuário sem email")
    void naoDeveCriarUsuarioSemEmail() throws Exception {
        // Arrange
        UsuarioDTO usuarioSemEmail = new UsuarioDTO();
        usuarioSemEmail.setNome("Teste");

        // Act & Assert
        mockMvc.perform(post("/api/usuarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(usuarioSemEmail)))
                .andDo(print())
                .andExpect(status().isBadRequest());

        // Verifica que nada foi salvo
        assertThat(repository.count()).isZero();
    }

    @Test
    @Order(5)
    @DisplayName("Não deve criar usuário com email inválido")
    void naoDeveCriarUsuarioComEmailInvalido() throws Exception {
        // Arrange
        UsuarioDTO usuarioEmailInvalido = new UsuarioDTO();
        usuarioEmailInvalido.setNome("Teste");
        usuarioEmailInvalido.setEmail("emailinvalido");

        // Act & Assert
        mockMvc.perform(post("/api/usuarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(usuarioEmailInvalido)))
                .andDo(print())
                .andExpect(status().isBadRequest());

        // Verifica que nada foi salvo
        assertThat(repository.count()).isZero();
    }

    // ==================== TESTES DE LISTAGEM ====================

    @Test
    @Order(6)
    @DisplayName("Deve listar todos os usuários com paginação")
    void deveListarTodosUsuariosComPaginacao() throws Exception {
        // Arrange - Cria 3 usuários
        criarUsuario("Ana Costa", "ana@email.com");
        criarUsuario("Bruno Lima", "bruno@email.com");
        criarUsuario("Carla Dias", "carla@email.com");

        // Act & Assert
        mockMvc.perform(get("/api/usuarios")
                        .param("page", "0")
                        .param("size", "10")
                        .param("sort", "nome,asc"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(3)))
                .andExpect(jsonPath("$.content[0].nome").value("Ana Costa"))
                .andExpect(jsonPath("$.content[1].nome").value("Bruno Lima"))
                .andExpect(jsonPath("$.content[2].nome").value("Carla Dias"))
                .andExpect(jsonPath("$.totalElements").value(3))
                .andExpect(jsonPath("$.totalPages").value(1));
    }

    @Test
    @Order(7)
    @DisplayName("Deve retornar lista vazia quando não há usuários")
    void deveRetornarListaVaziaQuandoNaoHaUsuarios() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/api/usuarios"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(0)))
                .andExpect(jsonPath("$.totalElements").value(0));
    }

    @Test
    @Order(8)
    @DisplayName("Deve aplicar paginação corretamente")
    void deveAplicarPaginacaoCorretamente() throws Exception {
        // Arrange - Cria 15 usuários
        for (int i = 1; i <= 15; i++) {
            criarUsuario("Usuario " + i, "user" + i + "@email.com");
        }

        // Act & Assert - Primeira página
        mockMvc.perform(get("/api/usuarios")
                        .param("page", "0")
                        .param("size", "5"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(5)))
                .andExpect(jsonPath("$.totalElements").value(15))
                .andExpect(jsonPath("$.totalPages").value(3))
                .andExpect(jsonPath("$.pageable.pageNumber").value(0));

        // Segunda página
        mockMvc.perform(get("/api/usuarios")
                        .param("page", "1")
                        .param("size", "5"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(5)))
                .andExpect(jsonPath("$.pageable.pageNumber").value(1));

        // Terceira página
        mockMvc.perform(get("/api/usuarios")
                        .param("page", "2")
                        .param("size", "5"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(5)))
                .andExpect(jsonPath("$.pageable.pageNumber").value(2));
    }

    @Test
    @Order(9)
    @DisplayName("Deve ordenar usuários por nome decrescente")
    void deveOrdenarUsuariosPorNomeDecrescente() throws Exception {
        // Arrange
        criarUsuario("Zeca", "zeca@email.com");
        criarUsuario("Ana", "ana@email.com");
        criarUsuario("Maria", "andietest@email.com");

        // Act & Assert
        mockMvc.perform(get("/api/usuarios")
                        .param("sort", "nome,desc"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].nome").value("Zeca"))
                .andExpect(jsonPath("$.content[1].nome").value("Maria"))
                .andExpect(jsonPath("$.content[2].nome").value("Ana"));
    }

    // ==================== TESTES DE BUSCA POR ID ====================

    @Test
    @Order(10)
    @DisplayName("Deve buscar usuário por ID com sucesso")
    void deveBuscarUsuarioPorIdComSucesso() throws Exception {
        // Arrange
        Usuario usuario = criarUsuario("Pedro Santos", "pedro@email.com");

        // Act & Assert
        mockMvc.perform(get("/api/usuarios/" + usuario.getId()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(usuario.getId()))
                .andExpect(jsonPath("$.nome").value("Pedro Santos"))
                .andExpect(jsonPath("$.email").value("pedro@email.com"))
                .andExpect(jsonPath("$.dataCriacao").exists());
    }

    @Test
    @Order(11)
    @DisplayName("Deve retornar 404 ao buscar usuário inexistente")
    void deveRetornar404AoBuscarUsuarioInexistente() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/api/usuarios/999"))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value(containsString("Usuário não encontrado")));
    }

    // ==================== TESTES DE ATUALIZAÇÃO ====================

    @Test
    @Order(12)
    @DisplayName("Deve atualizar usuário com sucesso")
    void deveAtualizarUsuarioComSucesso() throws Exception {
        // Arrange
        Usuario usuario = criarUsuario("João Test", "joao@email.com");

        UsuarioDTO usuarioAtualizado = new UsuarioDTO();
        usuarioAtualizado.setNome("Jesus Test");
        usuarioAtualizado.setEmail("jesus.test@email.com");

        // Act & Assert
        mockMvc.perform(put("/api/usuarios/" + usuario.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(usuarioAtualizado)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(usuario.getId()))
                .andExpect(jsonPath("$.nome").value("Jesus Test"))
                .andExpect(jsonPath("$.email").value("jesus.test@email.com"));

        // Verifica no banco
        Usuario usuarioNoBanco = repository.findById(usuario.getId()).orElseThrow();
        assertThat(usuarioNoBanco.getNome()).isEqualTo("Jesus Test");
        assertThat(usuarioNoBanco.getEmail()).isEqualTo("jesus.test@email.com");
    }

    @Test
    @Order(13)
    @DisplayName("Deve manter mesmo email ao atualizar")
    void deveManterMesmoEmailAoAtualizar() throws Exception {
        // Arrange
        Usuario usuario = criarUsuario("Maria Test", "maria.test@email.com");

        UsuarioDTO usuarioAtualizado = new UsuarioDTO();
        usuarioAtualizado.setNome("Maria de Nazare test");
        usuarioAtualizado.setEmail("maria.test@email.com"); // Mesmo email

        // Act & Assert
        mockMvc.perform(put("/api/usuarios/" + usuario.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(usuarioAtualizado)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value("Maria de Nazare test"))
                .andExpect(jsonPath("$.email").value("maria.test@email.com"));
    }

    @Test
    @Order(14)
    @DisplayName("Não deve atualizar com email já cadastrado")
    void naoDeveAtualizarComEmailJaCadastrado() throws Exception {
        // Arrange
        Usuario usuario1 = criarUsuario("Andie Test", "andietest@email.com");
        Usuario usuario2 = criarUsuario("Jesus Test", "jesus.test@email.com");

        UsuarioDTO usuarioAtualizado = new UsuarioDTO();
        usuarioAtualizado.setNome("João Test");
        usuarioAtualizado.setEmail("andietest@email.com"); // Email já usado

        // Act & Assert
        mockMvc.perform(put("/api/usuarios/" + usuario2.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(usuarioAtualizado)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(containsString("Email já cadastrado")));

        // Verifica que não foi alterado
        Usuario usuarioNoBanco = repository.findById(usuario2.getId()).orElseThrow();
        assertThat(usuarioNoBanco.getEmail()).isEqualTo("jesus.test@email.com");
    }

    @Test
    @Order(15)
    @DisplayName("Deve retornar 404 ao atualizar usuário inexistente")
    void deveRetornar404AoAtualizarUsuarioInexistente() throws Exception {
        // Arrange
        UsuarioDTO usuarioAtualizado = new UsuarioDTO();
        usuarioAtualizado.setNome("Teste");
        usuarioAtualizado.setEmail("teste@email.com");

        // Act & Assert
        mockMvc.perform(put("/api/usuarios/999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(usuarioAtualizado)))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    // ==================== TESTES DE EXCLUSÃO ====================

    @Test
    @Order(16)
    @DisplayName("Deve deletar usuário com sucesso")
    void deveDeletarUsuarioComSucesso() throws Exception {
        // Arrange
        Usuario usuario = criarUsuario("Teste Delete", "delete@email.com");
        Long id = usuario.getId();

        // Verifica que existe
        assertThat(repository.existsById(id)).isTrue();

        // Act & Assert
        mockMvc.perform(delete("/api/usuarios/" + id))
                .andDo(print())
                .andExpect(status().isNoContent());

        // Verifica que foi deletado
        assertThat(repository.existsById(id)).isFalse();
        assertThat(repository.count()).isZero();
    }

    @Test
    @Order(17)
    @DisplayName("Deve retornar 404 ao deletar usuário inexistente")
    void deveRetornar404AoDeletarUsuarioInexistente() throws Exception {
        // Act & Assert
        mockMvc.perform(delete("/api/usuarios/999"))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value(containsString("Usuário não encontrado")));
    }

    // ==================== TESTE DE FLUXO COMPLETO ====================

    @Test
    @Order(18)
    @DisplayName("Deve executar fluxo completo: criar, listar, buscar, atualizar, deletar")
    void deveExecutarFluxoCompleto() throws Exception {
        // 1. Criar usuário
        MvcResult createResult = mockMvc.perform(post("/api/usuarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(usuarioValido)))
                .andExpect(status().isCreated())
                .andReturn();

        String responseBody = createResult.getResponse().getContentAsString();
        UsuarioDTO usuarioCriado = objectMapper.readValue(responseBody, UsuarioDTO.class);
        Long id = usuarioCriado.getId();

        // 2. Listar e verificar que existe
        mockMvc.perform(get("/api/usuarios"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)));

        // 3. Buscar por ID
        mockMvc.perform(get("/api/usuarios/" + id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value("Andie Test"));

        // 4. Atualizar
        UsuarioDTO usuarioAtualizado = new UsuarioDTO();
        usuarioAtualizado.setNome("Ana Test");
        usuarioAtualizado.setEmail("ana.test@email.com");

        mockMvc.perform(put("/api/usuarios/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(usuarioAtualizado)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value("Ana Test"));

        // 5. Verificar atualização
        mockMvc.perform(get("/api/usuarios/" + id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value("Ana Test"))
                .andExpect(jsonPath("$.email").value("ana.test@email.com"));

        // 6. Deletar
        mockMvc.perform(delete("/api/usuarios/" + id))
                .andExpect(status().isNoContent());

        // 7. Verificar que foi deletado
        mockMvc.perform(get("/api/usuarios/" + id))
                .andExpect(status().isNotFound());

        // 8. Verificar lista vazia
        mockMvc.perform(get("/api/usuarios"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(0)));
    }

    // ==================== MÉTODOS AUXILIARES ====================

    /**
     * Método auxiliar para criar usuário diretamente no banco.
     * Útil para preparar cenários de teste.
     */
    private Usuario criarUsuario(String nome, String email) {
        Usuario usuario = new Usuario();
        usuario.setNome(nome);
        usuario.setEmail(email);
        usuario.setDataCriacao(LocalDateTime.now());
        return repository.save(usuario);
    }
}