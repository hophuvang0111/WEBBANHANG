package poly.edu.entity;



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
@Table(name = "Productdetail")
public class Productdetail {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)

    @Column(name = "ID")
    private Integer ID;

    @ManyToOne
    @JoinColumn(name = "HistoryID")
    private History history;

    @Column(name = "email")
    private String email;

    @Column(name = "password")
    private String password;

    @Column(name = "emailrecovery")
    private String emailrecovery;

    @Column(name = "IP")
    private String IP;

    @ManyToOne
    @JoinColumn(name = "productID")
    private Product product;

    @Column(name = "status")
    private Boolean status;

    @Column(name = "local")
    private String local;

    @Column(name = "os")
    private String os;
}
