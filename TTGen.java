import java.util.*;
public class BetterTruthTables {

    private final ArrayList<Character> vars;
    private final String prop;

    ArrayList<ArrayList<Boolean>> allPerms = new ArrayList<>();
    ArrayList<Boolean> currentPerm = new ArrayList<>();

    BetterTruthTables(String prop) {
        this.prop = prop;
        vars = new ArrayList<>();
        for (char c : prop.toCharArray()) {
            String ops = "~^v>=";
            if (ops.indexOf(c) == -1 && !vars.contains(c) && !Character.isSpaceChar(c)) {
                if (Character.isLetter(c)) vars.add(c);
                else if (!(c == '(' || c == ')')) {
                    System.out.println("Invalid characters!");
                    System.exit(1);
                }
            }
        }
    }

    public static void main(String[] argus) {
        Scanner sc = new Scanner(System.in);
        System.out.println("AND: ^\nOR: v\nNEGATION: ~\nIMPLIES: >\nBICONDITIONAL: =");
        System.out.print("Please enter the proposition: ");
        String prop = sc.nextLine();
        BetterTruthTables tt = new BetterTruthTables(prop);
        tt.getTableEntries();
    }

    public void getTableEntries() {
        System.out.print(" ");
        for (char c : vars) System.out.print(c + "   ");
        System.out.println("\t*  ");

        for (int i = 1; i <= 7 + vars.size() * 4; i++) System.out.print("-");
        System.out.println();

        getPerms(vars.size());
        StringBuilder results = new StringBuilder();
        for (ArrayList<Boolean> bools : allPerms) {
            makeTable(bools, subStringEval(prop, bools, 0));
            results.append(subStringEval(prop, bools, 0) ? 1 : 0);
        }
        System.out.print("\nResult: ");
        if (results.toString().indexOf('0') == -1) System.out.println("Tautology");
        else if (results.toString().indexOf('1') == -1) System.out.println("Contradiction");
        else System.out.println("Contingency");
    }

    public void makeTable(ArrayList<Boolean> values, boolean result) {
        System.out.print(" ");
        for (boolean b : values) System.out.print((b ? 'T' : 'F') + "   ");
        System.out.println("\t" + (result ? 'T' : 'F') + "  ");
    }

    public boolean doOps(boolean b1, boolean b2, char op) {
        return switch (op) {
            case '^' -> b1 && b2;
            case 'v' -> b1 || b2;
            case '>' -> !(b1 && !b2);
            case '=' -> !((b1 && !b2) || (!b1 && b2));
            default -> true;
        };
    }

    public int getNextParIndex(int index, ArrayList<Character> sub_chars) {
        int count = 0;
        for (int i = index; i < sub_chars.size(); i++) {
            if (sub_chars.get(i) == '(') count++;
            if (sub_chars.get(i) == ')') count--;
            if (count == 0 && i != index) return i;
        }
        return -1;
    }

    public void getPerms(int len) {
        if (currentPerm.size() == len) {
            allPerms.add(new ArrayList<>(currentPerm));
            return;
        }
        for (int i = 0; i < 2; i++) {
            currentPerm.add(i == 0);
            getPerms(len);
            currentPerm.removeLast();
        }
    }

    public String getSubString (List<Character> sublist) {
        StringBuilder s = new StringBuilder();
        for (char c : sublist) s.append(c);
        return s.toString();
    }

    public boolean subStringEval(String sub, ArrayList<Boolean> vals, int rec) {
        ArrayList<Character> sub_chars = new ArrayList<>();

        for (char c : sub.toCharArray()) {
            if (!Character.isSpaceChar(c)) sub_chars.add(c);
        }

        boolean isNeg = false, isOp = false, b1 = false, b2 = false, value = false;
        char op = ' ';
        String par_substring = "";

        for (int i = 0; i < sub_chars.size(); i++) {
            char c = sub_chars.get(i);

            if (c == '~') isNeg = !isNeg;

            if (vars.contains(c)) {
                if (isOp) {
                    b2 = vals.get(vars.indexOf(c));
                    if (isNeg) { b2 = !b2; isNeg = false; }
                } else {
                    b1 = vals.get(vars.indexOf(c));
                    if (isNeg) { b1 = !b1; isNeg = false; }
                }
                isOp = !isOp;
            }

            if (c == '(') {
                par_substring = getSubString(sub_chars.subList(i + 1, getNextParIndex(i, sub_chars)));

                if (isOp) {
                    b2 = subStringEval(par_substring, vals, rec + 1);
                    if (isNeg) { b2 = !b2; isNeg = false; }
                } else {
                    b1 = subStringEval(par_substring, vals, rec + 1);
                    if (isNeg) { b1 = !b1; isNeg = false; }
                }
                isOp = !isOp;
                i = getNextParIndex(i, sub_chars);
                c = sub_chars.get(i);
            }

            if ("v^>=".indexOf(c) != -1) op = c;
            if (!isOp && op != ' ') {
                value = doOps(b1, b2, op);
                b1 = value;
                op = ' ';
                isOp = true;
            }
        }
        return b1;
    }
}
