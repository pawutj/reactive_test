package com.example.demo.controller;

import com.example.demo.exception.NotFoundException;
import com.example.demo.model.IUser;
import com.example.demo.model.UserEntity;
import com.example.demo.model.UserList;
import com.example.demo.model.UserWithKey;
import com.example.demo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.r2dbc.core.R2dbcEntityOperations;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final R2dbcEntityOperations operations;

    private final UserRepository userRepository;

    @GetMapping
    public Flux<UserEntity> findAll() {
        return userRepository.findAll();
    }

    public <T extends IUser> List<IUser> getIUser(List<T> users){
        List<IUser> iusers = new ArrayList<>();
        for(int i=0;i<users.size();i++){
            iusers.add(users.get(i));
        }
        return iusers;
    }

    @GetMapping("/s4")
    public Mono<Integer> s4(){
        return Mono.just(Integer.valueOf(4)).map(i -> i+1);
    }

    @GetMapping("/s6")
    public Mono<UserList> s6(){
        Mono<UserWithKey> userListMono1 = userRepository.findAll().collectList().map( u -> {
            return UserWithKey.builder().values(getIUser(u)).key("Mono1").build();
        });
        Mono<UserWithKey> userListMono2 = userRepository.findAll().collectList().map( u -> {
            return UserWithKey.builder().values(getIUser(u)).key("Mono2").build();
        });

        Mono<UserWithKey> userListMono3 = userRepository.findAll().collectList().map( u -> {
            return UserWithKey.builder().values(getIUser(u)).key("Mono3").build();
        });

        return userListMono1
                .concatWith(userListMono2)
                .concatWith(userListMono3)
                .collectList()
                .map(userEntities -> {
                    UserList userList = UserList.builder().build();
                    userList.setUserWithKeys(new ArrayList<>());
                    for(int i=0 ;i< userEntities.size(); i++) {
                        userList.getUserWithKeys().add(userEntities.get(i));
                    }
                    return userList;
                });

    }

    @GetMapping("/s5")
    public Mono<UserList> s5(){
        Mono<List<UserEntity>> userListMono1 = userRepository.findAll().collectList();
        Mono<List<UserEntity>> userListMono2 = userRepository.findAll().collectList();
        Mono<List<UserEntity>> userListMono3 = userRepository.findAll().collectList();

        return Mono.zip(userListMono1, userListMono2, userListMono3)
                .map(tuple3 -> {
                    UserList userList = UserList.builder().build();
                    userList.setUser1(tuple3.getT1().get(0)); // Set first user from userListMono1
                    userList.setUser2(tuple3.getT2().get(0)); // Set first user from userListMono2
                    userList.setUser3(tuple3.getT3().get(0)); // Set first user from userListMono3
                    return userList;
                });
    }

    @GetMapping("/s0")
    public Mono<UserList> s3() {
        Mono<List<UserEntity>> userListMono1 = userRepository.findAll().collectList();
        Mono<List<UserEntity>> userListMono2 = userRepository.findAll().collectList();
        Mono<List<UserEntity>> userListMono3 = userRepository.findAll().collectList();

        return userListMono1
                .concatWith(userListMono2)
                .concatWith(userListMono3)
                .collectList()
                .map(userEntities -> {
                    UserList userList = UserList.builder().build();
                    userList.setUsers(new ArrayList<>());
                    for(int i=0 ;i< userEntities.size(); i++) {
                        userList.getUsers().add(userEntities.get(i).get(0));
                    }
                    return userList;
                });

    }

    @GetMapping("/s1")
    public Flux<UserEntity> s1(){
        Flux<UserEntity> u1 = userRepository.findAll().map(this::setMockName);
        Flux<UserEntity> u2 = userRepository.findAll();
        Flux<UserEntity> u3 = userRepository.findAll();

        u1.doOnNext(userEntity -> {
            System.out.println("Id: " + userEntity.getId());
        }).doOnNext(userEntity -> {
            System.out.println("Id: " + userEntity.getId());
        }).subscribe();

        return u1;

    }

    public UserEntity setMockName(UserEntity e){
        e.setFirstName("Mock");
        return e;
    }

    @GetMapping("/s2")
    public Mono<List<UserEntity>> s2(){
        Mono<List<UserEntity>> s =  userRepository.findAll().collectList();
        return s;

    }



    @GetMapping("/{id}")
    public Mono<UserEntity> findById(@PathVariable("id") final UUID id) {
        return userRepository.findById(id)
                .switchIfEmpty(Mono.error(new NotFoundException("User id \"" + id.toString() + "\"not found")));
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public Mono<UserEntity> create(@RequestBody final UserEntity entity) {
        entity.setId(UUID.randomUUID());
        return operations.insert(UserEntity.class)
                .using(entity)
                .then()
                .thenReturn(entity);
    }

    @PutMapping("/{id}")
    public Mono<UserEntity> update(@PathVariable("id") final UUID id, @RequestBody final UserEntity entity) {
        return findById(id)
                .flatMap(dbEntity -> {
                    dbEntity.setFirstName(entity.getFirstName());
                    dbEntity.setLastName(entity.getLastName());
                    return userRepository.save(dbEntity);
                });
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{id}")
    public Mono<Void> deleteById(@PathVariable("id") final UUID id) {
        return findById(id)
                .flatMap(dbEntity -> {
                    return userRepository.deleteById(dbEntity.getId());
                });
    }
}