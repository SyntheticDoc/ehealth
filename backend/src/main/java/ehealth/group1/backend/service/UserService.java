package ehealth.group1.backend.service;


import ehealth.group1.backend.entity.Argon2Parameters;
import ehealth.group1.backend.entity.SecurityData;
import ehealth.group1.backend.entity.User;
import ehealth.group1.backend.entity.UserUpdate;
import ehealth.group1.backend.exception.UserAlreadyExistsException;
import ehealth.group1.backend.exception.UserNotFoundException;
import ehealth.group1.backend.exception.UserPasswordMismatchException;
import ehealth.group1.backend.helper.ErrorHandler;
import ehealth.group1.backend.helper.argon2crypto.Argon2ParameterChecker;
import ehealth.group1.backend.helper.argon2crypto.Argon2PasswordEncoderWithParams;
import ehealth.group1.backend.repositories.Argon2ParametersRepository;
import ehealth.group1.backend.repositories.SecurityDataRepository;
import ehealth.group1.backend.repositories.UserRepository;
import jakarta.persistence.PersistenceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.lang.invoke.MethodHandles;

@Component
public class UserService {
    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    ErrorHandler errorHandler;
    UserRepository userRepository;
    SecurityDataRepository securityDataRepository;
    Argon2ParametersRepository parametersRepository;
    Argon2PasswordEncoderWithParams argon2PasswordEncoderWithParams;

    private String pwdSalt;
    private Argon2Parameters argon2Parameters;

    public UserService(UserRepository userRepository, SecurityDataRepository securityDataRepository, Argon2ParametersRepository parametersRepository,
                       Argon2PasswordEncoderWithParams argon2PasswordEncoderWithParams, ErrorHandler errorHandler) {
        this.userRepository = userRepository;
        this.securityDataRepository = securityDataRepository;
        this.errorHandler = errorHandler;
        this.parametersRepository = parametersRepository;
        this.argon2PasswordEncoderWithParams = argon2PasswordEncoderWithParams;

        SecurityData saltContainer = securityDataRepository.findByType("saltDefault");

        if(saltContainer != null) {
            pwdSalt = saltContainer.getVal();
        } else {
            pwdSalt = null;
            errorHandler.handleCustomException("UserService constructor", "Can't load salt because saltContainer was null", new PersistenceException());
        }

        argon2Parameters = parametersRepository.findByType(Argon2ParameterChecker.getParamNameSlow());

        if(argon2Parameters == null) {
            errorHandler.handleCustomException("UserService constructor", "Can't load cryptographic parameters", new PersistenceException());
        }
    }

    public User getUser(String name, String password) {
        User result;

        try {
            result = userRepository.findByNameAndPassword(name, hashUserPassword(password));
        } catch (Error e){
            throw new PersistenceException("Error in UserService.getUser()", e);
        }

        if(result == null) {
            throw new UserNotFoundException("User " + name + " was not found in the database.");
        } else {
            result.setPassword("");
            return result;
        }
    }

    public User postUser(User user) throws UserAlreadyExistsException {
        LOGGER.info("Saving user: " + user.toString());

        User user2 = userRepository.findByNameAndPassword(user.getName(), hashUserPassword(user.getPassword()));

        if(user2 != null) {
            throw new UserAlreadyExistsException("A user with the name \"" + user.getName() + "\" is already existing!");
        } else {
            user.setPassword("");
            return userRepository.save(user);
        }
    }

    public User updateUser(UserUpdate user) throws UserPasswordMismatchException {
        LOGGER.info("Updating user: " + user.toString());

        User userToUpdate = userRepository.findByNameAndPassword(user.getOldName(), hashUserPassword(user.getOldPassword()));

        if(userToUpdate == null) {
            throw new UserNotFoundException("Can't update user \"" + user.getOldName() + "\", user not found in database!");
        } else if(!doPasswordsMatch(user.getOldPassword(), userToUpdate.getPassword())) {
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
            userToUpdate.setPassword(hashUserPassword(user.getPassword()));
            userToUpdate.setAddress(user.getAddress());
            userToUpdate.setPhone(user.getPhone());
            userToUpdate.setEmergency(user.isEmergency());
            return saveUserToRepo(userToUpdate);
        }
    }

    private User saveUserToRepo(User u) {
        u.setPassword(hashUserPassword(u.getPassword()));
        User result = userRepository.save(u);
        result.setPassword("");
        return result;
    }

    public String hashUserPassword(String password) {
        return argon2PasswordEncoderWithParams.encode(password, argon2Parameters, pwdSalt);
    }

    public boolean doPasswordsMatch(String rawPassword, String encodedPassword) {
        return argon2PasswordEncoderWithParams.matches(rawPassword, encodedPassword);
    }
}
