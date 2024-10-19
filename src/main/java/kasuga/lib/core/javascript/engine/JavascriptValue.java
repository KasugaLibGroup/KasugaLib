package kasuga.lib.core.javascript.engine;

import kasuga.lib.core.client.frontend.dom.DomContext;

public interface JavascriptValue {
    public boolean isString();
    public String asString();

    <T> T as(Class<T> className);

    boolean canExecute();

    JavascriptValue execute(Object ...objects);
    void executeVoid(Object ...objects);

    void pin();

    boolean hasMember(String render);

    JavascriptValue getMember(String render);

    JavascriptValue invokeMember(String memberName, Object ...objects);

    boolean isNumber();

    int asInt();
}
