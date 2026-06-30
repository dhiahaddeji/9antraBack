package com.esprit.springjwt.security.services;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.esprit.springjwt.entity.User;
import com.fasterxml.jackson.annotation.JsonIgnore;

public class UserDetailsImpl implements UserDetails {
  private static final long serialVersionUID = 1L;

  private Long id;

  private String username;
  private User user; // Ajout d'un champ pour stocker l'objet User


  @JsonIgnore
  private String password;

  private Collection<? extends GrantedAuthority> authorities;


  public UserDetailsImpl(Long id, String username, String password,
                         Collection<? extends GrantedAuthority> authorities, User user) {
    this.id = id;
    this.username = username;
    this.password = password;
    this.authorities = authorities;
    this.user = user;
  }
  public User getUser() {
    return user;
  }
  public static UserDetailsImpl build(User user) {
    List<GrantedAuthority> authorities = user.getRoles().stream()
            .map(role -> new SimpleGrantedAuthority(role.getName().name()))
            .collect(Collectors.toList());

    return new UserDetailsImpl(
            user.getId(),
            user.getUsername(),
            user.getPassword(),
            authorities,
            user // Ajout de l'objet User en tant que cinquième argument
    );
  }


  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return authorities;
  }

  public Long getId() {
    return id;
  }


  @Override
  public String getPassword() {
    return password;
  }

  @Override
  public String getUsername() {
    return username;
  }

  @Override
  public boolean isAccountNonExpired() {
    return true;
  }

  @Override
  public boolean isAccountNonLocked() {
    return true;
  }

  @Override
  public boolean isCredentialsNonExpired() {
    return true;
  }

  @Override
  public boolean isEnabled() {
    return true;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o)
      return true;
    if (o == null || getClass() != o.getClass())
      return false;
    UserDetailsImpl user = (UserDetailsImpl) o;
    return Objects.equals(id, user.id);
  }
}
