package converter

fun main() {
    do {
        print("Enter what you want to convert (or exit): ")
        val input = readln()
        if (input != "exit") convertUnit(userInput = input.lowercase())
    } while (input != "exit")
}

fun convertUnit(userInput: String) {
    val input = userInput.split(" ").filter { !Regex("degrees?").matches(it) }

    val inputValue = try {
        input[0].toDouble()
    } catch (e: NumberFormatException) {
        println("Parse error\n")
        return
    }

    val inputUnit = Unit.values().find { unit -> input[1] in unit.symbols.map { symbol -> symbol.lowercase() } }

    if (inputUnit != null) {
        if (inputValue < 0 && inputUnit.type in listOf(UnitType.Weight, UnitType.Length)) {
            println("${inputUnit.type} shouldn't be negative\n")
            return
        }
    }

    val targetUnit = Unit.values().find { unit -> input[3] in unit.symbols.map { symbol -> symbol.lowercase() } }

    val units = listOf(inputUnit, targetUnit)

    if (
        (inputUnit != null) && (targetUnit != null) && (units.all {
            it?.type == UnitType.Length
        } || units.all {
            it?.type == UnitType.Weight
        } || units.all {
            it?.type == UnitType.Temperature
        })
    ) {
        val targetValue = if (inputUnit.type == UnitType.Temperature) {
            inputUnit.convertTemperature(value = inputValue, targetUnit = targetUnit)
        } else {
            inputUnit.convertLengthOrWeight(value = inputValue, targetUnit = targetUnit)
        }

        val inputUnitName = inputUnit.getName(inputValue)
        val targetUnitName = targetUnit.getName(targetValue)

        println("$inputValue $inputUnitName is $targetValue $targetUnitName\n")
    } else {
        println("Conversion from ${inputUnit?.symbols?.last() ?: "???"} to ${targetUnit?.symbols?.last() ?: "???"} is impossible\n")
    }
}

enum class Unit(
    val symbols: List<String>,
    private val conversionRate: Double = 0.0,
    val type: UnitType,
) {
    // Length units
    Meters(
        symbols = listOf("m", "meter", "meters"),
        conversionRate = 1.0,
        type = UnitType.Length
    ),
    Kilometers(
        symbols = listOf("km", "kilometer", "kilometers"),
        conversionRate = 1_000.0,
        type = UnitType.Length
    ),
    Centimeters(
        symbols = listOf("cm", "centimeter", "centimeters"),
        conversionRate = 0.01,
        type = UnitType.Length
    ),
    Millimeters(
        symbols = listOf("mm", "millimeter", "millimeters"),
        conversionRate = 0.001,
        type = UnitType.Length
    ),
    Miles(
        symbols = listOf("mi", "mile", "miles"),
        conversionRate = 1609.35,
        type = UnitType.Length
    ),
    Yards(
        symbols = listOf("yd", "yard", "yards"),
        conversionRate = 0.9144,
        type = UnitType.Length
    ),
    Feet(
        symbols = listOf("ft", "foot", "feet"),
        conversionRate = 0.3048,
        type = UnitType.Length
    ),
    Inches(
        symbols = listOf("in", "inch", "inches"),
        conversionRate = 0.0254,
        type = UnitType.Length
    ),

    // Weight units
    Grams(
        symbols = listOf("g", "gram", "grams"),
        conversionRate = 1.0,
        type = UnitType.Weight
    ),
    Kilograms(
        symbols = listOf("kg", "kilogram", "kilograms"),
        conversionRate = 1_000.0,
        type = UnitType.Weight
    ),
    Milligrams(
        symbols = listOf("mg", "milligram", "milligrams"),
        conversionRate = 0.001,
        type = UnitType.Weight
    ),
    Pounds(
        symbols = listOf("lb", "pound", "pounds"),
        conversionRate = 453.592,
        type = UnitType.Weight
    ),
    Ounces(
        symbols = listOf("oz", "ounce", "ounces"),
        conversionRate = 28.3495,
        type = UnitType.Weight
    ),

    // Temperature units
    Celsius(
        symbols = listOf("c", "dc", "celsius", "degree Celsius", "degrees Celsius"),
        type = UnitType.Temperature
    ),
    Fahrenheit(
        symbols = listOf("f", "df", "fahrenheit", "degree Fahrenheit", "degrees Fahrenheit"),
        type = UnitType.Temperature
    ),
    Kelvins(
        symbols = listOf("k", "kelvin", "kelvins"),
        type = UnitType.Temperature
    );

    fun convertLengthOrWeight(value: Double, targetUnit: Unit) = value * conversionRate / targetUnit.conversionRate
    fun convertTemperature(value: Double, targetUnit: Unit) = when (targetUnit) {
        Celsius -> when (this) {
            Fahrenheit -> (value - 32) * 5 / 9
            Kelvins -> value - 273.15
            else -> value
        }

        Fahrenheit -> when (this) {
            Celsius -> value * 9 / 5 + 32
            Kelvins -> value * 9 / 5 - 459.67
            else -> value
        }

        Kelvins -> when (this) {
            Celsius -> value + 273.15
            Fahrenheit -> (value + 459.67) * 5 / 9
            else -> value
        }

        else -> 0.0
    }

    fun getName(value: Double) = if (value != 1.0) symbols.last() else symbols[symbols.lastIndex - 1]
}

enum class UnitType {
    Length, Weight, Temperature
}