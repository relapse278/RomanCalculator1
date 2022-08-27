import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws CalcException {
        Scanner scanner = new Scanner(System.in);
        String expression = scanner.nextLine();
        System.out.println(calc(expression));
    }

    public static String calc (String input) throws CalcException {
        String expression = input.toUpperCase();
        Converter converter = new Converter();
        String[] expressionParts = expression.split(" ");

        if (expressionParts.length != 3)
            throw new CalcException ("Формат математической операции не удовлетворяет заданию - два операнда и один оператор (+, -, /, *)");

        String operand1String = expressionParts[0];
        String operator = expressionParts[1];
        String operand2String = expressionParts[2];

        boolean operand1IsArabic = false;
        boolean operand2IsArabic = false;
        boolean operand1IsRoman = false;
        boolean operand2IsRoman = false;

        int operand1Int = 0;
        int operand2Int = 0;

        try {
            operand1Int = Integer.parseInt(operand1String);
            if (operand1String.charAt(0) == '0')
                throw new CalcException("Арабское число имеет ноль/нули в начале!");

            operand1IsArabic = true;

            operand2Int = Integer.parseInt(operand2String);
            if (operand2String.charAt(0) == '0')
                throw new CalcException("Арабское число имеет ноль/нули в начале!");

            operand2IsArabic = true;
        }
        catch (NumberFormatException nfe) {
            operand1IsRoman = converter.checkHasRomanChars(operand1String);
            operand2IsRoman = converter.checkHasRomanChars(operand2String);
        }

        if ((operand1IsArabic & operand2IsArabic) == false && (operand1IsRoman & operand2IsRoman) == false)  // i + 1 -> Римское число содержит недопустимые символы!
            throw new CalcException("Калькулятор может считать только с арабскими или римскими цифрами одновременно!");

        if (operand1IsRoman && operand2IsRoman) {
            converter.verifyRoman(operand1String);
            converter.verifyRoman(operand2String);
            operand1Int = converter.getDecimal(operand1String);
            operand2Int = converter.getDecimal(operand2String);
        }

        if (operand1Int < 1 || operand1Int > 10 || operand2Int < 1 || operand2Int > 10 )
            throw new CalcException("Введённые числа не входят в допустимый диапазон!");

        int result;

        switch (operator) {
            case "+":
                result = operand1Int + operand2Int;
                break;
            case "-":
                result = operand1Int - operand2Int;
                break;
            case "*":
                result = operand1Int * operand2Int;
                break;
            case "/":
                result = operand1Int / operand2Int;
                break;
            default:
                throw new CalcException ("Неправильный оператор, допустимы лишь +, -, /, *");
        }

        if (operand1IsRoman & operand2IsRoman) {
            if (result < 1)
                throw new CalcException("Результатом работы калькулятора с римскими цифрами могут быть только положительные числа больше нуля!");

            return converter.getRoman(result);
        }

        return Integer.toString(result);
    }
}
