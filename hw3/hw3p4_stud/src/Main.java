import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.*;

public class Main {
    public static void main(String[] args) throws IOException {
        String fileName = "test.txt";
        BufferedReader reader =
                new BufferedReader(new FileReader(fileName));

        ArrayList<String> text = new ArrayList<>();
        for (String line = reader.readLine(); line != null; line = reader.readLine()) {
            text.add(line);
        }

        ArrayList<ArrayList<String>> res = new ArrayList<>();

        int longest = 0;
        for (int i = 0; i < text.size(); i++) {
            ArrayList<String> sequence = new ArrayList<>();
            sequence.add(text.get(i));
            for (int j = i + 1; j < text.size(); j++) {
                if (text.get(j) != text.get(i)) {
                    if (compare(text.get(i), text.get(j))) {
                        sequence.add(text.get(j));
                    }
                }
            }
            if (sequence.size() > longest) {
                longest = sequence.size();
                res.add(sequence);
            }
        }

        // print out the longest sequence
        for (ArrayList<String> s : res) {
            if (s.size() == longest) {
                System.out.print(s.toString());
            }
        }
    }

    public static boolean compare(String s1, String s2) {
        int length = Math.min(s1.length(), s2.length());
        for (int i = 0; i < length; i++) {
            if (s1.charAt(i) > s2.charAt(i)) return false;
        }
        return true;
    }
}
