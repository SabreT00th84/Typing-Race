# TypingRaceSimulator

Object Oriented Programming Project — ECS414U

## Project Structure

```
TypingRaceSimulator/src/main/java/
├── Part1/    # Textual simulation (Java, command-line)
└── Part2/    # GUI simulation
    ├── Models # Classes holding data
    ├── ViewModels # Coordinators between GUI and Models
    └── Views # JavaFX GUI Views
```

## Part 1 — Textual Simulation

### How to compile

```bash
cd src/main/java
javac Part1/Typist.java Part1/TypingRace.java
```

### How to run

The race is started by calling `startRace()` on a `TypingRace` object.
A simple way to test this is to add a `main` method to `TypingRace`, for example:

```java
public static void main(String[] args) {
    TypingRace race = new TypingRace(40);
    race.addTypist(new Typist('①', "TURBOFINGERS", 0.85), 1);
    race.addTypist(new Typist('②', "QWERTY_QUEEN",  0.60), 2);
    race.addTypist(new Typist('③', "HUNT_N_PECK",   0.30), 3);
    race.startRace();
}
```

You must add this method before compiling. Then run:

```bash
java Part1/TypingRace
```

Alternatively you can compile the Testing class which already contains a main function that starts a race.

```bash
javac Part1/Testing.java
java Part1/Testing
```

## Part 2 — GUI Simulation

### How to run
Part 2 uses the Maven build system to manage dependencies. There is no need to compile manually when
using maven. The Application will open automatically when the following command is run from the root of the 
project:

```bash
./mvnw javafx:run
```

The `startRaceGUI()` method can be found in the Main class and is called from `public static main(String[] args)`

## Dependencies

- Java Development Kit (JDK) 21 or higher
- No external libraries required for Part 1
- JavaFX 21 or higher

## Notes

- All code should compile and run using standard command-line tools without any IDE-specific configuration.
- The starter code in Part1 was originally written by Ty Posaurus. It contains known issues — finding and fixing them is part of the coursework.
