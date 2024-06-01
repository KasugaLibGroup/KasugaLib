package kasuga.lib.core.client.gui.commands;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;

public class SimpleStringParser implements ArgumentType<String> {
    public SimpleStringParser() {
    }

    @Override
    public String parse(StringReader reader){
        final int start = reader.getCursor();
        while (reader.getString().charAt(reader.getCursor()) != ' ') {
            reader.skip();
        }
        return reader.getString().substring(start, reader.getCursor());
    }
}
