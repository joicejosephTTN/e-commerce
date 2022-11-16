package com.ttn.ecommerce.model;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@Data

public class UserDTO {
//    @NotEmpty(message = "First Name is mandatory.")
//    @Pattern(regexp="(^[A-Za-z]*$).{2,30}",message = "Invalid Input." +
//            "Name can only contain alphabets. Must contain 2-30 characters.")
    private String firstName;
//    @NotEmpty(message = "Last Name is mandatory.")
//    @Pattern(regexp="(^[A-Za-z]*$).{2,30}",message = "Invalid Input." +
//            "Name can only contain alphabets. Must contain 2-30 characters.")
    private String lastName;
    //optional
//    @Pattern(regexp="(^[A-Za-z]*$).{0,30}",message = "Invalid Input." +
//            "Middle name can only contain alphabets. Must contain 1-30 characters.")
    private String middleName;

//    @Pattern(regexp = "(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,15}",
//            message = "Enter a valid password. Password must contain 8-15 characters" +
//                    "with at least 1 lower case, 1 upper case, 1 special character, and 1 Number.")
    private String password;
    private String reEnterPassword;
//    @Email(message = "Enter a valid email.")
    private String email;
}