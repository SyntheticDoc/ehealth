package ehealth.group1.backend.service;


import ehealth.group1.backend.entity.User;
import ehealth.group1.backend.repositories.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.lang.invoke.MethodHandles;

@Component
public class UserService {
    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User getUser(Long id ) {
        try {
            return userRepository.getReferenceById(id);
        }catch (Error e){
            return null;
        }
    }

    public User postUser(User user){
        try{
            LOGGER.info("Saving user: " + user.toString());
            return userRepository.save(user);
        }catch(Error e){
            return null;
        }
    }

}
