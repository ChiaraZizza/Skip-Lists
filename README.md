# Skip-Lists

Description: This program implements skip lists, which is an alternative to balanced trees. It was written in Java using the Eclipse IDE.

This algorithm creates a skip list, inserts key, value pairs into the list, deletes from the list, and prints the list to the screen at any given time. It inserts by finding the appropriate location within the skip list using the keys. It then determines how many levels of the list this key, value pair is placed by generating a random number and inserting into another level of this number is within a certain range (for this code the range is 0 - 0.25). To delete a node, the key is found within the bottom level of the list and all instances on this level and higher levels are removed, with pointers updating to point to the next elements in the list. A test suite also exists to ensure functionality.

Contributors: Chiara Zizza, David Neil Asanza, and Shida Jing

http://www.cs.grinnell.edu/~rebelsky/Courses/CSC301/2017F/assignments/assignment06
