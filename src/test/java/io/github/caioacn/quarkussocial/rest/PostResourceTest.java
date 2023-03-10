package io.github.caioacn.quarkussocial.rest;

import io.github.caioacn.quarkussocial.domain.model.Follower;
import io.github.caioacn.quarkussocial.domain.model.Post;
import io.github.caioacn.quarkussocial.domain.model.User;
import io.github.caioacn.quarkussocial.repository.FollowerRepository;
import io.github.caioacn.quarkussocial.repository.PostRepository;
import io.github.caioacn.quarkussocial.repository.UserRepository;
import io.github.caioacn.quarkussocial.rest.dto.CreatePostRequest;
import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;
import javax.transaction.Transactional;
import java.net.CacheRequest;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.*;
@QuarkusTest
@TestHTTPEndpoint(PostResource.class)
class PostResourceTest {

    @Inject
    UserRepository userRepository;
    @Inject
    FollowerRepository followerRepository;
    @Inject
    PostRepository postRepository;

    Long userId;

    Long userNotFollowerId;

    Long userFollowerId;
    @BeforeEach
    @Transactional
    public void setUP(){
        //Usuário padrão dos testes
        var user = new User();
        user.setAge(30);
        user.setName("FulanoTeste");
        userRepository.persist(user);
        userId = user.getId();
        // Criada a postagem para o usuário
        Post post = new Post();
        post.setText("Hello");
        post.setUser(user);
        postRepository.persist(post);


        //Usuário que não segue ninguém
        var userNotFollower= new User();
        userNotFollower.setAge(33);
        userNotFollower.setName("Cicrano");
        userRepository.persist((userNotFollower));
        userNotFollowerId = userNotFollower.getId();
        //Usuário seguidor
        var userFollower= new User();
        userFollower.setAge(31);
        userFollower.setName("Terceiro");
        userRepository.persist((userFollower));
        userFollowerId = userFollower.getId();

        Follower follower = new Follower();
        follower.setUser(user);
        follower.setFollower(userFollower);
        followerRepository.persist(follower);

    }

    @Test
    @DisplayName("should create a post for a user")
public void createdPostTest(){
        var postRequest = new CreatePostRequest();
        postRequest.setText("Some text");



        given()
            .contentType(ContentType.JSON)
            .body(postRequest)
            .pathParams("userId",userId)
        .when()
            .post()
        .then()
             .statusCode(201);
}
    @Test
    @DisplayName("should return 404 when trying to make a post for an inexistent user")
    public void postForAnInexistentUserTest(){
        var postRequest = new CreatePostRequest();
        postRequest.setText("Some text");

        var inexistentUserID=9999;

        given()
                .contentType(ContentType.JSON)
                .body(postRequest)
                .pathParams("userId",inexistentUserID)
            .when()
                .post()
            .then()
                .statusCode(404);
    }
    @Test
    @DisplayName("should return 404 when user doesn't exist")
    public void ListPostUserNotFoundTest(){

        var inexistentUserId = 999;

        given()
                .pathParams("userId",inexistentUserId)
        .when()
                .get()
        .then()
                .statusCode(404);
    }
    @Test
    @DisplayName("should return 400 when followerId header is not present")
    public void ListPostFollowerHeaderNotSendTest(){



        given()
            .pathParams("userId",userId)
        .when()
             .get()
        .then()
              .statusCode(400)
                .body(Matchers.is("You forgot the header followerId"));

    }
    @Test
    @DisplayName("should return 400 when follower doesn't exist")
    public void ListPostFollowerNotFoundTest(){
        var inexistentFollowerId = 999;
        given()
          .pathParams("userId",userId)
        .header("followerId",inexistentFollowerId)
          .when()
        .get()
          .then()
        .statusCode(400)
         .body(Matchers.is("Inexistent followerId"));


    }
    @Test
    @DisplayName("should return 403 when follower isn't a follower")
    public void ListPostNotAFollower(){
       given()
           .pathParams("userId",userId)
           .header("followerId",userNotFollowerId)
       .when()
           .get()
       .then()
            .statusCode(403)
            .body(Matchers.is("You can't see these posts"));


    }
    @Test
    @DisplayName("should return posts")
    public void ListPostsTest(){
        given()
           .pathParams("userId",userId)
               .header("followerId",userFollowerId)
            .when()
                .get()
            .then()
               .statusCode(200)
               .body("size()",Matchers.is(1));


    }
}