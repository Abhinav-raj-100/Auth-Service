package org.example.request;


import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class RefreshTokenRequestDTO {

    private String token;
}
