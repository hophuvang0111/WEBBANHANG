package poly.edu.entity;





import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;



@Entity
@Table(name = "[User]")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)

    @Column(name = "ID")
    private Integer ID;

    @Column(name = "username")
    private String username;

    @Column(name = "password")
    private String password;

    
    @Column(name = "balance")
    private Integer balance;

    @Column(name = "role")
    private String role;
    @Column(name = "active")
    private Boolean active;

    public User() {
    }

    public User(Integer iD, String username, String password, Integer balance, String role, Boolean active) {
        ID = iD;
        this.username = username;
        this.password = password;
        this.balance = balance;
        this.role = role;
        this.active = active;
    }

    public Integer getID() {
        return ID;
    }

    public void setID(Integer iD) {
        ID = iD;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Integer getBalance() {
        return balance;
    }

    public void setBalance(Integer balance) {
        this.balance = balance;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }
}
