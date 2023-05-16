package ehealth.group1.backend.service;


import ehealth.group1.backend.entity.User;
import ehealth.group1.backend.exception.UserAlreadyExistsException;
import ehealth.group1.backend.exception.UserPasswordMismatchException;
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

    public User getUser(String name, String password) {
        try {
            return userRepository.findByNameAndPassword(name, password);
        }catch (Error e){
            return null;
        }
    }

    public User postUser(User user) throws UserAlreadyExistsException {
        LOGGER.info("Saving user: " + user.toString());

        User user2 = userRepository.findByNameAndPassword(user.getName(), user.getPassword());

        if(user2 != null) {
            throw new UserAlreadyExistsException("A user with the name \"" + user.getName() + "\" is already existing!");
        } else {
            return userRepository.save(user);
        }
    }

    public User updateUser(User user) throws UserPasswordMismatchException {
        LOGGER.info("Updating user: " + user.toString());

        User userToUpdate = userRepository.findByNameAndPassword(user.getName(), user.getPassword());

        if(!userToUpdate.getPassword().matches(user.getPassword())) {
            throw new UserPasswordMismatchException("Can't update user \"" + user.getName() + "\", passwords are not matching!");
        } else {
            return userRepository.save(user);
        }
    }
}
