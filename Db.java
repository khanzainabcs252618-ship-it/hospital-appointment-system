package app;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import jakarta.servlet.http.HttpSession;
import java.nio.file.*;
import java.util.*;

public class Db {
    public static final Gson G = new Gson();
    static final Path F = Paths.get(System.getProperty("java.io.tmpdir"), "carelink.json");
    static Map<String, List<Map<String, String>>> d;
    static int seq = 0;

    public static Map<String, String> row(String... kv) {
        Map<String, String> m = new LinkedHashMap<>();
        for (int i = 0; i < kv.length; i += 2) m.put(kv[i], kv[i + 1]);
        return m;
    }

    public static Map<String, String> user(String id, String role, String name, String email, String pass, String spec) {
        return row("id", id, "role", role, "name", name, "email", email, "pass", pass, "phone", "", "spec", spec, "fee", "300", "course", "");
    }

    public static synchronized List<Map<String, String>> t(String n) {
        if (d == null) {
            try {
                d = G.fromJson(Files.readString(F), new TypeToken<Map<String, List<Map<String, String>>>>() {}.getType());
            } catch (Exception e) {
                d = new HashMap<>();
            }
            for (String k : new String[]{"users", "appts", "feedback"}) d.putIfAbsent(k, new ArrayList<>());
            if (d.get("users").isEmpty()) seed();
        }
        return d.get(n);
    }

    static void seed() {
        d.get("users").add(user("1", "admin", "Administrator", "admin@carelink.com", "admin123", ""));
        d.get("users").add(user("2", "doctor", "Dr. Meera Rao", "meera@carelink.com", "doctor123", "Cardiology"));
        d.get("users").get(1).put("fee", "600");
        d.get("users").add(user("3", "doctor", "Dr. Arjun Shah", "arjun@carelink.com", "doctor123", "General Physician"));
        d.get("users").add(user("4", "patient", "Demo Patient", "patient@carelink.com", "patient123", ""));
        save();
    }

    public static synchronized void save() {
        try {
            Files.writeString(F, G.toJson(d));
        } catch (Exception e) {
        }
    }

    public static synchronized String id() {
        return System.currentTimeMillis() + "" + (seq++ % 10);
    }

    public static Map<String, String> find(String table, String key, String val) {
        if (val == null) return null;
        for (Map<String, String> r : t(table)) if (val.equals(r.get(key))) return r;
        return null;
    }

    public static Map<String, String> me(HttpSession s) {
        Object i = s.getAttribute("uid");
        return i == null ? null : find("users", "id", "" + i);
    }

    public static String name(String uid) {
        Map<String, String> u = find("users", "id", uid);
        return u == null ? "Unknown" : u.get("name");
    }

    public static String h(String s) {
        return s == null ? "" : s.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;").replace("\"", "&quot;").replace("'", "&#39;");
    }

    public static int num(String s, int def) {
        try {
            return Integer.parseInt(s.trim());
        } catch (Exception e) {
            return def;
        }
    }

}
