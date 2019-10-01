package kz.teamvictus.store.core.service;

import kz.teamvictus.store.core.model.User;
import kz.teamvictus.store.core.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {
    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    @Autowired
    private UserRepository userRepository;

    public List<User> getAll(){
        logger.debug("inside getAll() method");
        return userRepository.findAll();
    }
}
