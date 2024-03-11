package kasuga.lib.core.xml;

import com.google.common.collect.Lists;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class XmlProcessor {
    public static IXmlObject<?> decodeAndPull(InputStream stream) {
        String text = new BufferedReader(new InputStreamReader(stream)).lines().collect(Collectors.joining(System.lineSeparator()));
        List<IXmlObject<?>> result = decode(text);
        if(result.size() == 1) return result.get(0);
        if(result.size() == 0) return XmlCompound.empty();
        XmlCompound root = new XmlCompound("root");
        for(IXmlObject<?> obj : result) {root.setValue(obj.key(), obj);}
        return root;
    }

    public static String encode(IXmlObject<?> xmlObject) {
        return xmlObject.toString();
    }

    public static OutputStream encodeAndPush(IXmlObject<?> xmlObject) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        new BufferedWriter(new OutputStreamWriter(baos)).write(xmlObject.toString());
        return baos;
    }

    public static List<IXmlObject<?>> decode(String text) throws IllegalArgumentException {
        if(!verifySequence(text)) return List.of(XmlCompound.empty());
        int head = text.indexOf("<");
        int end = text.lastIndexOf(">");
        String cache = formattingSequence(text.substring(head, end + 1));
        String cacheHead = "";
        ArrayList<IXmlObject<?>> resultList = new ArrayList<>();
        if(cache.startsWith("<?")) {
            int idx2 = cache.indexOf("?>");
            if(idx2 < 2) throw new IllegalArgumentException();
            String heading = cache.substring(2, idx2);
            resultList.add(parseBasicTagElement(heading, "", true, false));
            cache = cache.substring(idx2 + 2);
        }
        while(!cache.equals("")) {
            if(cacheHead.equals("")) {
                int left = cache.indexOf("<");
                int right = cache.indexOf(">");
                if(cache.indexOf("<--") == left) {
                    cache = cache.substring(cache.indexOf("-->") + 3);
                    continue;
                }
                if (right > 0 && cache.charAt(right - 1) == '/')
                    resultList.add(parseBasicTagElement(cache.substring(left + 1, right - 1), "", true, false));
                else
                    cacheHead = cache.substring(left + 1, right);

                cache = cache.substring(right + 1);
            } else {
                String closeTag = getCloseTag(cacheHead);
                int pCount = 0;
                if(!cache.contains(closeTag)) throw new IllegalArgumentException();
                String cacheBody = "";
                while (true) {
                    cacheBody = cache.substring(0, cache.indexOf(closeTag, pCount));
                    if(countElements(cacheBody, closeTag.replaceAll("/", "")) == countElements(cacheBody, closeTag)) break;
                    pCount = cache.indexOf(closeTag, pCount) + 1;
                    if(pCount > cache.length()) throw new IllegalArgumentException();
                }
                if(!cacheBody.contains("<") && !cacheBody.contains(">")) {
                    if(cacheBody.length() < 1) resultList.add(parseBasicTagElement(cacheHead, "", false, false));
                    if(!cacheBody.replaceAll("[0-9]", "").replaceAll("\\.", "").equals("")) {
                        resultList.add(parseBasicTagElement(cacheHead, cacheBody, false, false));
                    } else {
                        resultList.add(parseBasicTagElement(cacheHead, cacheBody, false, true));
                    }
                } else {
                    boolean hasOuter = false, inQuote = false;
                    for(int i = 0; i < cacheBody.length(); i++) {
                        char c = cacheBody.charAt(i);
                        if(c == '"') {
                            inQuote = !inQuote;
                            continue;
                        }
                        if(c == '<' || c == '>' && !inQuote) {
                            hasOuter = true;
                            break;
                        }
                    }
                    if(hasOuter) {
                        resultList.add(parseCompoundTagElement(cacheHead, false, decode(cacheBody).toArray(new IXmlObject[0])));
                    }
                }
                int window_width = cacheBody.length() + closeTag.length();
                if(window_width > cache.length()) {break;}
                cache = cache.substring(window_width);
                cacheHead = "";
            }
        }
        return resultList;
    }

    // =======================================    INNER METHOD    =======================================  //

    private static int countElements(String parent, String element) {
        int count = 0, length = parent.length(), ele = element.length();
        if(length < element.length()) return 0;
        if(!parent.contains(element)) return 0;
        if(parent.indexOf(element) == parent.lastIndexOf(element)) return 1;
        boolean inQuote = false;
        for (int i = 0; i <= length - ele; i++) {
            if(parent.charAt(i) == '"') {
                inQuote = !inQuote;
                continue;
            }
            if(!inQuote) {
                String sub = parent.substring(i, i + ele);
                if(sub.equals(element)) count++;
            }
        }
        return count;
    }

    private static IXmlObject<?> parseBasicTagElement(String cacheHead, String value, boolean singleSide, boolean isNumber) throws IllegalArgumentException {
        List<String> elements = new ArrayList<>();
        elements.add("");
        boolean inQuote = false;
        for(int i = 0; i < cacheHead.length(); i++) {
            char c = cacheHead.charAt(i);
            if(c == '"') {inQuote =! inQuote;}
            if(!inQuote && c == ' ') {
                    elements.add("");
                    continue;
            }
            elements.set(elements.size() - 1, elements.get(elements.size() - 1) + c);
        }
        while (elements.contains("=")) {
            int index = elements.indexOf("=");
            if(index == 0 || index > elements.size() - 1) throw new IllegalArgumentException();
            elements.set(index, elements.get(index - 1) + "=" + elements.get(index + 1));
            elements.set(index - 1, "");
            elements.set(index + 1, "");
        }
        int length = elements.size();
        while(true) {
            elements.remove("");
            if(length == elements.size()) break;
            length = elements.size();
        }
        if(elements.size() < 1) throw new IllegalArgumentException();
        if(elements.get(0).contains("=")) throw new IllegalArgumentException();
        if(isNumber)
            return new XmlNumber(elements.get(0), Double.parseDouble(value), singleSide, parseAttributes(elements).toArray(new IXmlObject[0]));
        return new XmlString(elements.get(0), value, singleSide, parseAttributes(elements).toArray(new IXmlObject[0]));
    }

    private static IXmlObject<?> parseCompoundTagElement(String cacheHead, boolean singleSide, IXmlObject<?>... values) throws IllegalArgumentException {
        IXmlObject<?> obj = parseBasicTagElement(cacheHead, "", singleSide, false);
        XmlCompound compound = new XmlCompound(obj.key(), obj.attributes().toArray(new IXmlObject[0]));
        for(IXmlObject<?> xml : values) {
            compound.setValue(xml.key(), xml);
        }
        return compound;
    }

    private static List<IXmlObject<?>> parseAttributes(List<String> elements) throws IllegalArgumentException{
        if(elements.size() < 2) return List.of();
        List<IXmlObject<?>> results = new ArrayList<>();
        for(String e : elements.subList(1, elements.size())) {
            if(!e.contains("\"")) {
                String[] k_v = e.split("=");
                if(k_v.length != 2) throw new IllegalArgumentException();
                results.add(new XmlNumber(k_v[0], Double.parseDouble(k_v[1])));
            } else {
                int frontQuote = e.indexOf('"');
                int equ = e.indexOf('=');
                if(equ < 0 || frontQuote < equ) throw new IllegalArgumentException();
                String[] k_v = e.split("=");
                if(k_v.length != 2) throw new IllegalArgumentException();
                results.add(new XmlString(k_v[0], removeLeftAndRightQuote(k_v[1])));
            }
        }
        return results;
    }

    private static String getCloseTag(String cacheHead) throws IllegalArgumentException {
        List<String> tags = new ArrayList<>(List.of(cacheHead.split(" ")));
        int length = tags.size();
        while (true) {
            tags.remove(" ");
            if(tags.size() == length) break;
            length = tags.size();
        }
        if(tags.size() < 1) throw new IllegalArgumentException();
        if(tags.get(0).contains("=") || tags.get(0).contains("\"")) throw new IllegalArgumentException();
        return "</" + tags.get(0) + ">";
    }

    private static String removeLeftAndRightQuote(String str) throws IllegalArgumentException{
        if(!str.contains("\"")) return str;
        int count = 0;
        for(char c : str.toCharArray()) {
            if(c == '"') count++;
        }
        if(count == 1 && (str.startsWith("\"") || str.endsWith("\""))) throw new IllegalArgumentException();
        if(str.startsWith("\"") && str.endsWith("\"")) return str.substring(1, str.length() - 1);
        return str;
    }

    private static String formattingSequence(String cache) throws IllegalArgumentException {
        StringBuilder builder = new StringBuilder();
        boolean iQ = false, iB = false;
        for(int i = 0; i < cache.length(); i++) {
            char c = cache.charAt(i);
            if(!iQ && c == '<') {iB = true;builder.append(c);continue;}
            if(!iQ && c == '>') {iB = false;builder.append(c);continue;}
            if(c == '"') iQ =! iQ;
            if(!iQ && iB && (c == '\n' || c == '\r' || c == '\t')) {continue;}
            if(!iQ && iB && c == ' ') {
                if(i > 0 && (cache.charAt(i - 1) == '<' || cache.charAt(i - 1) == ' ')) continue;
                if(i < cache.length() - 1 && (cache.charAt(i + 1) == '>' || cache.charAt(i + 1) == ' ')) continue;
            }
            builder.append(c);
        }
        return builder.toString();
    }

    private static boolean verifySequence(String text) {
        int length = text.length();
        if(length == 0) return false;
        int head = text.indexOf("<");
        int end = text.lastIndexOf(">");
        int lashHead = text.lastIndexOf("<");
        int firstEnd = text.indexOf(">");
        if(head == -1 || firstEnd < head) return false;
        return end != -1 && end >= head && lashHead <= end;
    }
}
