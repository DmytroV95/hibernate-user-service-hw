package mate.academy.service.impl;

import mate.academy.exception.AuthenticationException;
import mate.academy.exception.RegistrationException;
import mate.academy.lib.Inject;
import mate.academy.lib.Service;
import mate.academy.model.User;
import mate.academy.service.AuthenticationService;
import mate.academy.service.UserService;
import mate.academy.util.HashUtil;

@Service
public class AuthenticationServiceImpl implements AuthenticationService {
    @Inject
    private UserService userService;

    @Override
    public User login(String email, String password) throws AuthenticationException {
        User userByEmail = userService.findByEmail(email).orElse(null);
        if (userByEmail != null && isUserPasswordValid(password, userByEmail)) {
            return userByEmail;
        }
        throw new AuthenticationException("Can't authenticate user by email: " + email
                + " or incorrect password input");
    }

    @Override
    public User register(String email, String password) throws RegistrationException {
        if (userService.findByEmail(email).isPresent()) {
            throw new RegistrationException("User with provided email: "
                    + email + " already exist");
        }
        User newUser = new User();
        newUser.setEmail(email);
        newUser.setPassword(password);
        return userService.add(newUser);
    }

    private static boolean isUserPasswordValid(String password, User userByEmail) {
        String hashedPassword = HashUtil.hashPassword(password, userByEmail.getSalt());
        return userByEmail.getPassword().equals(hashedPassword)
                && !userByEmail.getPassword().isEmpty();
    }
}