package cz.muni.fi.pa165.library.services;

import cz.muni.fi.pa165.library.entities.Role;
import cz.muni.fi.pa165.library.entities.User;
import cz.muni.fi.pa165.library.repositories.UserRepository;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.util.Arrays;
import java.util.List;

/**
 * @author Katarína Hermanová
 * UČO 433511
 * Github katHermanova
 */
public class UserServiceTest {
    private User user;
    private UserService userService;

    @Mock
    private UserRepository userRepository;

    @Before
    public void init() {
        userService = new UserService(userRepository);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testFindByIdNegative() {
        userService.findById(-1);
    }

    @Test
    public void testFindByFirstNameNull() {
        Assert.assertTrue(userService.findByFirstName(null).isEmpty());
    }

    @Test
    public void testFindByLastNameNull() {
        Assert.assertTrue(userService.findByLastName(null).isEmpty());
    }

    @Test
    public void testFindByEmailNull() {
        Assert.assertTrue(userService.findByEmail(null).isEmpty());
    }

   /* @Test
    public void testFindByFirstName() {
        setUser();
        String firstName = user.getFirstName();

        Mockito.when(userRepository.findAll())
                .thenReturn(Arrays.asList(user));

        Assert.assertEquals(Arrays.asList(user), userService.findByFirstName(firstName));
    }

    private void setUser() {
        List<Role> roles = Arrays.asList(new Role(Role.LIBRARIAN), new Role(Role.MEMBER));
        user = new User("Kat", "Herman", "kHerm@mail.com", "This0Is1Hash", roles);
    }*/
}
