package ehealth.group1.backend.helper.mock;

import ehealth.group1.backend.enums.ECGSTATE;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.invoke.MethodHandles;

public class RingBuffer {
    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    ECGSTATE[] memory;
    int memPointer = 0;

    public RingBuffer(int capacity) {
        if(capacity <= 2 || capacity > 128) {
            throw new IllegalArgumentException("Error while creating RingBuffer. Capacity must be > 2 and <= 128, but was " +
                    capacity + "!");
        }

        memory = new ECGSTATE[capacity];
    }

    public int getMemSize() {
        return memory.length;
    }

    public void memPut(ECGSTATE value, int index) {
        if(index < 0 || index >= memory.length) {
            LOGGER.error("Index {} out of bound for memory size {}!", index, memory.length);
            return;
        }

        memory[index] = value;
    }

    public ECGSTATE getNext() {
        ECGSTATE result = memory[memPointer];

        memPointer++;

        if(memPointer >= memory.length) {
            memPointer = 0;
        }

        return result;
    }

    public void resetPointer() {
        memPointer = 0;
    }
}
