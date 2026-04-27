package mx.unison.service;

import mx.unison.DatabaseManager;
import mx.unison.Usuario;

import java.sql.SQLException;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Servicio de Usuarios.
 * Encapsula la consulta de usuarios vía ORMLite.
 * Solo el rol ADMIN debe llegar a invocar este servicio
 * (la restricción la aplica el controlador según AuthService).
 */
public class UsuarioService {

    private final DatabaseManager dbManager;

    public UsuarioService(DatabaseManager dbManager) {
        this.dbManager = dbManager;
    }

    /** Devuelve todos los usuarios registrados. */
    public List<Usuario> listarTodos() {
        try {
            return dbManager.getUsuarioDao().queryForAll();
        } catch (SQLException e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

    /**
     * Filtra usuarios por nombre o rol (case-insensitive).
     * Si el filtro está vacío devuelve todos.
     */
    public List<Usuario> buscar(String filtro) {
        if (filtro == null || filtro.isBlank()) {
            return listarTodos();
        }
        String f = filtro.trim().toLowerCase();
        return listarTodos().stream()
                .filter(u -> (u.nombre != null && u.nombre.toLowerCase().contains(f))
                          || (u.rol    != null && u.rol.toLowerCase().contains(f)))
                .collect(Collectors.toList());
    }
}
