package io.github.caioacn.quarkussocial.rest;


import io.github.caioacn.quarkussocial.domain.model.Post;
import io.github.caioacn.quarkussocial.domain.model.User;
import io.github.caioacn.quarkussocial.repository.PostRepository;
import io.github.caioacn.quarkussocial.repository.UserRepository;
import io.github.caioacn.quarkussocial.rest.dto.CreatePostRequest;
import io.quarkus.hibernate.orm.panache.PanacheQuery;
import io.quarkus.panache.common.Sort;

import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.time.LocalDateTime;
import java.util.List;

@Path("/users/{userId}/posts")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class PostResource {
    private UserRepository userRepository;
    private PostRepository repository;

    @Inject
    public PostResource(UserRepository userRepository,
             PostRepository repository){

        this.userRepository = userRepository;
        this.repository = repository;
    }
    @POST
    @Transactional

    public Response savePost
            (@PathParam("userId") Long userId, CreatePostRequest request){

        User user = userRepository.findById(userId);
        if(user == null){
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        Post post = new Post();
        post.setText(request.getText());
        post.setUser(user);
        post.setDateTime(LocalDateTime.now());


        repository.persist(post);
        return Response.status(Response.Status.CREATED).build();

    }
    @GET
    public Response listPosts(@PathParam("userId") Long userId){
        User user = userRepository.findById(userId);
        if(user == null){
            return Response.status(Response.Status.NOT_FOUND).build();
        }

       PanacheQuery<Post> query = repository.find("user", Sort.by("dateTime",Sort.Direction.Descending),user);
        List<Post> list= query.list();



        return Response.ok(list).build();

    }
}
