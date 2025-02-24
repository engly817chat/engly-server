package com.engly.engly_server.utils.registration_chooser;

import com.engly.engly_server.models.entity.AdditionalInfo;
import com.engly.engly_server.models.entity.Users;
import com.engly.engly_server.models.enums.Provider;
import com.engly.engly_server.models.request.SignUpRequest;
import com.engly.engly_server.repo.UserRepo;
import lombok.extern.slf4j.Slf4j;
import org.graalvm.collections.Pair;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import java.util.Set;

@Component
@Slf4j
public class EmailRegistration implements RegistrationChooser {
    private final UserRepo userRepo;
    private final PasswordEncoder passwordEncoder;

    @Value("#{'${sysadmin.email}'.split(',\\s*')}")
    private Set<String> sysadminEmails;

    public EmailRegistration(UserRepo userRepo, PasswordEncoder passwordEncoder) {
        this.userRepo = userRepo;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public Pair<Users, AdditionalInfo> registration(SignUpRequest signUpRequest) {
        log.info("[AuthService:registerUser]User Registration Started with :::{}", signUpRequest);
        userRepo.findByEmail(signUpRequest.email()).ifPresent(users -> {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User Already Exist");
        });

        var users = Users.builder()
                .roles(sysadminEmails.contains(signUpRequest.email()) ? "ROLE_SYSADMIN" : "ROLE_USER")
                .email(signUpRequest.email())
                .emailVerified(Boolean.FALSE)
                .username(signUpRequest.username())
                .password(passwordEncoder.encode(signUpRequest.password()))
                .provider(Provider.LOCAL)
                .build();

        var addInfo = AdditionalInfo.builder()
                .goal(signUpRequest.goals())
                .englishLevel(signUpRequest.englishLevel())
                .gender(signUpRequest.gender())
                .dateOfBirth(signUpRequest.dateOfBirth())
                .nativeLanguage(signUpRequest.nativeLanguage())
                .build();

        users.setAdditionalInfo(addInfo);
        addInfo.setUser(users);

        Users save = userRepo.save(users);

        return Pair.create(save, save.getAdditionalInfo());
    }

    @Override
    public Provider getProvider() {
        return Provider.LOCAL;
    }
}
