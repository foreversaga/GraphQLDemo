package com.example.graphqlservlet;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import javax.servlet.annotation.WebServlet;

import graphql.com.google.common.collect.Lists;
import graphql.kickstart.servlet.GraphQLConfiguration;
import graphql.kickstart.servlet.GraphQLHttpServlet;
import graphql.schema.GraphQLSchema;
import graphql.schema.idl.RuntimeWiring;
import graphql.schema.idl.SchemaGenerator;
import graphql.schema.idl.SchemaParser;
import graphql.schema.idl.TypeDefinitionRegistry;
import graphql.schema.idl.TypeRuntimeWiring;

@WebServlet(value = "/graphql")
public class GraphQlServlet extends GraphQLHttpServlet {

	@Override
	protected GraphQLConfiguration getConfiguration() {
		InputStream systemResourceAsStream = getClass().getClassLoader().getResourceAsStream("graphql/schema.graphqls");
		TypeDefinitionRegistry typeDefinitionRegistry = new SchemaParser().parse(systemResourceAsStream);

		ArrayList<Book> books = Lists.newArrayList(
			Book.builder()
				.id(1L)
				.title("bookA")
				.authorId(1L)
				.build(),
			Book.builder()
				.id(2L)
				.title("bookB")
				.authorId(2L)
				.build()
		);

		ArrayList<Author> authors = Lists.newArrayList(
			Author.builder()
				.id(1L)
				.name("AuthorA")
				.age(30)
				.build(),
			Author.builder()
				.id(2L)
				.name("AuthorB")
				.age(40)
				.build()
		);

		RuntimeWiring runtimeWiring = RuntimeWiring.newRuntimeWiring()
			.type(TypeRuntimeWiring.newTypeWiring("Query")
				.dataFetcher("getBooks", dataFetchingEnvironment -> books))

			.type(TypeRuntimeWiring.newTypeWiring("Book")
				.dataFetcher("author", dataFetchingEnvironment -> {

					Book book = dataFetchingEnvironment.getSource();

					return authors.stream()
						.filter(author -> author.getId() == book.getAuthorId())
						.findFirst().orElse(null);

				})).build();

		SchemaGenerator schemaGenerator = new SchemaGenerator();
		GraphQLSchema graphQLSchema = schemaGenerator.makeExecutableSchema(typeDefinitionRegistry, runtimeWiring);
		return GraphQLConfiguration.with(graphQLSchema).build();
	}
}
