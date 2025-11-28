package dev.andie.userapi.service;

import dev.andie.userapi.dto.UsuarioDTO;
import dev.andie.userapi.exception.ResourceNotFoundException;
import dev.andie.userapi.exception.ValidationException;
import dev.andie.userapi.model.Usuario;
import dev.andie.userapi.repository.UsuarioRepository;
import dev.andie.userapi.service.impl.UsuarioServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes do UsuarioServiceImpl")
class UsuarioServiceImplTest {

    @Mock
    private UsuarioRepository repository;

    @InjectMocks
    private UsuarioServiceImpl service;

    private Usuario usuario;
    private UsuarioDTO usuarioSetUpDTO;

    @BeforeEach
    void setUp() {
        usuario = new Usuario();
        usuario.setId(1L);
        usuario.setNome("Usuario test");
        usuario.setEmail("usuariotest@email.com");
        usuario.setDataCriacao(LocalDateTime.now());

        usuarioSetUpDTO = new UsuarioDTO();
        usuarioSetUpDTO.setNome("Usuario test");
        usuarioSetUpDTO.setEmail("usuariotest@email.com");
    }

    // ==================== TESTES DE LISTAR ====================

    @Test
    @DisplayName("Deve listar todos os usuários com paginação")
    void deveListarTodosUsuariosComPaginacao() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);
        List<Usuario> usuarios = Arrays.asList(usuario);
        Page<Usuario> page = new PageImpl<>(usuarios, pageable, usuarios.size());

        when(repository.findAll(pageable)).thenReturn(page);

        // Act
        Page<UsuarioDTO> resultado = service.listarTodos(pageable);

        // Assert
        assertThat(resultado).isNotNull();
        assertThat(resultado.getContent()).hasSize(1);
        assertThat(resultado.getContent().get(0).getNome()).isEqualTo(usuarioSetUpDTO.getNome());
        assertThat(resultado.getContent().get(0).getEmail()).isEqualTo(usuarioSetUpDTO.getEmail());

        verify(repository, times(1)).findAll(pageable);
    }

    @Test
    @DisplayName("Deve retornar página vazia quando não houver usuários")
    void deveRetornarPaginaVaziaQuandoNaoHouverUsuarios() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);
        Page<Usuario> pageVazia = new PageImpl<>(Arrays.asList());

        when(repository.findAll(pageable)).thenReturn(pageVazia);

        // Act
        Page<UsuarioDTO> resultado = service.listarTodos(pageable);

        // Assert
        assertThat(resultado).isNotNull();
        assertThat(resultado.getContent()).isEmpty();
        assertThat(resultado.getTotalElements()).isZero();

        verify(repository, times(1)).findAll(pageable);
    }

    // ==================== TESTES DE BUSCAR POR ID ====================

    @Test
    @DisplayName("Deve buscar usuário por ID com sucesso")
    void deveBuscarUsuarioPorIdComSucesso() {
        // Arrange
        when(repository.findById(1L)).thenReturn(Optional.of(usuario));

        // Act
        UsuarioDTO resultado = service.buscarPorId(1L);

        // Assert
        assertThat(resultado).isNotNull();
        assertThat(resultado.getId()).isEqualTo(1L);
        assertThat(resultado.getNome()).isEqualTo(usuarioSetUpDTO.getNome());
        assertThat(resultado.getEmail()).isEqualTo(usuarioSetUpDTO.getEmail());

        verify(repository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Deve lançar exceção quando usuário não for encontrado")
    void deveLancarExcecaoQuandoUsuarioNaoForEncontrado() {
        // Arrange
        when(repository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> service.buscarPorId(999L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Usuário não encontrado com ID: 999");

        verify(repository, times(1)).findById(999L);
    }

    // ==================== TESTES DE CRIAR ====================

    @Test
    @DisplayName("Deve criar usuário com sucesso")
    void deveCriarUsuarioComSucesso() {
        // Arrange
        when(repository.existsByEmail(anyString())).thenReturn(false);
        when(repository.save(any(Usuario.class))).thenReturn(usuario);

        // Act
        UsuarioDTO resultado = service.criar(usuarioSetUpDTO);

        // Assert
        assertThat(resultado).isNotNull();
        assertThat(resultado.getNome()).isEqualTo(usuarioSetUpDTO.getNome());
        assertThat(resultado.getEmail()).isEqualTo(usuarioSetUpDTO.getEmail());

        verify(repository, times(1)).existsByEmail(usuarioSetUpDTO.getEmail());
        verify(repository, times(1)).save(any(Usuario.class));
    }

    @Test
    @DisplayName("Deve lançar exceção ao criar usuário com email duplicado")
    void deveLancarExcecaoAoCriarUsuarioComEmailDuplicado() {
        // Arrange
        when(repository.existsByEmail(usuarioSetUpDTO.getEmail())).thenReturn(true);

        // Act & Assert
        assertThatThrownBy(() -> service.criar(usuarioSetUpDTO))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("Email já cadastrado: " + usuarioSetUpDTO.getEmail());

        verify(repository, times(1)).existsByEmail(usuarioSetUpDTO.getEmail());
        verify(repository, never()).save(any(Usuario.class));
    }

    @Test
    @DisplayName("Deve lançar exceção ao criar usuário sem nome")
    void deveLancarExcecaoAoCriarUsuarioSemNome() {
        // Arrange
        usuarioSetUpDTO.setNome(null);

        // Act & Assert
        assertThatThrownBy(() -> service.criar(usuarioSetUpDTO))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("Nome é obrigatório");

        verify(repository, never()).save(any(Usuario.class));
    }

    @Test
    @DisplayName("Deve lançar exceção ao criar usuário com nome vazio")
    void deveLancarExcecaoAoCriarUsuarioComNomeVazio() {
        // Arrange
        usuarioSetUpDTO.setNome("   ");

        // Act & Assert
        assertThatThrownBy(() -> service.criar(usuarioSetUpDTO))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("Nome é obrigatório");

        verify(repository, never()).save(any(Usuario.class));
    }

    @Test
    @DisplayName("Deve lançar exceção ao criar usuário sem email")
    void deveLancarExcecaoAoCriarUsuarioSemEmail() {
        // Arrange
        usuarioSetUpDTO.setEmail(null);

        // Act & Assert
        assertThatThrownBy(() -> service.criar(usuarioSetUpDTO))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("Email é obrigatório");

        verify(repository, never()).save(any(Usuario.class));
    }

    @Test
    @DisplayName("Deve lançar exceção ao criar usuário com email vazio")
    void deveLancarExcecaoAoCriarUsuarioComEmailVazio() {
        // Arrange
        usuarioSetUpDTO.setEmail("   ");

        // Act & Assert
        assertThatThrownBy(() -> service.criar(usuarioSetUpDTO))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("Email é obrigatório");

        verify(repository, never()).save(any(Usuario.class));
    }

    // ==================== TESTES DE ATUALIZAR ====================

    @Test
    @DisplayName("Deve atualizar usuário com sucesso")
    void deveAtualizarUsuarioComSucesso() {
        // Arrange
        UsuarioDTO dtoAtualizado = new UsuarioDTO();
        dtoAtualizado.setNome("Usuario test atualizado");
        dtoAtualizado.setEmail("usuario.atualizado@email.com");

        Usuario usuarioAtualizado = new Usuario();
        usuarioAtualizado.setId(1L);
        usuarioAtualizado.setNome(dtoAtualizado.getNome());
        usuarioAtualizado.setEmail(dtoAtualizado.getEmail());
        usuarioAtualizado.setDataCriacao(usuario.getDataCriacao());

        when(repository.findById(1L)).thenReturn(Optional.of(usuario));
        when(repository.existsByEmail(dtoAtualizado.getEmail())).thenReturn(false);
        when(repository.save(any(Usuario.class))).thenReturn(usuarioAtualizado);

        // Act
        UsuarioDTO resultado = service.atualizar(1L, dtoAtualizado);

        // Assert
        assertThat(resultado).isNotNull();
        assertThat(resultado.getNome()).isEqualTo(dtoAtualizado.getNome());
        assertThat(resultado.getEmail()).isEqualTo(dtoAtualizado.getEmail());

        verify(repository, times(1)).findById(1L);
        verify(repository, times(1)).save(any(Usuario.class));
    }

    @Test
    @DisplayName("Deve atualizar usuário mantendo mesmo email")
    void deveAtualizarUsuarioMantendoMesmoEmail() {
        // Arrange
        UsuarioDTO dtoAtualizado = new UsuarioDTO();
        dtoAtualizado.setNome(usuarioSetUpDTO.getNome());
        dtoAtualizado.setEmail(usuarioSetUpDTO.getEmail());

        Usuario usuarioAtualizado = new Usuario();
        usuarioAtualizado.setId(1L);
        usuarioAtualizado.setNome(dtoAtualizado.getNome());
        usuarioAtualizado.setEmail(dtoAtualizado.getEmail());
        usuarioAtualizado.setDataCriacao(usuario.getDataCriacao());

        when(repository.findById(1L)).thenReturn(Optional.of(usuario));
        when(repository.save(any(Usuario.class))).thenReturn(usuarioAtualizado);

        // Act
        UsuarioDTO resultado = service.atualizar(1L, dtoAtualizado);

        // Assert
        assertThat(resultado).isNotNull();
        assertThat(resultado.getNome()).isEqualTo(usuarioAtualizado.getNome());
        assertThat(resultado.getEmail()).isEqualTo(usuarioAtualizado.getEmail());

        verify(repository, times(1)).findById(1L);
        verify(repository, never()).existsByEmail(anyString()); // Não verifica email duplicado
        verify(repository, times(1)).save(any(Usuario.class));
    }

    @Test
    @DisplayName("Deve lançar exceção ao atualizar com email já cadastrado")
    void deveLancarExcecaoAoAtualizarComEmailJaCadastrado() {
        // Arrange
        UsuarioDTO dtoAtualizado = new UsuarioDTO();
        dtoAtualizado.setNome(usuarioSetUpDTO.getNome());
        dtoAtualizado.setEmail("outro@email.com");

        when(repository.findById(1L)).thenReturn(Optional.of(usuario));
        when(repository.existsByEmail("outro@email.com")).thenReturn(true);

        // Act & Assert
        assertThatThrownBy(() -> service.atualizar(1L, dtoAtualizado))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("Email já cadastrado: outro@email.com");

        verify(repository, times(1)).findById(1L);
        verify(repository, never()).save(any(Usuario.class));
    }

    @Test
    @DisplayName("Deve lançar exceção ao atualizar usuário inexistente")
    void deveLancarExcecaoAoAtualizarUsuarioInexistente() {
        // Arrange
        when(repository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> service.atualizar(999L, usuarioSetUpDTO))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Usuário não encontrado com ID: 999");

        verify(repository, times(1)).findById(999L);
        verify(repository, never()).save(any(Usuario.class));
    }

    @Test
    @DisplayName("Deve lançar exceção ao atualizar sem nome")
    void deveLancarExcecaoAoAtualizarSemNome() {
        // Arrange
        usuarioSetUpDTO.setNome(null);
        when(repository.findById(1L)).thenReturn(Optional.of(usuario));

        // Act & Assert
        assertThatThrownBy(() -> service.atualizar(1L, usuarioSetUpDTO))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("Nome é obrigatório");

        verify(repository, never()).save(any(Usuario.class));
    }

    @Test
    @DisplayName("Deve lançar exceção ao atualizar sem email")
    void deveLancarExcecaoAoAtualizarSemEmail() {
        // Arrange
        usuarioSetUpDTO.setEmail(null);
        when(repository.findById(1L)).thenReturn(Optional.of(usuario));

        // Act & Assert
        assertThatThrownBy(() -> service.atualizar(1L, usuarioSetUpDTO))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("Email é obrigatório");

        verify(repository, never()).save(any(Usuario.class));
    }

    // ==================== TESTES DE DELETAR ====================

    @Test
    @DisplayName("Deve deletar usuário com sucesso")
    void deveDeletarUsuarioComSucesso() {
        // Arrange
        when(repository.existsById(1L)).thenReturn(true);
        doNothing().when(repository).deleteById(1L);

        // Act
        service.deletar(1L);

        // Assert
        verify(repository, times(1)).existsById(1L);
        verify(repository, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("Deve lançar exceção ao deletar usuário inexistente")
    void deveLancarExcecaoAoDeletarUsuarioInexistente() {
        // Arrange
        when(repository.existsById(999L)).thenReturn(false);

        // Act & Assert
        assertThatThrownBy(() -> service.deletar(999L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Usuário não encontrado com ID: 999");

        verify(repository, times(1)).existsById(999L);
        verify(repository, never()).deleteById(anyLong());
    }
}