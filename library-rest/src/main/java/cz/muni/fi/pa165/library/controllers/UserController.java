package cz.muni.fi.pa165.library.controllers;

import cz.muni.fi.pa165.library.entities.User;
import cz.muni.fi.pa165.library.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author Katarína Hermanová
 * UČO 433511
 * Github katHermanova
 */
/*@RestController
@Transactional*/
@RestController
@CrossOrigin(origins = "http://localhost:4200")
public class UserController /*extends AbstractController for now*/{

    @Autowired
    private UserService userService;

    /*public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping(value = "/users", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<User> findAllUsers() {
        return userService.findAll();
    }*/

    @GetMapping("/users")
    public List<User> getUsers() {
        return userService.findAll();
    }
}
