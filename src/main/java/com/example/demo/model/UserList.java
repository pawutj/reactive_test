package com.example.demo.model;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Builder
@Getter
@Setter
public class UserList {
    public List<IUser> users;
    public IUser user1;
    public IUser user2;
    public IUser user3;

//    public UserList() {
//        this.users  = new ArrayList<>();
//    }
//
//    public UserList(List<IUser> users) {
//        this.users = users;
//    }

}
