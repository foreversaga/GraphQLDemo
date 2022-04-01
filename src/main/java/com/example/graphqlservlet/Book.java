package com.example.graphqlservlet;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Book {

	private long id;

	private String title;

	private long authorId;
}
