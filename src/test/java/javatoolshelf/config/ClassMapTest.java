package javatoolshelf.config;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static javatoolshelf.config.ClassMapTest.Gender.Male;

class ClassMapTest {
    Book book1 = new Book(
            "War and Peace",
            new Author(
                    "Leo",
                    Male));

    BookWithMultiAuthor book2 = new BookWithMultiAuthor(
            "Good Omens: The Nice and Accurate Prophecies of Agnes Nutter, Witch",
            Arrays.asList(new Author("Terry", Male), new Author("Neil", Male)));

    @Test
    void test() {
        ClassMap map1 = new ClassMap(book1);
        System.out.println(map1.toString());

        ClassMap map2 = new ClassMap(book2);
        System.out.println(map2.toString());
    }

    class Book {
        String title;
        Author author;

        public String getTitle() {
            return title;
        }

        public Author getAuthor() {
            return author;
        }

        public Book(String title, Author author) {
            this.title = title;
            this.author = author;
        }

    }

    class BookWithMultiAuthor {
        String title;
        List<Author> authors;

        public String getTitle() {
            return title;
        }

        public List<Author> getAuthors() {
            return authors;
        }

        public BookWithMultiAuthor(String title, List<Author> authors) {
            this.title = title;
            this.authors = authors;
        }

    }

    class Author {
        String firstName;
        Gender gender;

        public Author(String firstName, Gender gender) {
            this.firstName = firstName;
            this.gender = gender;
        }

        public String getFirstName() {
            return firstName;
        }

        public Gender getGender() {
            return gender;
        }
    }

    enum Gender {
            Female, Male
    }

}