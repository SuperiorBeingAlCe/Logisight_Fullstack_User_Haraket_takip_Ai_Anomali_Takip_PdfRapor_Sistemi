package com.Logisight.dto.update;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.util.Set;

@Data
public class UpdateUserDTO {

    @NotBlank(message = "Kullanıcı adı boş olamaz.")
    @Size(max = 100, message = "Kullanıcı adı en fazla 100 karakter olabilir.")
    private String username;

    @NotBlank(message = "Şifre boş olamaz.")
    private String password;

    @NotBlank(message = "Email boş olamaz.")
    @Email(message = "Geçerli bir email adresi giriniz.")
    private String email;

    @NotNull(message = "Hesap etkinlik durumu belirtilmelidir.")
    private Boolean enabled;

    @NotNull(message = "Rol listesi boş olamaz.")
    @Size(min = 1, message = "En az bir rol atanmalıdır.")
    private Set<Long> roleIds;
}
