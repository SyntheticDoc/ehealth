package ehealth.group1.backend.service;


import ehealth.group1.backend.entity.User;
import ehealth.group1.backend.entity.UserUpdate;
import ehealth.group1.backend.exception.UserAlreadyExistsException;
import ehealth.group1.backend.exception.UserNotFoundException;
import ehealth.group1.backend.exception.UserPasswordMismatchException;
import ehealth.group1.backend.repositories.UserRepository;
import jakarta.persistence.PersistenceException;
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
        User result;

        try {
            result = userRepository.findByNameAndPassword(name, password);
        } catch (Error e){
            throw new PersistenceException("Error in UserService.getUser()", e);
        }

        if(result == null) {
            throw new UserNotFoundException("User " + name + " was not found in the database.");
        } else {
            return result;
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

    public User updateUser(UserUpdate user) throws UserPasswordMismatchException {
        LOGGER.info("Updating user: " + user.toString());

        User userToUpdate = userRepository.findByNameAndPassword(user.getOldName(), user.getOldPassword());

        if(userToUpdate == null) {
            throw new UserNotFoundException("Can't update user \"" + user.getOldName() + "\", user not found in database!");
        } else if(!userToUpdate.getPassword().matches(user.getOldPassword())) {
            // Todo: Match passwords with Argon2
            throw new UserPasswordMismatchException("Can't update user \"" + user.getOldName() + "\", passwords are not matching!");
        } else {
            /*
                To save the user, we have to user the userToUpdate freshly fetched from the database. Reason: A user received
                from the Endpoint is marked as Transient in JPA and is thus saved as a new user every time userRepository.save()
                is called. Only a user fetched directly from the repository is marked as Persistent and has an open transaction
                attached in JPAs TransactionManager, which is the reason that only this way the user entry in the database
                is really updated instead of a new entry created.

                - SyntheticDoc
             */
            userToUpdate.setName(user.getName());
            userToUpdate.setPassword(user.getPassword());
            userToUpdate.setAddress(user.getAddress());
            userToUpdate.setPhone(user.getPhone());
            userToUpdate.setEmergency(user.isEmergency());
            return saveUserToRepo(userToUpdate);
        }
    }

    private User saveUserToRepo(User u) {
        u.setPassword(hashUserPassword(u.getPassword()));
        return userRepository.save(u);
    }

    private String hashUserPassword(String password) {
        // Todo: Hash user password with Argon2
        return password;
    }
}
