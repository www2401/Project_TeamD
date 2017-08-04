package com.example.ilove.login;

/**
 * Created by Administrator on 2016-06-07.
 */
public class Person {
    private String email;
    private String password;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }


    @Override
    public String toString() {
        return "Person [email=" + email + ", password=" + password+"]";
    }
}
