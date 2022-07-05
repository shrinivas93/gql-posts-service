package com.gql.gqlpostsservice.domain;

import lombok.*;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class PostFilter {
    Integer userId;
}
