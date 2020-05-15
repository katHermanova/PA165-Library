package cz.muni.fi.pa165.library.controllers;

import cz.muni.fi.pa165.library.dto.UserDTO;
import cz.muni.fi.pa165.library.facade.UserFacade;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.slf4j.Logger;

import java.util.List;

/**
 * @author Katarína Hermanová
 * UČO 433511
 * Github katHermanova
 */
@RestController
@Transactional
public class UserController extends AbstractController {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserController.class);

    private final UserFacade userFacade;

    public UserController(UserFacade userFacade) {
        this.userFacade = userFacade;
    }

    /*@PostMapping(value = "/books", consumes = MediaType.APPLICATION_JSON_VALUE)
    public long createBook(@RequestBody BookDTO book) {
        LOGGER.info("Creating book {}.", book);
        return bookFacade.createBook(book);
    }

    @DeleteMapping(value = "/books", params = "id")
    public long deleteBook(@RequestParam long id) {
        LOGGER.info("Deleting book with id {}.", id);
        return bookFacade.deleteBook(id);
    }*/

    @GetMapping(value = "/users", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<UserDTO> findAllUsers() {
        LOGGER.info("Finding all books.");
        return userFacade.findAll();
    }

   /* @GetMapping(value = "/books", params = "title", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<BookDTO> findByTitle(@RequestParam String title) {
        LOGGER.info("Finding all books containing {} in title.", title);
        return bookFacade.findByTitle(title);
    }

    @GetMapping(value = "/books", params = "author", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<BookDTO> findByAuthor(@RequestParam String author) {
        LOGGER.info("Finding all books containing {} as an author.", author);
        return bookFacade.findByAuthor(author);
    }*/
}
