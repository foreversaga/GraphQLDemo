package com.example.graphqlservlet;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Author {

	private long id;

	private String name;

	private int age;
}
