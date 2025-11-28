package dev.andie.userapi.service;


import dev.andie.userapi.dto.UsuarioDTO;
import dev.andie.userapi.exception.*;
import dev.andie.userapi.model.Usuario;
import dev.andie.userapi.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UsuarioService {

    private final UsuarioRepository repository;

    @Transactional(readOnly = true)
    public Page<UsuarioDTO> listarTodos(Pageable pageable) {
        return repository.findAll(pageable).map(this::toDTO);
    }

    @Transactional(readOnly = true)
    public UsuarioDTO buscarPorId(Long id) {
        Usuario usuario = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado com ID: " + id));
        return toDTO(usuario);
    }

    @Transactional
    public UsuarioDTO criar(UsuarioDTO dto) {
        validarUsuario(dto, null);

        if (repository.existsByEmail(dto.getEmail())) {
            throw new ValidationException("Email já cadastrado: " + dto.getEmail());
        }

        Usuario usuario = toEntity(dto);
        usuario = repository.save(usuario);
        return toDTO(usuario);
    }

    @Transactional
    public UsuarioDTO atualizar(Long id, UsuarioDTO dto) {
        Usuario usuario = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado com ID: " + id));

        validarUsuario(dto, id);

        if (!usuario.getEmail().equals(dto.getEmail()) && repository.existsByEmail(dto.getEmail())) {
            throw new ValidationException("Email já cadastrado: " + dto.getEmail());
        }

        usuario.setNome(dto.getNome());
        usuario.setEmail(dto.getEmail());
        usuario = repository.save(usuario);
        return toDTO(usuario);
    }

    @Transactional
    public void deletar(Long id) {
        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException("Usuário não encontrado com ID: " + id);
        }
        repository.deleteById(id);
    }

    private void validarUsuario(UsuarioDTO dto, Long id) {
        if (dto.getNome() == null || dto.getNome().trim().isEmpty()) {
            throw new ValidationException("Nome é obrigatório");
        }
        if (dto.getEmail() == null || dto.getEmail().trim().isEmpty()) {
            throw new ValidationException("Email é obrigatório");
        }
    }

    private UsuarioDTO toDTO(Usuario usuario) {
        return new UsuarioDTO(
                usuario.getId(),
                usuario.getNome(),
                usuario.getEmail(),
                usuario.getDataCriacao()
        );
    }

    private Usuario toEntity(UsuarioDTO dto) {
        Usuario usuario = new Usuario();
        usuario.setNome(dto.getNome());
        usuario.setEmail(dto.getEmail());
        return usuario;
    }
}
