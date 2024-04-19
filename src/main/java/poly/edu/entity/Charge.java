package poly.edu.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
@Table(name = "Charge")
public class Charge {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)

    @Column(name = "ID")
    private Integer ID;

    @ManyToOne
    @JoinColumn(name = "userID")
    private User user;
    
    @Column(name = "amount")
    private Integer amount;

    @Column(name = "status")
    private String status;

    @Column(name = "transactiondate")
    private LocalDateTime transactiondate;
}
