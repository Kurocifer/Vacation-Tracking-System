package com.vts.vaccation_tracking_system.model;

// haven't yet found a suitable name for this interface
public abstract class AbstractUser {
    public abstract void setEmail(String email);
    public abstract String getEmail();

    public abstract String getPassword();

    public abstract void setPassword(String password);

    public abstract String getLastName();

    public abstract void setLastName(String lastName);

    public abstract String getFirstName();

    public abstract void setFirstName(String firstName);

    public abstract String getUsername();

    public abstract void setUsername(String username);
}

// why have such an interface ?
// Due to the quite complex relationship between the Employee and Manager this interface was made just so
// they can both have a similar supertype so there's no need to implement separate methods to do the same thing for the two classes.
// Might find a better solution later
