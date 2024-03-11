package kasuga.lib.codes.logic.data.functions;

import kasuga.lib.codes.logic.infrastructure.LogicalData;

public abstract class LogicFunction implements LogicalData {

    @Override
    public abstract LogicFunction clone();
}
