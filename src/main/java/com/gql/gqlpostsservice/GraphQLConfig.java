package com.gql.gqlpostsservice;

import com.apollographql.federation.graphqljava.Federation;
import com.apollographql.federation.graphqljava._Entity;
import com.gql.gqlpostsservice.domain.PostFilter;
import com.gql.gqlpostsservice.domain.User;
import com.gql.gqlpostsservice.resolver.QueryResolver;
import graphql.kickstart.tools.SchemaParser;
import graphql.kickstart.tools.SchemaParserDictionary;
import graphql.kickstart.tools.SchemaParserOptions;
import graphql.language.*;
import graphql.schema.DataFetchingEnvironment;
import graphql.schema.GraphQLSchema;
import graphql.schema.idl.SchemaPrinter;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Configuration
public class GraphQLConfig {

    @Bean
    public GraphQLSchema customSchema(SchemaParser schemaParser, QueryResolver queryResolver) {


        return Federation.transform(schemaParser.makeExecutableSchema())
                .fetchEntities(env ->
                        env.<List<Map<String, Object>>>getArgument(_Entity.argumentName)
                                .stream()
                                .map(values -> {
                                    if ("User".equals(values.get("__typename"))) {
                                        final Object id = values.get("id");
                                        if (id instanceof Integer) {
                                            String body = extractValueFromFederatedQuery(env, "User", "posts", "body");
                                            return User.builder()
                                                    .id((Integer) id)
                                                    .posts(queryResolver.posts(PostFilter.builder().userId((Integer) id).build()).stream()
                                                            .filter(post -> post.getBody().contains(body))
                                                            .collect(Collectors.toList()))
                                                    .build();
                                        }
                                    }
                                    return null;
                                })
                                .collect(Collectors.toList())
                )
                .resolveEntityType(env -> {
                    final Object src = env.getObject();
                    if (src instanceof User) {
                        return env.getSchema().getObjectType("User");
                    }
                    return null;
                }).build();
    }

    private String extractValueFromFederatedQuery(DataFetchingEnvironment env, String fragmentName, String fieldName, String argumentName) {
        InlineFragment fragment = (InlineFragment) env
                .getField()
                .getSelectionSet()
                .getSelections()
                .stream()
                .filter(selection -> ((InlineFragment) selection).getTypeCondition().getName().equals(fragmentName))
                .findFirst()
                .orElse(null);
        if(fragment == null) return null;
        Field field = (Field) fragment
                .getSelectionSet()
                .getSelections()
                .stream()
                .filter(selection -> ((Field) selection).getName().equals(fieldName))
                .findFirst()
                .orElse(null);
        if(field == null) return null;
        StringValue stringValue = (StringValue) field
                .getArguments()
                .stream()
                .filter(arg -> arg.getName().equals(argumentName))
                .map(Argument::getValue)
                .findFirst()
                .orElse(null);
        if(stringValue == null) return null;
        return stringValue.getValue();
    }

    @Bean
    public SchemaPrinter customSchemaPrinter() {
        return new SchemaPrinter(SchemaPrinter.Options.defaultOptions().includeDirectives(true).includeDirectiveDefinitions(true).includeSchemaDefinition(true).includeIntrospectionTypes(true));
    }

    @Bean
    public BeanPostProcessor schemaParserOptionsBuilderPostProcessor() {
        return new BeanPostProcessor() {
            @Override
            public Object postProcessAfterInitialization(@NotNull Object bean, @NotNull String beanName)
                    throws BeansException {
                return bean instanceof SchemaParserOptions.Builder
                        ? ((SchemaParserOptions.Builder) bean).includeUnusedTypes(true) : bean;
            }
        };
    }

    @Bean
    public SchemaParserDictionary schemaParserDictionary() {
        return new SchemaParserDictionary().add("User", User.class);
    }

}