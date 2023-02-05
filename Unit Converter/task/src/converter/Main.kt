package converter

fun main() {
    do {
        print("Enter what you want to convert (or exit): ")
        val userInput = readln()
        if (userInput != "exit") convertUnit(userInput.lowercase())
    } while (userInput != "exit")
}

fun convertUnit(userInput: String) {
    val input = userInput.split(" ").filter { !Regex("degrees?").matches(it) }

    val inputValue = try {
        input[0].toDouble()
    } catch (e: NumberFormatException) {
        println("Parse error\n")
        return
    }

    val inputUnit = Unit.values().find { unit -> input[1] in unit.names }

    if (inputUnit != null && inputValue < 0 && inputUnit.type != UnitType.Temperature) {
        println("${inputUnit.type} shouldn't be negative\n")
        return
    }

    val targetUnit = Unit.values().find { unit -> input[3] in unit.names }

    if (inputUnit != null && targetUnit != null && inputUnit.type == targetUnit.type) {
        val targetValue = if (inputUnit.type == UnitType.Temperature) {
            inputUnit.convertTemperature(inputValue, targetUnit)
        } else {
            inputUnit.convertLengthOrWeight(inputValue, targetUnit)
        }

        val inputUnitName = inputUnit.getName(inputValue)
        val targetUnitName = targetUnit.getName(targetValue)

        println("$inputValue $inputUnitName is $targetValue $targetUnitName\n")
    } else {
        println("Conversion from ${inputUnit?.plural ?: "???"} to ${targetUnit?.plural ?: "???"} is impossible\n")
    }
}

enum class Unit(
    val names: List<String>,
    private val conversionRate: Double = 0.0,
    val type: UnitType,
) {
    // Length units
    Meters(
        names = listOf("m", "meter", "meters"),
        conversionRate = 1.0,
        type = UnitType.Length
    ),
    Kilometers(
        names = listOf("km", "kilometer", "kilometers"),
        conversionRate = 1_000.0,
        type = UnitType.Length
    ),
    Centimeters(
        names = listOf("cm", "centimeter", "centimeters"),
        conversionRate = 0.01,
        type = UnitType.Length
    ),
    Millimeters(
        names = listOf("mm", "millimeter", "millimeters"),
        conversionRate = 0.001,
        type = UnitType.Length
    ),
    Miles(
        names = listOf("mi", "mile", "miles"),
        conversionRate = 1609.35,
        type = UnitType.Length
    ),
    Yards(
        names = listOf("yd", "yard", "yards"),
        conversionRate = 0.9144,
        type = UnitType.Length
    ),
    Feet(
        names = listOf("ft", "foot", "feet"),
        conversionRate = 0.3048,
        type = UnitType.Length
    ),
    Inches(
        names = listOf("in", "inch", "inches"),
        conversionRate = 0.0254,
        type = UnitType.Length
    ),

    // Weight units
    Grams(
        names = listOf("g", "gram", "grams"),
        conversionRate = 1.0,
        type = UnitType.Weight
    ),
    Kilograms(
        names = listOf("kg", "kilogram", "kilograms"),
        conversionRate = 1_000.0,
        type = UnitType.Weight
    ),
    Milligrams(
        names = listOf("mg", "milligram", "milligrams"),
        conversionRate = 0.001,
        type = UnitType.Weight
    ),
    Pounds(
        names = listOf("lb", "pound", "pounds"),
        conversionRate = 453.592,
        type = UnitType.Weight
    ),
    Ounces(
        names = listOf("oz", "ounce", "ounces"),
        conversionRate = 28.3495,
        type = UnitType.Weight
    ),

    // Temperature units
    Celsius(
        names = listOf("c", "dc", "celsius", "degree Celsius", "degrees Celsius"),
        type = UnitType.Temperature
    ),
    Fahrenheit(
        names = listOf("f", "df", "fahrenheit", "degree Fahrenheit", "degrees Fahrenheit"),
        type = UnitType.Temperature
    ),
    Kelvins(
        names = listOf("k", "kelvin", "kelvins"),
        type = UnitType.Temperature
    );

    fun convertLengthOrWeight(inputValue: Double, targetUnit: Unit) =
        inputValue * this.conversionRate / targetUnit.conversionRate

    fun convertTemperature(inputValue: Double, targetUnit: Unit) = when (targetUnit) {
        Celsius -> when (this) {
            Fahrenheit -> (inputValue - 32) * 5 / 9
            Kelvins -> inputValue - 273.15
            else -> inputValue
        }

        Fahrenheit -> when (this) {
            Celsius -> inputValue * 9 / 5 + 32
            Kelvins -> inputValue * 9 / 5 - 459.67
            else -> inputValue
        }

        Kelvins -> when (this) {
            Celsius -> inputValue + 273.15
            Fahrenheit -> (inputValue + 459.67) * 5 / 9
            else -> inputValue
        }

        else -> 0.0
    }

    private val singular = names[names.lastIndex - 1]
    val plural = names.last()
    fun getName(value: Double) = if (value == 1.0) singular else plural
}

enum class UnitType {
    Length, Weight, Temperature
}