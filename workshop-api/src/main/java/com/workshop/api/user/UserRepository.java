/* (C) 2023 */
package com.workshop.api.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<UserModel, String> {
  long countByFirstName(String firstName);

  long countByLastName(String firstName);
}
