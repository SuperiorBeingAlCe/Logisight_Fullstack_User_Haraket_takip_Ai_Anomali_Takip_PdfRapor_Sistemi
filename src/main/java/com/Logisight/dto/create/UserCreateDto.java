package com.Logisight.dto.create;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UserCreateDto {
	
	@NotBlank(message = "Kullanıcı adı boş olamaz")
    @Size(min = 4, max = 100)
    private String username;

    @NotBlank(message = "Email boş olamaz")
    @Email(message = "Geçerli bir email girin")
    private String email;

    @NotBlank(message = "Şifre boş olamaz")
    @Size(min = 6, message = "Şifre en az 6 karakter olmalı")
    private String password;


}
