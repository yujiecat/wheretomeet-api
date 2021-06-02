package com.wheretomeet.controller;

import java.util.Optional;

import com.wheretomeet.model.Group;
import com.wheretomeet.model.User;
import com.wheretomeet.repository.FriendsListRepository;
import com.wheretomeet.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
public class UserControllerTests {
    
    @Autowired
    private MockMvc mvc;

    @MockBean
    FriendsListRepository friendsRepo;

    @MockBean
    UserRepository userRepo;

    @Test
    void testGetOneUser() throws Exception { 
        User user = new User("Ayy", "123");
        user.setUserId("Ayy#1234");

        Mockito.when(userRepo.findById("Ayy#1234")).thenReturn(Optional.of(user));
 
        mvc.perform(get("/user/id/{id}", "Ayy#1234"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.userId", Matchers.is("Ayy#1234")))
            .andExpect(jsonPath("$.username", Matchers.is("Ayy")))
            .andExpect(jsonPath("$.password", Matchers.is("123")));
    }

    @Test
    void testGetUserViaEmail() throws Exception { 
        User user = new User("Ayy", "123");
        user.setUserId("Ayy#1234");
        user.setEmail("a@email.com");

        Mockito.when(userRepo.findByEmail("a@email.com")).thenReturn(Optional.of(user));
 
        mvc.perform(get("/user/email/{email}", "a@email.com"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.email", Matchers.is("a@email.com")))
            .andExpect(jsonPath("$.password", Matchers.is("123")));
    }

    @Test
    void testCreateUser() throws Exception {
        User user = new User("Ayy", "123");
        user.setUserId("Ayy#1234");
        ObjectMapper mapper = new ObjectMapper();
        String jsonUser = mapper.writeValueAsString(user);

        Mockito.when(userRepo.save(user)).thenReturn(user);

        mvc.perform(post("/users")
            .contentType(MediaType.APPLICATION_JSON)
            .content(jsonUser)
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", Matchers.is("User Ayy#1234 created")));
    }

    @Test
    void testDeleteUser() throws Exception {
        User user = new User("Ayy", "123");
        user.setUserId("Ayy#1234");
        Gson gson = new Gson();
        String jsonUser = gson.toJson(user);
        Mockito.doNothing().when(userRepo).deleteById("Ayy#1234");

        mvc.perform(delete("/user/id/{id}", "Ayy#1234")
            .contentType(MediaType.APPLICATION_JSON)
            .content(jsonUser)
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", Matchers.is("User Ayy#1234 deleted")));
    }

    @Test
    void testGetUsersGroups() throws Exception {
        User user = new User("Ayy", "123");
        user.setUserId("Ayy#1234");

        Group g1 = new Group("g1", "123", user);
        Group g2 = new Group("g2", "123", user);
        Group g3 = new Group("g3", "123", user);

        user.addGroup(g1);
        user.addGroup(g2);
        user.addGroup(g3);

        Mockito.when(userRepo.findById("Ayy#1234")).thenReturn(Optional.of(user));

        mvc.perform(get("/user/{id}/groups", "Ayy#1234"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", Matchers.hasSize(3)));
    }



}
