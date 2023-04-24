package ehealth.group1.backend.service;


import ehealth.group1.backend.entity.User;
import ehealth.group1.backend.helper.ErrorHandler;
import ehealth.group1.backend.persistence.UserDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestParam;

import java.lang.invoke.MethodHandles;
import java.util.List;

@Component
public class UserService {
    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    UserDao userDao;

    public UserService(UserDao userDao) {
        this.userDao = userDao;
    }

    public List<User> getUser(Long id ){
        try {
            List<User> userList = userDao.getOneById(id);
            return userList;
        }catch (Error e){
            return null;
        }

    }
    public User postUser( String name,  String address,  Long phone,boolean emergency, String password){
        try{
            User newUser = new User(null,name,address,phone,emergency,password);
           userDao.createUser(newUser);
           List<User> userList= userDao.searchUser(newUser);
           return userList.get(0);
        }catch(Error e){
            return null;
        }
    }

}
