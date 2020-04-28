package cz.muni.fi.pa165.library.services;

import cz.muni.fi.pa165.library.entities.User;
import cz.muni.fi.pa165.library.repositories.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;

/**
 * service layer class for User
 * any business logic should be implemented here
 *
 * @author Katarína Hermanová
 * UČO 433511
 * Github katHermanova
 */
@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    private static final Logger LOGGER = LoggerFactory.getLogger(UserService.class);

    /**
     * class constructor
     *
     * @param userRepository UserDAO
     */
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * method is looking for specific User by id
     *
     * @param id of user we are looking for
     * @return User
     * @throws IllegalArgumentException if id less than 0
     */
    public User findById(long id) {
        if (id < 0) {
            throw new IllegalArgumentException("ID can not be less than 0.");
        }
        return userRepository.findById(id).get();
    }

    /**
     * method is looking for users by first name
     *
     * @param firstName
     * @return list of users or empty list if non matches
     * @throws IllegalArgumentException if firstName is illegal argument
     */
    public List<User> findByFirstName(String firstName) {
        List<User> foundUsers = new ArrayList<>();
        if (firstName == null || firstName.isEmpty()) {
            throw new IllegalArgumentException("FirstName is empty or null.");
        }

        for (User user : findAll()) {
            if (user.getFirstName().equals(firstName)) {
                foundUsers.add(user);
            }
        }
        return foundUsers;
    }

    /**
     * method is looking for users by lastName
     *
     * @param lastName
     * @return list of users or empty list if non matches
     * @throws IllegalArgumentException if lastName is illegal argument
     */
    public List<User> findByLastName(String lastName) {
        List<User> foundUsers = new ArrayList<>();
        if (lastName == null || lastName.isEmpty()) {
            throw new IllegalArgumentException("LastName is empty or null.");
        }

        for (User user : findAll()) {
            if (user.getLastName().equals(lastName)) {
                foundUsers.add(user);
            }
        }
        return foundUsers;
    }

    /**
     * method is looking for users by email
     *
     * @param email
     * @return user or null if non matches
     * @throws IllegalArgumentException if email is illegal argument
     */
    public User findByEmail(String email) {
        if (email == null || email.isEmpty()) {
            throw new IllegalArgumentException("Email is empty or null.");
        }

        for (User user : findAll()) {
            if (user.getEmail().equals(email)) {
                return user;
            }
        }
        return null;
    }

    /**
     * @return list of all users
     */
    public List<User> findAll() {
        return userRepository.findAll();
    }

    /**
     *
     * @return list of all librarians
     */
    public List<User> findAllLibrarians() {
        List<User> librarians = new ArrayList<>();
        for (User user: findAll()) {
            if (user.isLibrarian()) {
                librarians.add(user);
            }
        }
        return librarians;
    }

    /**
     * method adds user
     *
     * @param user
     * @throws IllegalArgumentException if user is null or has illegal attributes
     */
    public void addUser(User user, String password) {
        if (user == null) {
            throw new IllegalArgumentException("Can not add non-existing user.");
        }
        if (password == null || password.isEmpty()) {
            throw new IllegalArgumentException("Password is empty or null.");
        }
        if (user.getFirstName() == null || user.getLastName() == null
                || user.getEmail() == null) {
            throw new IllegalArgumentException("User we adding has null attribute.");
        }
        for (User u : findAll()) {
            if (u.getEmail().equals(user.getEmail())) {
                throw new IllegalArgumentException("Email is already used by another user.");
            }
        }
        user.setPasswordHash(createHash(password));
        userRepository.save(user);
        LOGGER.info("User was added.");
    }

    /**
     * method deletes user by id
     *
     * @param id of user
     * @throws IllegalArgumentException if ID is less than 0
     */
    public void deleteUser(long id) {
        if (id < 0) {
            throw new IllegalArgumentException("Id can not be less than 0.");
        }
        if (userRepository.existsById(id)) {
            userRepository.deleteById(id);
            LOGGER.info("User deleted.");
        } else {
            LOGGER.warn("Trying to delete non-existing user.");
        }
    }

    /**
     *
     * @param user to authenticate
     * @param password entered password
     * @return if entered password is valid
     * @throws IllegalArgumentException if user does not have any password associated with him
     */
    public boolean authenticate(User user, String password) {
        return validatePassword(password, user.getPasswordHash());
    }

    //https://github.com/katHermanova/PA165/blob/master/eshop-service/src/main/java/cz/fi/muni/pa165/service/UserServiceImpl.java

    private static String createHash(String password) {
        final int SALT_BYTE_SIZE = 24;
        final int HASH_BYTE_SIZE = 24;
        final int PBKDF2_ITERATIONS = 1000;
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[SALT_BYTE_SIZE];
        random.nextBytes(salt);
        byte[] hash = pbkdf2(password.toCharArray(), salt, PBKDF2_ITERATIONS, HASH_BYTE_SIZE);
        return PBKDF2_ITERATIONS + ":" + toHex(salt) + ":" + toHex(hash);
    }

    private static byte[] pbkdf2(char[] password, byte[] salt, int iterations, int bytes) {
        try {
            PBEKeySpec spec = new PBEKeySpec(password, salt, iterations, bytes * 8);
            return SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256").generateSecret(spec).getEncoded();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static boolean validatePassword(String password, String correctHash) {
        if(password==null) return false;
        if(correctHash==null) throw new IllegalArgumentException("password hash is null");
        String[] params = correctHash.split(":");
        int iterations = Integer.parseInt(params[0]);
        byte[] salt = fromHex(params[1]);
        byte[] hash = fromHex(params[2]);
        byte[] testHash = pbkdf2(password.toCharArray(), salt, iterations, hash.length);
        return slowEquals(hash, testHash);
    }

    /**
     * Compares two byte arrays in length-constant time. This comparison method
     * is used so that password hashes cannot be extracted from an on-line
     * system using a timing attack and then attacked off-line.
     *
     * @param a the first byte array
     * @param b the second byte array
     * @return true if both byte arrays are the same, false if not
     */
    private static boolean slowEquals(byte[] a, byte[] b) {
        int diff = a.length ^ b.length;
        for (int i = 0; i < a.length && i < b.length; i++)
            diff |= a[i] ^ b[i];
        return diff == 0;
    }

    private static byte[] fromHex(String hex) {
        byte[] binary = new byte[hex.length() / 2];
        for (int i = 0; i < binary.length; i++) {
            binary[i] = (byte) Integer.parseInt(hex.substring(2 * i, 2 * i + 2), 16);
        }
        return binary;
    }

    private static String toHex(byte[] array) {
        BigInteger bi = new BigInteger(1, array);
        String hex = bi.toString(16);
        int paddingLength = (array.length * 2) - hex.length();
        return paddingLength > 0 ? String.format("%0" + paddingLength + "d", 0) + hex : hex;
    }
}
