package ehealth.group1.backend.helper.argon2crypto;

import ehealth.group1.backend.entity.Argon2Parameters;
import org.bouncycastle.crypto.generators.Argon2BytesGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Primary;
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;
import org.springframework.stereotype.Component;

import java.lang.invoke.MethodHandles;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Base64;

@Component
@Primary
public class Argon2PasswordEncoderWithParams extends Argon2PasswordEncoder {
    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    public Argon2PasswordEncoderWithParams() {
        // Use default values from defaultsForSpringSecurity_v5_8() from Springs own Argon2PasswordEncoder
        super(16, 32, 1, 16384, 2);
    }

    public String encode(CharSequence rawPassword, Argon2Parameters params, String salt) {
        byte[] byteSalt = Base64.getDecoder().decode(salt);
        byte[] hash = new byte[params.getHashLength()];

        int argonType;

        switch (params.getArgonType().toLowerCase()) {
            case "argon2d":
                argonType = 0;
                break;
            case "argon2i":
                argonType = 1;
                break;
            case "argon2id":
                argonType = 2;
                break;
            default:
                throw new IllegalArgumentException("Argon2 type parameter " + params.getArgonType() + " not recognized!");
        }

        org.bouncycastle.crypto.params.Argon2Parameters newParams = new org.bouncycastle.crypto.params.Argon2Parameters
                .Builder(argonType)
                .withSalt(byteSalt)
                .withParallelism(params.getParallelism())
                .withMemoryAsKB((int) params.getMemoryCost())
                .withIterations(params.getIterations())
                .build();

        Instant start = Instant.now();

        Argon2BytesGenerator generator = new Argon2BytesGenerator();
        generator.init(newParams);
        generator.generateBytes(rawPassword.toString().toCharArray(), hash);

        Instant end = Instant.now();

        LOGGER.debug("Argon2 - hashed string in " + ChronoUnit.MILLIS.between(start, end) + " ms");


        return String.format("$%s$v=%d$m=%d,t=%d,p=%d$%s$%s", params.getArgonType().toLowerCase(), 19,
                (int) params.getMemoryCost(), params.getIterations(), params.getParallelism(), salt,
                Base64.getEncoder().encodeToString(hash));
    }
}
