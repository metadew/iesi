package io.metadew.iesi.data.generation.configuration;

import io.metadew.iesi.data.generation.execution.GenerationComponentExecution;
import io.metadew.iesi.data.generation.execution.GenerationDataExecution;


public class Book extends GenerationComponentExecution {

    public Book(GenerationDataExecution execution) {
        super(execution);
    }

    public String title() {
        return fetch("book.title");
    }

    public String author() {
        return parse("book.title");
    }

    public String publisher() {
        return fetch("book.publisher");
    }

    public String genre() {
        return fetch("book.genre");
    }
}
