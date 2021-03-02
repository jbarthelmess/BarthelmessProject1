package entities;

public class LoginAttempt {
    private String username;
    private String password;

    public LoginAttempt() {
        username = null;
        password = null;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        if(username == null) {
            throw new IllegalArgumentException("Username cannot be null");
        }
        if(username.equals("")) {
            throw new IllegalArgumentException("Username cannot be empty");
        }
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        if(password == null) {
            throw new IllegalArgumentException("Password cannot be null");
        }
        if(password.equals("")) {
            throw new IllegalArgumentException("Password cannot be empty");
        }
        this.password = password;
    }
}
