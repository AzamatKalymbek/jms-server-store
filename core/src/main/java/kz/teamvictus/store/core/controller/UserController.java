package kz.teamvictus.store.core.controller;

import io.swagger.annotations.*;
import kz.teamvictus.store.core.model.User;
import kz.teamvictus.store.core.repository.UserRepository;
import kz.teamvictus.store.core.service.UserService;
import kz.teamvictus.store.core.util.exception.ResourceNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1")
@Api(tags = {"User"}, description = "C.R.U.D. operations for User", authorizations = {@Authorization(value = "bearerAuth")})
public class UserController {
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserService userService;

    @GetMapping("/users")
    @Produces("application/json")
    @ApiOperation(value = "Fetch All Users", tags = {"User"})
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully retrieved list"),
            @ApiResponse(code = 401, message = "You are not authorized to view the resource"),
            @ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden"),
            @ApiResponse(code = 404, message = "The resource you were trying to reach is not found")
    })
    public List<User> getAllUsers() {
        logger.debug("inside UserController.getAllUsers() method");
        logger.debug("====================================================================");
        return userService.getAll();
    }

    @ApiOperation(value = "Get an user by Id")
    @GetMapping("/users/{id}")
    @Produces("application/json")
    public ResponseEntity<User> getUserById( @PathVariable(value = "id") Long userId) throws ResourceNotFoundException {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found on :: " + userId));
        return ResponseEntity.ok().body(user);
    }

    @ApiOperation(value = "Add an user")
    @PostMapping("/users")
    @Produces("application/json")
    @Consumes("application/json")
    public User createUser(@Valid @RequestBody User user) {
        return userRepository.save(user);
    }

    @ApiOperation(value = "Update an user")
    @PutMapping("/users/{id}")
    @Consumes("application/json")
    public ResponseEntity<User> updateUser( @PathVariable(value = "id") Long userId,
                                            @Valid @RequestBody User userDetails) throws ResourceNotFoundException {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found on :: " + userId));

        user.setEmail(userDetails.getEmail());
        user.setLastName(userDetails.getLastName());
        user.setFirstName(userDetails.getFirstName());
        final User updatedUser = userRepository.save(user);
        return ResponseEntity.ok(updatedUser);
    }

    @ApiOperation(value = "Delete an user")
    @DeleteMapping("/user/{id}")
    public Map<String, Boolean> deleteUser(@PathVariable(value = "id") Long userId) throws Exception {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found on :: " + userId));

        userRepository.delete(user);
        Map<String, Boolean> response = new HashMap<>();
        response.put("deleted", Boolean.TRUE);
        return response;
    }
}
