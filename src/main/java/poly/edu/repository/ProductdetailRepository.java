package poly.edu.repository;



import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;


import poly.edu.entity.Productdetail;

@Repository
public interface ProductdetailRepository extends JpaRepository<Productdetail, Integer> {
    
    @Query(value="Select * from Productdetail where productID = ?1 ", nativeQuery = true)
    List<Productdetail> findProductdetailByProductID(int productID);

    @Query(value="Select * from Productdetail where HistoryID = ?1 ", nativeQuery = true)
    List<Productdetail> findProductdetailByHistoryID(int historyID);
}
