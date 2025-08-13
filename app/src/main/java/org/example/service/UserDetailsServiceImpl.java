package org.example.service;

import org.example.Util.AuthValidator;
import org.example.entities.UserInfo;
import org.example.eventProducer.UserInfoEvent;
import org.example.eventProducer.UserInfoProducer;
import org.example.model.UserInfoDTO;
import org.example.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Objects;
import java.util.UUID;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserInfoProducer userInfoProducer;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        UserInfo user = this.userRepository.findByUsername(username);
        if(user==null)
        {
            throw new UsernameNotFoundException("could not Found User...!!");
        }

        return new CustomUserDetails(user);
    }


    public UserInfo checkIfUserAlreadyExists(UserInfoDTO userInfoDTO)
    {
        return this.userRepository.findByUsername(userInfoDTO.getUsername());
    }


    public Boolean signUp(UserInfoDTO userInfoDTO) {
        String email = userInfoDTO.getUsername();
        String password = userInfoDTO.getPassword();

        // ✅ Validate email
        if (!AuthValidator.isValidEmail(email)) {
            throw new IllegalArgumentException("Invalid email format.");
        }

        // ✅ Validate password and return specific message
        String passwordError = AuthValidator.getPasswordValidationError(password);
        if (passwordError != null) {
            throw new IllegalArgumentException("Invalid password: " + passwordError);
        }

        // ✅ Check if user already exists
        if (Objects.nonNull(checkIfUserAlreadyExists(userInfoDTO))) {
            return false;
        }

        // ✅ Proceed with encoding and saving
        userInfoDTO.setPassword(passwordEncoder.encode(password));
        String userId = UUID.randomUUID().toString();
        this.userRepository.save(
                new UserInfo(userId, email, userInfoDTO.getPassword(), new HashSet<>())
        );

        // pushEventToQueue
        userInfoProducer.sendEventToKafka(userInfoEventToPublish(userInfoDTO,userId));
        return true;
    }

    private UserInfoEvent userInfoEventToPublish(UserInfoDTO userInfoDto, String userId)
    {
        return UserInfoEvent.builder()
                .userId(userId)
                .firstName(userInfoDto.getFirstName())
                .lastName(userInfoDto.getLastName())
                .email(userInfoDto.getEmail())
                .phoneNumber(userInfoDto.getPhoneNumber()).build();
    }

}
