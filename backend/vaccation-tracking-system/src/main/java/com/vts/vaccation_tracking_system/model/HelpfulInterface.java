package com.vts.vaccation_tracking_system.model;

// haven't yet found a suitable name for this interface
public interface HelpfulInterface {
}

// why have such an interface ?
// Due to the quite complex relationship between the Employee and Manager this interface was made just so
// they can both have a similar supertype so there's no need to implement separate methods to do the same thing for the two classes.
// Might find a better solution later
