package dev.andie.userapi.service;

import dev.andie.userapi.dto.UsuarioDTO;
import org.springframework.data.domain.*;

public interface UsuarioService {

    Page<UsuarioDTO> listarTodos(Pageable pageable);

    UsuarioDTO buscarPorId(Long id);

    UsuarioDTO criar(UsuarioDTO dto);

    UsuarioDTO atualizar(Long id, UsuarioDTO dto);

    void deletar(Long id);
}
