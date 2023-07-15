package Exceptions;

public class InvalidUserNameException extends Exception { 
    
    public InvalidUserNameException(String text) { 
        super((text.contains("@") ? "Email not found" : "UserID not found")); 
    }
   
}