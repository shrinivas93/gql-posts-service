package com.gql.gqlpostsservice;

import com.apollographql.federation.graphqljava.Federation;
import graphql.kickstart.tools.SchemaParser;
import graphql.schema.GraphQLSchema;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GraphQLConfig {

    @Bean
    public GraphQLSchema customSchema(SchemaParser schemaParser){
        return Federation.transform(schemaParser.makeExecutableSchema()).build();
    }
}