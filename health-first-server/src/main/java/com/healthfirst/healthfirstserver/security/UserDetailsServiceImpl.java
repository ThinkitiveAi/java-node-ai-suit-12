package com.healthfirst.healthfirstserver.security;

import com.healthfirst.healthfirstserver.domain.entity.Provider;
import com.healthfirst.healthfirstserver.exception.ResourceNotFoundException;
import com.healthfirst.healthfirstserver.repository.ProviderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final ProviderRepository providerRepository;

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Provider provider = providerRepository.findByEmail(email)
                .orElseThrow(() -> 
                    new UsernameNotFoundException("Provider not found with email: " + email)
                );

        return UserDetailsImpl.build(provider);
    }
}
