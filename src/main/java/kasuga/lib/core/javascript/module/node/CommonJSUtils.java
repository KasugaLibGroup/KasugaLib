package kasuga.lib.core.javascript.module.node;

import kasuga.lib.core.util.MultipleReader;

import java.io.Reader;
import java.io.StringReader;

public class CommonJSUtils {
    public static Reader transform(Reader input){
        StringReader headReader = new StringReader("(function(exports, {require}, module, __filename, __dirname) {");
        StringReader tailReader = new StringReader(";});");
        return new MultipleReader(headReader,input,tailReader);
    }
}
