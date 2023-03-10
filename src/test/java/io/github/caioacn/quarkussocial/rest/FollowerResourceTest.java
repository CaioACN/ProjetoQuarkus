package io.github.caioacn.quarkussocial.rest;

import io.github.caioacn.quarkussocial.domain.model.Follower;
import io.github.caioacn.quarkussocial.domain.model.User;
import io.github.caioacn.quarkussocial.repository.FollowerRepository;
import io.github.caioacn.quarkussocial.repository.UserRepository;
import io.github.caioacn.quarkussocial.rest.dto.FollowerRequest;
import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.*;

import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.core.Response;

import static io.restassured.RestAssured.given;
import static io.restassured.RestAssured.when;
import static org.junit.jupiter.api.Assertions.*;
@QuarkusTest
@TestHTTPEndpoint(FollowerResource.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)


class FollowerResourceTest {
    @Inject
    UserRepository userRepository;
    @Inject
    FollowerRepository followerRepository;

    Long userId;
    Long followerId;
        @BeforeEach
        @Transactional
    void setUp() {
            //Usuário padrão dos testes
            var user = new User();
            user.setAge(30);
            user.setName("Fulano");
            userRepository.persist(user);
            userId = user.getId();
            //O seguidor
            var follower = new User();
            follower.setAge(31);
            follower.setName("Cicrano");
            userRepository.persist(follower);
            followerId = follower.getId();
            //Criar um follower
            var followerEntity = new Follower();
            followerEntity.setFollower(follower);
            followerEntity.setUser(user);
            followerRepository.persist(followerEntity);


    }

    @Test

    public void sameUserAsFollowerTest(){
            var body = new FollowerRequest();
            body.setFollowerId(userId);
            given()
                    .contentType(ContentType.JSON)
                    .body(body)
                    .pathParams("userId",userId)
        .when()
                .put()
                .then()
                .statusCode(Response.Status.CONFLICT.getStatusCode())
                .body(Matchers.is("You can't follow yourself"));


    }
    @Test
    @DisplayName("should return 404 on follow a user when User id doen't exist")
    public void userNotFoundWhenTryingToFollowTest(){

            var body = new FollowerRequest();
            body.setFollowerId(userId);
            var inexistentUserId = 999;
        given()
                .contentType(ContentType.JSON)
                .body(body)
                .pathParams("userId",inexistentUserId)
        .when()
                .put()
        .then()
                .statusCode(Response.Status.NOT_FOUND.getStatusCode());

    }
    @Test
    @DisplayName("should follow a user")

    public void followUserTest(){

        var body = new FollowerRequest();
        body.setFollowerId(followerId);

        given()
                .contentType(ContentType.JSON)
                .body(body)
                .pathParam("userId",userId)
        .when()
                .put()
        .then()
                .statusCode(Response.Status.NO_CONTENT.getStatusCode());

    }
    @Test
    @DisplayName("should return 404 on list user followers and User id doen't exist")
     public void userNotFoundWhenListFollowersTest(){
        var inexistentUserId = 999;

        given()
            .contentType(ContentType.JSON)
            .pathParams("userId",inexistentUserId)
        .when()
            .get()
        .then()
            .statusCode(Response.Status.NOT_FOUND.getStatusCode());

    }
    @Test
    @DisplayName("should list a user's followers")
    public void listFollowersTest(){
            var response =
              given()
                .contentType(ContentType.JSON)
                .pathParams("userId",userId)
                .when()
                .get()
                .then()
                .extract().response();

           var followersCount = response.jsonPath().get("followersCount");
           var followersContent = response.jsonPath().getList("content");
        assertEquals(Response.Status.OK.getStatusCode(),response.statusCode());
    assertEquals(1,followersCount);
       assertEquals(1,followersContent.size());
        }
    @Test
    @DisplayName("should return 404 on unfollow user and User id doen't exist")
    public void userNotFoundWhenUnfollowingAUserTest(){
        var inexistentUserId = 999;

        given()
                .pathParams("userId",inexistentUserId)
                .queryParam("followerId",followerId)
                .when()
                .delete()
                .then()
                .statusCode(Response.Status.NOT_FOUND.getStatusCode());

    }
    @Test
    @DisplayName("should Unfolower an user")
    public void unfollowingAUserTest(){
             given()
                .pathParams("userId",userId)
                .queryParam("followerId",followerId)
             .when()
                .delete()
             .then()
                .statusCode(Response.Status.NO_CONTENT.getStatusCode());

    }
}