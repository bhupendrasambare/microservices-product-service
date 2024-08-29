/**
 * author @bhupendrasambare
 * Date   :27/06/24
 * Time   :5:01 pm
 * Project:microservices-registry
 **/
package com.service.product.controller;

import com.service.product.config.Constants;
import com.service.product.config.UserService;
import com.service.product.dto.request.LoginRequest;
import com.service.product.dto.response.Response;
import com.service.product.dto.Status;
import com.service.product.dto.response.UserDto;
import com.service.product.kafka.KafkaNotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/product")
@Tag(name = "Product Controller", description = "APIs for Product Services")
public class authController {

    @Autowired
    UserService userService;

    @Autowired
    KafkaNotificationService kafkaNotificationService;

    @PostMapping("/register")
    @Operation(summary = "Registration", description = "User registration with unique email")
    public ResponseEntity<Response> addUser(@RequestBody UserDto users){
        return userService.createUser(users);
    }

    @PostMapping("/login")
    @Operation(summary = "Login", description = "Product user and return a token")
    public ResponseEntity<Response> login(@Valid @RequestBody LoginRequest users) {
        ResponseEntity<Response> res;
        String token = userService.generateToken(users.getEmail(), users.getPassword());
        res = ResponseEntity.status(HttpStatus.OK).body(new Response(Constants.SUCCESS_CODE,"", Status.SUCCESS,token));
        kafkaNotificationService.sendMessage(users);
        return res;
    }

    @PostMapping("/validate")
    @Operation(summary = "Validate token", description = "Validate your token")
    public ResponseEntity<Response> validateToken(@RequestBody String token) {
        ResponseEntity<Response> res;
        try {
            userService.validateToken(token);
            res = ResponseEntity.status(HttpStatus.OK).body(new Response(Constants.SUCCESS_CODE,"", Status.SUCCESS,null));
        } catch (UsernameNotFoundException e) {
            res = ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new Response(Constants.INVALID_TOKEN_CODE,Constants.INVALID_TOKEN, Status.FAILED,null));
        } catch (Exception e) {
            res = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new Response(Constants.INVALID_TOKEN_CODE,Constants.INVALID_TOKEN, Status.FAILED,null));
        }
        return res;
    }

}
