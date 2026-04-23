package com.sonora.sonora.repository;

import com.sonora.sonora.model.entity.User;
import com.sonora.sonora.model.enums.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/** Repository for user entities. */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Finds a user by email.
     *
     * @param email user email
     * @return optional user
     */
    Optional<User> findByEmail(String email);

    /**
     * Checks whether a user exists for the email.
     *
     * @param email user email
     * @return true when user exists
     */
    boolean existsByEmail(String email);

    /**
     * Finds users having the given role.
     *
     * @param role role to filter
     * @return users with that role
     */
    List<User> findByRole(Role role);

    /**
     * Counts users matching a role.
     *
     * @param role role to count
     * @return number of users with that role
     */
    long countByRole(Role role);
}
