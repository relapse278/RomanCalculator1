class Converter {
    private final char[] romans;
    private final char[] repeatableRomans;
    private final int[] decimals;

    public Converter() {
        this.romans = new char[]  {'C', 'L', 'X', 'V', 'I'};
        this.decimals = new int[] { 100, 50,  10,  5,   1};
        this.repeatableRomans = new char[] {'I', 'X', 'C'};
    }

    public void verifyRoman (String roman) throws CalcException {
        String regex = "(?<=(.))(?!\\1)";
        String[] parts = roman.split(regex);

        checkOrder(parts);
        checkNumberOfRepetitions(parts);
        checkRepeatedFiguresAreRepeatable(parts);
        checkSubtractiveNotation(parts);
    }

    private void checkNumberOfRepetitions(String[] parts) throws CalcException {
        for (String s: parts) {
            if (s.length() > 3)
                throw new CalcException("Количество повторений в римском числе более 3!");
        }
    }

    private void checkRepeatedFiguresAreRepeatable(String[] parts) throws CalcException {
        for (String s: parts) {
            if (s.length() > 1 && !isRepeatable(s.charAt(0)))
                throw new CalcException("Римское число содержит цифры, недопустимые для повторения!");
        }
    }

    public boolean checkHasRomanChars(String s) {
        String romansString = new String(romans);

        char[] chars = s.toCharArray();

        for (char c : chars) {
            if(romansString.indexOf(c) == -1) {
                return false;
            }
        }

        return true;
    }

    private void checkOrder(String[] parts) throws CalcException {
        char[] checker = new char[parts.length];

        for (int i = 0; i < parts.length; i++)
            checker[i] = parts[i].charAt(0);

        for (int j = 0; j < checker.length - 1; j++) {
            if (getIndexOfRoman(checker[j]) > getIndexOfRoman(checker[j+1])) {
                char temp = checker[j];
                checker[j] = checker[j + 1];
                checker[j + 1] = temp;
            }
        }

        for(int k = 0; k < checker.length - 1; k++) {
            if (getIndexOfRoman(checker[k]) >= getIndexOfRoman(checker[k + 1]))
                throw new CalcException("Порядок разрядности цифр в римском числе неправилен!");
        }
    }

    private void checkSubtractiveNotation(String[] parts) throws CalcException {
        for (int i = 0; i < parts.length - 1; i++) {
            if (isRepeatable(parts[i].charAt(0)) && isSubtractiveNotation(parts, i)) {
                if (i + 1 < parts.length) {
                    if (getIndexOfRoman(parts[i].charAt(0)) - getIndexOfRoman(parts[i + 1].charAt(0)) > 2)
                        throw new CalcException("Цифры римского числа нарушают правило вычитания!");
                    if (parts[i].length() > 1)
                        throw new CalcException("Правило вычитания исключает повторение вычитаемых цифр!");
                }
            }
        }
    }

    private boolean isSubtractiveNotation(String[] parts, int index) {
        if (index + 1 < parts.length)
            return getIndexOfRoman(parts[index + 1].charAt(0)) < getIndexOfRoman(parts[index].charAt(0));

        return false;
    }

    private boolean isRepeatable (char c) {
        for (char ch : repeatableRomans) {
            if (c == ch)
                return true;
        }
        return false;
    }

    private int getIndexOfRoman (char c) {
        for (int i = 0; i < romans.length; i++)
            if (c == romans[i])
                return i;

        return -1;
    }

    public int getDecimal(String roman) {
        char[] charArray = roman.toCharArray();

        int temp = 0;
        int i = 0;

        while (i < charArray.length) {
            int indexThisDigit = getIndexOfRoman(charArray[i]);
            if (i < charArray.length - 1) {
                int indexNextDigit = getIndexOfRoman(charArray[i + 1]);

                if (indexThisDigit > indexNextDigit) {
                    temp += decimals[indexNextDigit] - decimals[indexThisDigit];
                    i += 2;
                    continue;
                }
            }
            temp += decimals[indexThisDigit];
            i++;
        }

        return temp;
    }

    public String getRoman(int arabic) {
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < romans.length; i++) {
            char roman = romans[i];
            int decimal = decimals[i];

            int modulo = arabic % decimal;

            if (modulo == arabic) // e.g. 99 % 100 = 99
                continue;

            int division = arabic / decimal;

            for (int j = 0; j < division; j++)
                sb.append(roman);

            arabic %= decimal;
        }

        String result = normalizeRoman(sb);

        return result;
    }

    private String normalizeRoman (StringBuilder sb) {
        StringBuilder sbResult = new StringBuilder();

        String regex = "(?<=(.))(?!\\1)";
        String[] parts = sb.toString().split(regex);
        int i = parts.length - 1;

        while(i >= 0){
            if (parts[i].length() == 4) {
                if (i == 0) { // 4 oder 40
                    sbResult.insert(0, convertFour(parts[i].charAt(0)));
                    i--;
                    continue;
                }
                else {
                    char actualRoman = parts[i].charAt(0);
                    char previousRoman = parts[i-1].charAt(parts[i-1].length()-1);
                    int indexActualRoman = romanArrayIndex(actualRoman);
                    int indexPreviousRoman = romanArrayIndex(previousRoman);

                    if (indexActualRoman - indexPreviousRoman >= 2) {
                        sbResult.insert(0, convertFour(actualRoman));
                        i--;
                        continue;
                    }
                    else {
                        sbResult.insert(0, convertNine(actualRoman));
                        i-=2;
                        continue;
                    }
                }
            }
            sbResult.insert(0, parts[i]);
            i--;
        }

        return sbResult.toString();
    }

    private String convertFour(char repeatedChar) {
        StringBuilder sb = new StringBuilder();
        sb.append(repeatedChar);
        sb.append(romans[romanArrayIndex(repeatedChar) - 1]);

        return sb.toString();
    }

    private String convertNine(char repeatedChar) {
        StringBuilder sb = new StringBuilder();
        sb.append(repeatedChar);
        sb.append(romans[romanArrayIndex(repeatedChar) - 2]);

        return sb.toString();
    }

    private int romanArrayIndex(char ch) {
        for (int i = 0; i < romans.length; i++) {
            if (ch == romans[i])
                return i;
        }
        return -1;
    }
}