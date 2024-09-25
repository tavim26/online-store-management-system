package BusinessLogic;

import Model.User;

public class UserBLL {

    public int nameValidator(String firstName, String lastName) {
        if (firstName == null || firstName.trim().isEmpty()) {
            return 1;
        }

        if (lastName == null || lastName.trim().isEmpty()) {
            return 1;
        }

        String nameRegex = "^[A-Za-z]+$";

        if (!firstName.matches(nameRegex)) {
            return 2;
        }

        if (!lastName.matches(nameRegex)) {
            return 2;
        }

        return 0;
    }

    public boolean emailValidator(String email) {
        String EMAIL_REGEX = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$";
        return email != null && email.matches(EMAIL_REGEX);
    }

    private int passwordValidator(String password) {
        if (password == null || password.trim().isEmpty()) {
            return 1;
        }

        if (password.length() < 6) {
            return 2;
        }

        return 0;
    }

    private int addressValidator(String address) {
        if (address == null || address.trim().isEmpty()) {
            return 1;
        }

        return 0;
    }

    private int cnpValidator(String cnp) {
        String cnpRegex = "^[0-9]{13}$";

        if (cnp == null || cnp.trim().isEmpty()) {
            return 1;
        }

        if (!cnp.matches(cnpRegex)) {
            return 2;
        }

        return 0;
    }

    private int phoneValidator(String phone) {
        if (phone == null || phone.trim().isEmpty()) {
            return 1;
        }

        if (!phone.matches("\\d{10}")) {
            return 2;
        }

        return 0;
    }

    public int userValidator(User user) {
        int nameValidation = nameValidator(user.getFirstName(), user.getLastName());
        if (nameValidation != 0) {
            return nameValidation;
        }

        if (!emailValidator(user.getEmail())) {
            return 3;
        }

        int passwordValidation = passwordValidator(user.getPassword());
        if (passwordValidation != 0) {
            return passwordValidation + 3;
        }

        int addressValidation = addressValidator(user.getAddress());
        if (addressValidation != 0) {
            return 6;
        }

        int cnpValidation = cnpValidator(user.getCnp());
        if (cnpValidation != 0) {
            return 7;
        }

        int phoneValidation = phoneValidator(user.getPhone());
        if (phoneValidation != 0) {
            return 8;
        }

        return 0;
    }

}
