package poly.edu.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import poly.edu.entity.Charge;

@Repository
public interface ChargeRepository extends JpaRepository<Charge, Integer> {
    @Query(value = "SELECT * FROM Charge c WHERE c.userID = :userId", nativeQuery = true)
    List<Charge> findByUser(@Param("userId") Integer userId);

    @Query(value = "SELECT * FROM Charge c WHERE userID= ?1 and ID=?2", nativeQuery = true)
    Charge findChargeByUser(int userId, int ID);
}
