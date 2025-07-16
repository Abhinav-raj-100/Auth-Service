package org.example.service;


import org.example.entities.RefreshToken;
import org.example.entities.UserInfo;
import org.example.repository.RefreshTokenRepository;
import org.example.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Service
public class RefreshTokenService {

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    @Autowired
    private UserRepository userRepository;


    public RefreshToken createRefreshToken(String userName)
    {
        UserInfo userInfo  = this.userRepository.findByUsername(userName);

        RefreshToken refreshToken = RefreshToken
                .builder()
                .userInfo(userInfo)
                .token(UUID.randomUUID().toString())
                .expiryDate(Instant.now().plusMillis(600000))
                .build();
        return this.refreshTokenRepository.save(refreshToken);
    }

    public RefreshToken verifyExpiration(RefreshToken token){
        if(token.getExpiryDate().compareTo(Instant.now())<0)
        {
            this.refreshTokenRepository.delete(token);
            throw new RuntimeException(token.getToken()+
                    "Refresh token is expired. Please make a new login...!");

        }
        return token;
    }

    public Optional<RefreshToken>  findByToken(String token)
    {
        return refreshTokenRepository.findByToken(token);
    }
}
