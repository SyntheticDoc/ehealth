package ehealth.group1.backend.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@NoArgsConstructor
@Getter @Setter
public class Argon2Parameters {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String type;

    private String argonType;

    private int saltLength;
    private int parallelism;
    private long memoryCost;
    private int hashLength;
    private int iterations;

    // Copy constructor
    public Argon2Parameters(Argon2Parameters source) {
        this.argonType = source.argonType;
        this.saltLength = source.saltLength;
        this.parallelism = source.parallelism;
        this.memoryCost = source.memoryCost;
        this.hashLength = source.hashLength;
        this.iterations = source.iterations;
    }

    @Override
    public String toString() {
        return "Argon2Parameters["
                + "id=" + id
                + ", type='" + argonType + '\''
                + ", saltLength=" + saltLength
                + ", parallelism=" + parallelism
                + ", memoryCost=" + memoryCost
                + ", hashLength=" + hashLength
                + ", iterations=" + iterations
                + ']';
    }
}
