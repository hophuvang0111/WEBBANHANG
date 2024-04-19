package poly.edu.repository;



import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import poly.edu.entity.History;




@Repository
public interface HistoryRepository extends JpaRepository<History, Integer> {

    @Query( value = "select * from History where UserID = ?", nativeQuery = true)
    List<History> findHistoryByUserID(Integer userID);
}

