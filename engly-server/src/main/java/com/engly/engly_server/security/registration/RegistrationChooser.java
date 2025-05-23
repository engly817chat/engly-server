package com.engly.engly_server.security.registration;

import com.engly.engly_server.models.entity.Users;
import com.engly.engly_server.models.enums.Provider;
import com.engly.engly_server.models.dto.create.SignUpRequestDto;

public sealed interface RegistrationChooser permits EmailRegistration, GoogleRegistration {
    Users registration(SignUpRequestDto signUpRequestDto);
    Provider getProvider();
}
