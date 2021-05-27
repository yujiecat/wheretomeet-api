package com.wheretomeet.controller;

import java.util.Optional;

import com.wheretomeet.model.FriendsList;
import com.wheretomeet.model.User;
import com.wheretomeet.repository.FriendsListRepository;
import com.wheretomeet.repository.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class FriendsListController {
    @Autowired
    FriendsListRepository friendsRepo;

    @Autowired
    UserRepository userRepo;

    @GetMapping("/friends/{userId}")
	public ResponseEntity<?> getUsersFriendsList(@PathVariable("userId") String userId) {
		Optional<FriendsList> friendsList = friendsRepo.findById(userId);
		if(friendsList.isPresent()) {
			return ResponseEntity.ok().body(friendsList.get());
		}
		return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
    
    @PutMapping("/friends/{userId}/add/{friendId}")
    public ResponseEntity<?> addFriendToList(@PathVariable("userId") String userId, @PathVariable("friendId") String friendId) {

        FriendsList friendsList = friendsRepo.findById(userId).orElse(null);
        if(friendsList != null) {
            User friend = userRepo.findById(friendId).orElse(null);
            if(friend != null) {
                friendsList.addFriend(friend);
                friendsRepo.save(friendsList);
                return new ResponseEntity<>(friendsList, HttpStatus.OK);
            }
        }

        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @PutMapping("/friends/{userId}/remove/{friendId}")
    public ResponseEntity<?> removeFriendFromList(@PathVariable("userId") String userId, @PathVariable("friendId") String friendId) {

        FriendsList friendsList = friendsRepo.findById(userId).orElse(null);

        if(friendsList != null) {
            User friend = userRepo.findById(friendId).orElse(null);
            if(friend != null) {
                friendsList.removeFriend(friend);
                friendsRepo.save(friendsList);
                return new ResponseEntity<>(friendsList, HttpStatus.OK);
            }
        }

        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @DeleteMapping("/friends/destroy/{userId}")
    public ResponseEntity<?> destroyFriendsList(@PathVariable("userId") String userId) {

        FriendsList friendsList = friendsRepo.findById(userId).orElse(null);

        if(friendsList != null) {
            friendsRepo.delete(friendsList);
            return new ResponseEntity<>(userId + "'s friends list deleted", HttpStatus.OK);
        }

        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

}