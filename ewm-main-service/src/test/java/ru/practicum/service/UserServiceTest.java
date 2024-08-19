package ru.practicum.service;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.model.User;
import ru.practicum.service.interfaces.UserService;

import java.util.Comparator;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.notNullValue;

@Transactional
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class UserServiceTest {

    private final UserService userService;
    private final EntityManager em;

    @Test
    public void createUserTest() {
        // Create User
        User user = createUser("User","user@email.com");
        // Save User to DB
        userService.createUser(user);
        // Get User from DB
        List<User> users = getUsersFromDB();
        // Check results
        assertThat(users.size(), equalTo(1));
        assertThat(users.get(0).getId(), notNullValue());
        assertThat(users.get(0).getName(), equalTo(user.getName()));
        assertThat(users.get(0).getEmail(), equalTo(user.getEmail()));
    }

    @Test
    public void getAndRemoveUsersTest() {
        // Create users
        User user1 = createUser("User1", "user1@email.com");
        User user2 = createUser("User2", "user2@email.com");
        User user3 = createUser("User3", "user3@email.com");
        // Add users to DB
        insertUsers(List.of(user1, user2, user3));
        // Get users from DB
        List<User> users = userService.getUsers(null, 0, 10);
        // Check results
        users = users.stream().sorted(Comparator.comparingInt(User::getId)).toList();
        assertThat(users.size(), equalTo(3));
        assertThat(users.get(0).getId(), notNullValue());
        assertThat(users.get(0).getName(), equalTo(user1.getName()));
        assertThat(users.get(0).getEmail(), equalTo(user1.getEmail()));
        assertThat(users.get(1).getId(), notNullValue());
        assertThat(users.get(1).getName(), equalTo(user2.getName()));
        assertThat(users.get(1).getEmail(), equalTo(user2.getEmail()));
        assertThat(users.get(2).getId(), notNullValue());
        assertThat(users.get(2).getName(), equalTo(user3.getName()));
        assertThat(users.get(2).getEmail(), equalTo(user3.getEmail()));

        // Remove user from DB
        userService.removeUser(users.get(0).getId());
        // Get users from DB
        List<User> newUsers = userService.getUsers(null, 0, 10);
        // Check results
        assertThat(newUsers.size(), equalTo(2));
        assertThat(newUsers, not(contains(hasProperty("id", is(users.get(0).getId())))));
    }

    private User createUser(String name, String email) {
        User user = new User();
        user.setId(null);
        user.setName(name);
        user.setEmail(email);
        return user;
    }

    private List<User> getUsersFromDB() {
        return em.createQuery("SELECT u FROM User u", User.class).getResultList();
    }

    private void insertUsers(List<User> list) {
        list.forEach(em::persist);
    }
}
