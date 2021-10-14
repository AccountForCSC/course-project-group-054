/*
 * Below is the Person class which represents the origin of our program.
 * This will be the superclass of User and will also be a stand in for
 * a person not in a Group.
 */

package Entities;

public abstract class Person {
    protected String name;
    protected float balance;
    protected String email;

    /**
     * Construct Person, giving them the given name, balance, and email.
     * @param name the Person's name
     * @param balance the Person's balance (the amount owed)
     * @param email the Person's email used to contact them
     */
    public Person(String name, float balance, String email) {
        this.name = name;
        this.balance = balance;
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public float getBalance() {
        return balance;
    }

    public void setBalance(float balance) {
        this.balance = balance;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}