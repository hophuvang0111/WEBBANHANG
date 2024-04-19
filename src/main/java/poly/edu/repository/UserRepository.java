package poly.edu.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import poly.edu.entity.User;



@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
    
    User findByUsername(String username);
}
