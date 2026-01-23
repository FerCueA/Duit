package es.duit.app.service;

import es.duit.app.entity.AppUser;
import es.duit.app.repository.AppUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
    @Autowired
    private AppUserRepository appUserRepository;

    public AppUser validateUser(String username, String password) {
        return appUserRepository.findByUsername(username)
                .filter(user -> user.getPassword().equals(password))
                .orElse(null);
    }
}
