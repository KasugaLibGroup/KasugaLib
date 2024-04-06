package kasuga.lib.registrations.create;

import com.simibubi.create.content.trains.bogey.AbstractBogeyBlock;
import kasuga.lib.registrations.common.BlockReg;

public class BogeyReg<T extends AbstractBogeyBlock<?>> extends BlockReg<T> {
    /**
     * Use this to create a BlockReg.
     *
     * @param registrationKey your block registration key.
     */
    public BogeyReg(String registrationKey) {
        super(registrationKey);
    }
}
