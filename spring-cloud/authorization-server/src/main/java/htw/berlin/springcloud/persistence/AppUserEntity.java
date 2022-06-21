package htw.berlin.springcloud.persistence;

import javax.persistence.*;

@Entity
@Table(name = "users")
public class AppUserEntity {
    @Id
    @GeneratedValue
    private int id;

    @Version
    private int version;

    private int userId;

    private String email;

    private String password;

    private String role;
    private boolean enabled = false;

    public AppUserEntity() {
    }

    public AppUserEntity(int userId, String email, String password, String role, boolean enabled) {
        this.userId = userId;
        this.email = email;
        this.password = password;
        this.role = role;
        this.enabled = enabled;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
}