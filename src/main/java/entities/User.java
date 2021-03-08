package entities;

import java.util.HashSet;

public class User {
    private int userId;
    private String username;
    private boolean isManager;
    private HashSet<Expense> myExpenses;
    private String jwt;

    public User() {
        this.userId = 0;
        this.username = "";
        this.isManager = false;
        this.myExpenses = null;
        this.jwt = null;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        if(userId <= 0) {
            throw new IllegalArgumentException("User ID cannot be Negative or Zero");
        }
        this.userId = userId;
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

    public boolean isManager() {
        return isManager;
    }

    public void setManager(boolean manager) {
        isManager = manager;
    }

    public HashSet<Expense> getMyExpenses() {
        return myExpenses;
    }

    public void setMyExpenses(HashSet<Expense> myExpenses) {
        if(myExpenses == null) {
            throw new IllegalArgumentException("Expenses cannot be null");
        }
        this.myExpenses = myExpenses;
    }

    public String getJwt() {
        return this.jwt;
    }

    public void setJwt(String jwt) {
        this.jwt = jwt;
    }
}
