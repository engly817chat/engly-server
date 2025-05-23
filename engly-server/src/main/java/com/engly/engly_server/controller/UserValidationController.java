package com.engly.engly_server.controller;

import com.engly.engly_server.models.dto.AvailabilityResponseDto;
import com.engly.engly_server.models.dto.GoogleAvailabilityDto;
import com.engly.engly_server.service.common.UserValidationService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/valid")
@RequiredArgsConstructor
public class UserValidationController {
    private final UserValidationService userValidationService;

    @GetMapping("/first-login")
    @PreAuthorize("hasAuthority('SCOPE_AUTHORIZE')")
    public ResponseEntity<GoogleAvailabilityDto> firstLogin() {
        return ResponseEntity.ok(userValidationService.firstLogin());
    }

    @Operation(
            summary = "Перевірка при реєстрації на username live",
            description = """
                        Дозволяє перевіряти в живую юзернейм - це для фронтенду, треба додати listener
                    """
    )
    @GetMapping("/check-username")
    public ResponseEntity<AvailabilityResponseDto> checkUsernameAvailability(@NotBlank(message = "Username is blank")
                                                                             @Size(min = 2, max = 50, message = "Username must be between 2 and 50 characters.")
                                                                             @Pattern(
                                                                                     regexp = "^[a-zA-Z]{2,50}$",
                                                                                     message = "Username must contain only letters (a-z, A-Z) and be between 2 and 50 characters long."
                                                                             )
                                                                             @RequestParam
                                                                             String username) {
        return new ResponseEntity<>(userValidationService.isUsernameAvailable(username), HttpStatus.OK);
    }

    @Operation(
            summary = "Перевірка при реєстрації на email live",
            description = """
                        Дозволяє перевіряти в живую email - це для фронтенду, треба додати listener
                    """
    )
    @GetMapping("/check-email")
    public ResponseEntity<AvailabilityResponseDto> checkEmailAvailability(@RequestParam
                                                                          @Email(message = "Isn't email")
                                                                          @NotBlank(message = "Email is blank")
                                                                          @Size(max = 50, message = "Email cannot exceed 50 characters. Please shorten your input.")
                                                                          String email) {
        return new ResponseEntity<>(userValidationService.isEmailAvailable(email), HttpStatus.OK);
    }
}
