package io.github.tavim26.onlinestore.service;

import io.github.tavim26.onlinestore.config.DataAccessException;
import io.github.tavim26.onlinestore.config.Database;
import io.github.tavim26.onlinestore.dao.UserDao;
import io.github.tavim26.onlinestore.model.Role;
import io.github.tavim26.onlinestore.model.User;

import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.regex.Pattern;

public class UserService {

    /** Data entered in the registration form. */
    public record Registration(String firstName, String lastName, String email, String password,
                               String cnp, String address, String phone) {
    }

    private static final Pattern NAME = Pattern.compile("^\\p{L}+([ '-]\\p{L}+)*$");
    private static final Pattern EMAIL = Pattern.compile("^[\\w.%+-]+@[\\w.-]+\\.[A-Za-z]{2,}$");
    private static final Pattern PHONE = Pattern.compile("^0\\d{9}$");
    private static final Pattern CNP = Pattern.compile("^[1-9]\\d{12}$");
    private static final int[] CNP_WEIGHTS = {2, 7, 9, 1, 4, 6, 3, 5, 8, 2, 7, 9};
    private static final String PASSWORD_RULE =
            "Password must have at least 8 characters, including a letter and a digit.";

    private final UserDao userDao = new UserDao();

    public User register(Registration form) {
        String email = normalizeEmail(form.email());

        Validator validator = new Validator();
        validateProfile(validator, form.firstName(), form.lastName(), email,
                form.cnp(), form.address(), form.phone());
        validator.check(isValidPassword(form.password()), PASSWORD_RULE);
        validator.throwIfInvalid();

        User user = new User(Role.CUSTOMER, form.firstName().trim(), form.lastName().trim(), email,
                PasswordHasher.hash(form.password()), form.cnp(), form.address().trim(), form.phone());
        try {
            Database.withConnection(c -> userDao.insert(c, user));
        } catch (DataAccessException e) {
            throw translateDuplicate(e);
        }
        return getById(user.getId());
    }

    public User login(String email, String password) {
        Optional<User> user = Database.withConnection(c -> userDao.findByEmail(c, normalizeEmail(email)));
        if (user.isEmpty() || password == null
                || !PasswordHasher.verify(password, user.get().getPasswordHash())) {
            // Same message in both cases: don't reveal which emails have accounts.
            throw new BusinessException("Invalid email or password.");
        }
        return user.get();
    }

    public User getById(int userId) {
        return Database.withConnection(c -> userDao.findById(c, userId))
                .orElseThrow(() -> new BusinessException("User not found."));
    }

    public void updateProfile(User user) {
        user.setEmail(normalizeEmail(user.getEmail()));

        Validator validator = new Validator();
        validateProfile(validator, user.getFirstName(), user.getLastName(), user.getEmail(),
                user.getCnp(), user.getAddress(), user.getPhone());
        validator.throwIfInvalid();

        try {
            if (!Database.withConnection(c -> userDao.update(c, user))) {
                throw new BusinessException("User not found.");
            }
        } catch (DataAccessException e) {
            throw translateDuplicate(e);
        }
    }

    public void changePassword(int userId, String currentPassword, String newPassword) {
        User user = getById(userId);
        if (currentPassword == null || !PasswordHasher.verify(currentPassword, user.getPasswordHash())) {
            throw new BusinessException("Current password is incorrect.");
        }
        new Validator().check(isValidPassword(newPassword), PASSWORD_RULE).throwIfInvalid();

        user.setPasswordHash(PasswordHasher.hash(newPassword));
        Database.withConnection(c -> userDao.update(c, user));
    }

    // ---- validation rules (package-private so they can be unit tested) ----

    private static void validateProfile(Validator v, String firstName, String lastName, String email,
                                        String cnp, String address, String phone) {
        v.check(isValidName(firstName), "First name may contain only letters, spaces, hyphens and apostrophes.");
        v.check(isValidName(lastName), "Last name may contain only letters, spaces, hyphens and apostrophes.");
        v.check(isValidEmail(email), "Email address is not valid.");
        v.check(isValidCnp(cnp), "CNP is not valid.");
        v.check(!Validator.isBlank(address) && address.length() <= 255,
                "Address is required (max 255 characters).");
        v.check(isValidPhone(phone), "Phone number must have 10 digits and start with 0.");
    }

    static boolean isValidName(String name) {
        return name != null && name.trim().length() <= 50 && NAME.matcher(name.trim()).matches();
    }

    static boolean isValidEmail(String email) {
        return email != null && email.length() <= 100 && EMAIL.matcher(email).matches();
    }

    static boolean isValidPhone(String phone) {
        return phone != null && PHONE.matcher(phone).matches();
    }

    static boolean isValidPassword(String password) {
        return password != null
                && password.length() >= 8
                && password.chars().anyMatch(Character::isLetter)
                && password.chars().anyMatch(Character::isDigit);
    }

    /** Checks the format and the control digit of a Romanian CNP. */
    static boolean isValidCnp(String cnp) {
        if (cnp == null || !CNP.matcher(cnp).matches()) {
            return false;
        }
        int sum = 0;
        for (int i = 0; i < 12; i++) {
            sum += (cnp.charAt(i) - '0') * CNP_WEIGHTS[i];
        }
        int control = sum % 11 == 10 ? 1 : sum % 11;
        return control == cnp.charAt(12) - '0';
    }

    private static String normalizeEmail(String email) {
        return email == null ? null : email.trim().toLowerCase(Locale.ROOT);
    }

    private static RuntimeException translateDuplicate(DataAccessException e) {
        return e.isConstraintViolation()
                ? new BusinessException("An account with this email or CNP already exists.")
                : e;
    }

    public List<User> getAll() {
        return Database.withConnection(userDao::findAll);
    }
}