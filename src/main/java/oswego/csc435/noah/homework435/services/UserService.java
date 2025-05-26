package oswego.csc435.noah.homework435.services;

import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import oswego.csc435.noah.homework435.models.User;
import oswego.csc435.noah.homework435.repositories.UserRepository;

import java.util.Optional;

@Service
public class UserService {
    
    private final UserRepository userRepository;
    
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
    
    public User getOrCreateUser(OAuth2User oauth2User) {
        String sub = oauth2User.getAttribute("sub");
        String name = oauth2User.getAttribute("name");
        
        Optional<User> existingUser = userRepository.findBySub(sub);
        
        if (existingUser.isPresent()) {
            return existingUser.get();
        } else {
            User newUser = new User(sub, name);
            return userRepository.save(newUser);
        }
    }
    
    public Optional<User> getUserById(int id) {
        return userRepository.findById(id);
    }
    
    public Optional<User> getUserBySub(String sub) {
        return userRepository.findBySub(sub);
    }
}