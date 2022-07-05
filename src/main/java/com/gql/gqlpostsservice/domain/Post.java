package com.gql.gqlpostsservice.domain;

import lombok.Builder;
import lombok.Value;

@Builder
@Value
public class Post {
    Integer id;
    Integer userId;
    String title;
    String body;
}
