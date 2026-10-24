package ec.fin.be.sigered.seguridad;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

/**
 *
 * @author pvillarruel
 */
@Service
@RequiredArgsConstructor
public class DetallesUsuarioService implements UserDetailsService {

    @Override
    public UserDetails loadUserByUsername(String cedula)  {

        return new DetalleUsuario(cedula);
    }
}
