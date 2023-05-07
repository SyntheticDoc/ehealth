package ehealth.group1.backend.generators;

import java.util.concurrent.ThreadLocalRandom;

public class IDStringGenerator {

    public static String getNewIDString() {
        StringBuilder completeID = new StringBuilder();
        char[] chars = new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};

        for(int i = 0; i < 64; i++) {
            completeID.append(chars[ThreadLocalRandom.current().nextInt(0, chars.length)]);
        }

        return completeID.toString();
    }

    /*public static String getNewIDString() {
        long id1 = ThreadLocalRandom.current().nextLong();
        long id2 = ThreadLocalRandom.current().nextLong();
        long id3 = ThreadLocalRandom.current().nextLong();
        long id4 = ThreadLocalRandom.current().nextLong();
        String completeID = Long.toHexString(id1) + Long.toHexString(id2) + Long.toHexString(id3) + Long.toHexString(id4);

        while(!isUnused(completeID)) {
            id1 = ThreadLocalRandom.current().nextLong();
            id2 = ThreadLocalRandom.current().nextLong();
            id3 = ThreadLocalRandom.current().nextLong();
            id4 = ThreadLocalRandom.current().nextLong();
            completeID = Long.toHexString(id1) + Long.toHexString(id2) + Long.toHexString(id3) + Long.toHexString(id4);
        }

        return completeID;
    }*/

    // Verifies if the id generated in getNewIDString is already in use (= if it already exists in the database)
    private static boolean isUnused(String id) {
        // TODO: Verify in database if this id is still unused
        return true;
    }
}
