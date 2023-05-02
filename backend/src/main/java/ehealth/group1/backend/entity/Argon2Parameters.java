package ehealth.group1.backend.entity;

import ehealth.group1.backend.repositories.Argon2ParametersRepository;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import org.springframework.beans.factory.annotation.Autowired;

@Entity
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

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getArgonType() {
        return argonType;
    }

    public void setArgonType(String type) {
        this.argonType = type;
    }

    public int getSaltLength() {
        return saltLength;
    }

    public void setSaltLength(int saltLength) {
        this.saltLength = saltLength;
    }

    public int getParallelism() {
        return parallelism;
    }

    public void setParallelism(int parallelism) {
        this.parallelism = parallelism;
    }

    public long getMemoryCost() {
        return memoryCost;
    }

    public void setMemoryCost(long memoryCost) {
        this.memoryCost = memoryCost;
    }

    public int getHashLength() {
        return hashLength;
    }

    public void setHashLength(int hashLength) {
        this.hashLength = hashLength;
    }

    public int getIterations() {
        return iterations;
    }

    public void setIterations(int iterations) {
        this.iterations = iterations;
    }

    public Argon2Parameters() {

    }

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
