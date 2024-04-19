package poly.edu.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


import poly.edu.entity.Product;

@Repository
public interface ProductRepository extends JpaRepository<Product, Integer> {
    
}
