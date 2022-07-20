package com.gql.gqlpostsservice.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class User {
    Integer id;
    List<Post> posts;
    public List<Post> getPosts(String body) {
        return posts;
    }
}
