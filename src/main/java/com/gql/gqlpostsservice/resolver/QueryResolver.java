package com.gql.gqlpostsservice.resolver;

import com.gql.gqlpostsservice.domain.Post;
import com.gql.gqlpostsservice.domain.PostFilter;
import graphql.kickstart.tools.GraphQLQueryResolver;
import kong.unirest.GenericType;
import kong.unirest.Unirest;
import org.springframework.stereotype.Component;

import java.util.List;

import static com.gql.gqlpostsservice.util.Constants.POSTS_ENDPOINT;

@Component
public class QueryResolver implements GraphQLQueryResolver {
    public List<Post> posts(PostFilter where) {
        return Unirest.get(POSTS_ENDPOINT).queryString("userId", where.getUserId()).asObject(new GenericType<List<Post>>() {}).getBody();
    }
}
