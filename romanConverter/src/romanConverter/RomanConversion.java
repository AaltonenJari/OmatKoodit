package romanConverter;

import java.util.Hashtable;
/*
 * Class containing methods to convert Arabic numbers to Roman and vice versa.
 * The class contains also methods to check if numbers are valid.
 * The class contains a main function that is invoked from command line.
 * The main takes an argument to be converted.
 */
public class RomanConversion {
    private final int MIN_VALUE = 1;
    private final int MAX_VALUE = 3000;
    private final String ROMAN_REGEX = "^(M{0,4})(CM|CD|D?C{0,3})(XC|XL|L?X{0,3})(IX|IV|V?I{0,3})$";
    private final String[] RN_1000 = {"", "M", "MM", "MMM"};
    private final String[] RN_100 = {"", "C", "CC", "CCC", "CD", "D", "DC", "DCC", "DCCC", "CM"};
    private final String[] RN_10 = {"", "X", "XX", "XXX", "XL", "L", "LX", "LXX", "LXXX", "XC"};
    private final String[] RN_1 = {"", "I", "II", "III", "IV", "V", "VI", "VII", "VIII", "IX"};

    public RomanConversion() {
    }

    /*
     * main program of the number converter.
     * It takes an argument that is number to converted.
     * If the argument is recognized to a valid positive integer, it is converted to Roman.
     * If the argument is not recognized an integer, it is taken as a string.
     * A string argument is checked if it represents a valid Roman number.
     * If it is a valid Roman number, it is converted to a Arabic number.
     * The result of the conversion is shown on the console output.
     */
    public static void main(String[] args) {
        RomanConversion conv = new RomanConversion();

        if (args.length > 0) {
            try {
                int firstIntArg = Integer.parseInt(args[0]);
                if (conv.checArabic(firstIntArg)) {
                    String roman = conv.arabicToRoman(firstIntArg);
                    System.out.println("An integer " + firstIntArg + " has a Roman value of " + roman);
                } else {
                    System.err.println(args[0] + " is not a valid number to convert.");
                }
            } catch (NumberFormatException e) {
                if (conv.checkRoman(args[0])) {
                    int arabic = conv.romanToArabic(args[0]);
                    System.out.println("A Roman value " + args[0] + " has an integer value of " + arabic);
                } else {
                    System.err.println(args[0] + " is not a valid roman number");
                }
            }
        } else {
            System.out.println("Give a string representing a Roman number or an integer between " + conv.MIN_VALUE + " and " + conv.MAX_VALUE);
        }
    }

    /*
     * Method checks if argument "arabic" is between 1 and max_value (say: 3000).
     */
    public boolean checArabic(int arabic) {
        if (arabic >= MIN_VALUE && arabic <= MAX_VALUE) {
            return true;
        }
        return false;
    }

    /*
     * Method converts a valid Arabic integer number to Roman
     */
    public String arabicToRoman(int arabic) {
        String result = "";
        int units = arabic%10;
        int tens = (arabic%100)/10;
        int hundreds = (arabic%1000)/100;
        int thousands = (arabic%10000)/1000;

        result = RN_1000[thousands] + RN_100[hundreds] + RN_10[tens] + RN_1[units];
        return result;
    }

    /*
     * Method checks if argument "roman" represents a valid Roman number.
     * Argument is checked against a regular expression.
     * See http://stackoverflow.com/questions/267399/how-do-you-match-only-valid-roman-numerals-with-a-regular-expression
     * for the documentation.
     */
    public boolean checkRoman(String roman) {
        if (roman.matches(ROMAN_REGEX))
            return true;

        return false;
    }

    /*
     * Method converts a valid Roman number into Arabic
     */
    public int romanToArabic (String roman) {
        int result = 0;
        int numRomanValue = 0;
        int prevNumRomanValue = 0;

        Hashtable<Character, Integer> ht = new Hashtable<Character, Integer>();
        ht.put('I',1);
        ht.put('X',10);
        ht.put('C',100);
        ht.put('M',1000);
        ht.put('V',5);
        ht.put('L',50);
        ht.put('D',500);

        for (int i = roman.length()-1; i >= 0; i--){
            numRomanValue = ht.get(roman.charAt(i));
            if(numRomanValue < result && numRomanValue != prevNumRomanValue ){
                result -= numRomanValue;
            } else {
                result += numRomanValue;
            }
            prevNumRomanValue = numRomanValue;
        }
        return result;
    }
}
