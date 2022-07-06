package com.gql.gqlpostsservice;

import com.apollographql.federation.graphqljava.Federation;
import com.apollographql.federation.graphqljava._Entity;
import com.gql.gqlpostsservice.domain.Post;
import com.gql.gqlpostsservice.domain.PostFilter;
import com.gql.gqlpostsservice.resolver.QueryResolver;
import graphql.kickstart.tools.SchemaParser;
import graphql.schema.GraphQLSchema;
import graphql.schema.idl.SchemaPrinter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Configuration
public class GraphQLConfig {

    @Bean
    public GraphQLSchema customSchema(SchemaParser schemaParser, QueryResolver queryResolver) {
        return Federation.transform(schemaParser.makeExecutableSchema())
                .fetchEntities(env -> env.<List<Map<String, Object>>>getArgument(_Entity.argumentName)
                        .stream()
                        .map(values -> {
                            if ("Post".equals(values.get("__typename"))) {
                                final Object id = values.get("id");
                                if (id instanceof Integer) {
                                    return queryResolver.posts(new PostFilter((Integer) id));
                                }
                            }
                            return null;
                        })
                        .collect(Collectors.toList()))
                .resolveEntityType(env -> {
                    final Object src = env.getObject();
                    if (src instanceof Post) {
                        return env.getSchema().getObjectType("Post");
                    }
                    return null;
                }).build();
    }

    @Bean
    public SchemaPrinter customSchemaPrinter(){
        return new SchemaPrinter(SchemaPrinter.Options.defaultOptions().includeDirectives(true).includeDirectiveDefinitions(true).includeSchemaDefinition(true).includeIntrospectionTypes(true));
    }

}