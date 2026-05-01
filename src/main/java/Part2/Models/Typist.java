package Part2.Models;

import javafx.beans.property.*;

import java.util.*;

/**
 * @author Eesa Adam
 * @version 2
 */
public class Typist {

    public static final Map<String, Double> typingStyles = new LinkedHashMap<>();

    static {
        typingStyles.put("Touch Typist (90% Accuracy, Higher chance of burnout)", 0.90);
        typingStyles.put("Hunt & Peck (70% Accuracy, Medium chance of burnout)", 0.70);
        typingStyles.put("Phone Thumbs (50% Accuracy, Lower chance of burnout)", 0.50);
        typingStyles.put("Voice-to-Text (30% Accuracy, Very low chance of burnout)", 0.30);
    }

    public static final Map<String, Double[]> keyboardTypes = new LinkedHashMap<>();

    static {
        keyboardTypes.put("0 Coins: Touchscreen (-5% Accuracy, 1 character per turn)", new Double[]{-0.10, 1.0, 0.0});
        keyboardTypes.put("75 Coins: Membrane (+0% Accuracy, 1 character per turn)", new Double[]{0.00, 1.0, 75.0});
        keyboardTypes.put("150 Coins: Mechanical (+5% Accuracy, 2 character per turn)", new Double[]{0.05, 2.0, 150.0});
        keyboardTypes.put("400 Coins: Stenographic (+10% Accuracy, 3 character per turn)", new Double[]{0.10, 3.0, 400.0});
    }

    public static final List<String> awards = new ArrayList<>();
    static {
        awards.add("Speed Demon (Awarded for 3 wins)");
        awards.add("Iron Fingers (Awarded for 5 races with no burnouts");
    }

    private String name;
    private String symbol;
    private String colour; //As a hex code
    private final DoubleProperty accuracy;
    private int speed;
    private int coins;
    private final List<RaceStat> raceStats;
    private final Set<String> awardsReceived;
    private String typingStyle;
    private String keyboardType;
    private boolean wristSupport;
    private boolean energyDrink;
    private boolean headphones;
    private final BooleanProperty sponsored;

    //Race Specific
    private final IntegerProperty progress;

    private final BooleanProperty burntOut;
    private final IntegerProperty burnoutRemaining;
    private int burnoutDurationModifier;

    private final IntegerProperty speedBoost;
    private int speedBoostRemaining;

    private final DoubleProperty accuracyBoost;
    private int accuracyBoostRemaining;
    private double accuracyModifer;

    private final BooleanProperty justMistyped;
    private double mistypeChanceModifier;

    private double timeTaken; // In s
    private int numberOfMistypes;
    private int numberOfBurnouts;



    // Constructor of class Typist

    /**
     * Constructor for objects of class Typist.
     * Creates a new typist with a given symbol, name, and accuracy rating.
     *
     * @param typistName     the name of the typist (e.g. "TURBOFINGERS")
     * @param typistSymbol   a single Unicode character representing this typist (e.g. '①', '②', '③')
     * @param colour         the typist's colour, as a hex code
     */
    public Typist(String typistName, String typistSymbol, String colour) {
        this.name = typistName;
        this.symbol = typistSymbol;
        this.colour = colour;
        this.accuracy = new SimpleDoubleProperty(0);
        this.speed = 0;
        this.coins = 0;
        this.raceStats = new LinkedList<>();
        this.awardsReceived = new HashSet<>();
        this.typingStyle = typingStyles.keySet().iterator().next();
        this.keyboardType = keyboardTypes.keySet().iterator().next();
        this.wristSupport = false;
        this.energyDrink = false;
        this.headphones = false;
        this.sponsored = new SimpleBooleanProperty(false);


        //Race Specific
        this.progress = new SimpleIntegerProperty(0);

        this.burntOut = new SimpleBooleanProperty(false);
        this.burnoutRemaining = new SimpleIntegerProperty(0);
        this.burnoutDurationModifier = 0;

        this.speedBoost = new SimpleIntegerProperty(0);
        this.speedBoostRemaining = 0;

        this.accuracyBoost = new SimpleDoubleProperty(0);
        this.accuracyBoostRemaining = 0;
        this.accuracyModifer = 0;

        this.justMistyped = new SimpleBooleanProperty(false);
        this.mistypeChanceModifier = 0;

        this.timeTaken = 0;
    }


    // Methods of class Typist

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public String getColour() {
        return colour;
    }

    public void setColour(String colour) {
        this.colour = colour;
    }

    public double getAccuracy() {
        return Math.min (accuracy.get() + accuracyBoost.get() + accuracyModifer, 1);
    }

    public double getBaseAccuracy() {
        return accuracy.get();
    }

    public DoubleProperty getBaseAccuracyProperty() {
        return accuracy;
    }

    public void setAccuracy(double newAccuracy) {
        if (newAccuracy < 0) {
            accuracy.set(0);
        } else if (newAccuracy > 1) {
            accuracy.set(1);
        } else {
            accuracy.set(newAccuracy);
        }
    }

    public int getSpeed() {
        return speed + speedBoost.get();
    }

    private void setSpeed(int newSpeed) {
        if (newSpeed < 1) {
            speed = 1;
        } else if (newSpeed > 10) {
            speed = 10;
        } else {
            speed = newSpeed;
        }
    }

    public int getCoins() {
        return coins;
    }

    public void addCoins(int coins) {
        if (this.coins + coins >= 0) {
            this.coins += coins;
        } else {
            this.coins = 0;
        }
    }

    public boolean spendCoins(int coins) {
        if (this.coins >= coins) {
            this.coins -= coins;
            return true;
        } else {
            return false;
        }
    }

    public List<RaceStat> getRaceStats() {
        return raceStats;
    }

    public RaceStat getLastRaceStat() {
        return raceStats.getLast();
    }

    public void addRaceStat(RaceStat raceStat) {
        raceStats.add(raceStat);
    }

    public Set<String> getAwardsReceived() {
        return awardsReceived;
    }

    public void addAward(String award) {
        awardsReceived.add(award);
    }

    public String getTypingStyle() {
        return typingStyle;
    }

    public void setTypingStyle(String typingStyle) {
        this.typingStyle = typingStyle;
        setAccuracy(getBaseAccuracy() + typingStyles.get(typingStyle));
    }

    public String getKeyboardType() {
        return keyboardType;
    }

    public void setKeyboardType(String keyboardType) {
        this.keyboardType = keyboardType;
        setAccuracy(getBaseAccuracy() + keyboardTypes.get(keyboardType)[0]);
        setSpeed(keyboardTypes.get(keyboardType)[1].intValue());
    }

    public boolean hasWristSupport() {
        return wristSupport;
    }

    public void setWristSupport(boolean wristSupport) {
        this.wristSupport = wristSupport;
        burnoutDurationModifier = -1;
    }

    public boolean hasEnergyDrink() {
        return energyDrink;
    }

    public void setEnergyDrink(boolean energyDrink) {
        this.energyDrink = energyDrink;
    }

    public boolean hasHeadphones() {
        return headphones;
    }

    public void setHeadphones(boolean headphones) {
        this.headphones = headphones;
        setMistypeChanceModifier(-0.05);
    }

    public boolean isSponsored() {
        return sponsored.get();
    }

    public BooleanProperty getSponsoredProperty() {
        return sponsored;
    }

    public void setSponsored(boolean sponsored) {
        this.sponsored.set(sponsored);
    }

    public int getProgress() {
        return progress.get();
    }

    public IntegerProperty getProgressProperty() {
        return progress;
    }

    public void takeTurn(double mistypeBaseChance,
                         int slideBackAmount,
                         int burnoutDuration,
                         double baseAccuracyMultiplier) {

        if (hasJustMistyped()) {
            justMistyped.set(false);
        }

        if (isBurntOut()) {
            recoverFromBurnout();
        } else if (Math.random() < (1 - getAccuracy()) * (mistypeBaseChance + mistypeChanceModifier)) {
            slideBack(slideBackAmount);
        } else if (Math.random() < 0.05 * getAccuracy() * getAccuracy()) {
            burnOut(burnoutDuration);
            setAccuracy(accuracy.get() - (accuracy.get() * baseAccuracyMultiplier));
        } else {
            typeCharacter();
        }

        if (getSpeedBoostRemaining() > 0) {
            speedBoostRemaining--;
        }

        if (getSpeedBoostRemaining() <= 0) {
            speedBoostRemaining = 0;
            speedBoost.set(0);
        }

        accuracyBoostRemaining--;

        if (getAccuracyBoostRemaining() <= 0) {
            accuracyBoost.set(-accuracyBoost.get());
            accuracyBoostRemaining = Integer.MAX_VALUE;
        }

        timeTaken += 0.25;
    }

    private void typeCharacter() {
        progress.set(getProgress() + getSpeed());
    }

    private void slideBack(int amount) {

        justMistyped.set(true);
        numberOfMistypes++;

        if ((getProgress() - amount) < 0) {
            progress.set(0);
        } else {
            progress.set(getProgress() - amount);
        }
    }

    public boolean isBurntOut() {
        return burntOut.get();
    }

    public BooleanProperty getBurntOutProperty() {
        return burntOut;
    }

    private void burnOut(int turns) {
        burntOut.set(true);
        burnoutRemaining.set(turns + burnoutDurationModifier);
        numberOfBurnouts++;
    }

    private void recoverFromBurnout() {
        if (isBurntOut()) {
            burnoutRemaining.set(burnoutRemaining.get() - 1);

            if (burnoutRemaining.get() <= 0) {
                burntOut.set(false);
            }
        }
    }

    public int getBurnoutTurnsRemaining() {
        return burnoutRemaining.get();
    }

    public IntegerProperty getBurnoutRemainingProperty() {
        return burnoutRemaining;
    }

    public IntegerProperty getSpeedBoostProperty() {
        return speedBoost;
    }

    public void setSpeedBoost(int speedBoost, int speedBoostDuration) {
        this.speedBoost.set(speedBoost);
        speedBoostRemaining = speedBoostDuration;
    }

    public int getSpeedBoostRemaining() {
        return speedBoostRemaining;
    }

    public DoubleProperty getAccuracyBoostProperty() {
        return accuracyBoost;
    }

    private void setAccuracyBoost(double accuracyBoost, int accuracyBoostDuration) {
        this.accuracyBoost.set(accuracyBoost);
        accuracyBoostRemaining = accuracyBoostDuration;
    }

    public int getAccuracyBoostRemaining() {
        return accuracyBoostRemaining;
    }

    public void setAccuracyModifier(double accuracyModifier) {
        this.accuracyModifer = accuracyModifier;
    }

    public boolean hasJustMistyped() {
        return justMistyped.get();
    }

    public BooleanProperty getJustMistypedProperty() {
        return justMistyped;
    }

    public void setMistypeChanceModifier(double mistypeChanceModifier) {
        this.mistypeChanceModifier = mistypeChanceModifier;
    }

    public double getMistypeChanceModifier() {
        return mistypeChanceModifier;
    }

    public double getTimeTaken() {
        return timeTaken;
    }

    public int getNumberOfMistypes() {
        return numberOfMistypes;
    }

    public int getNumberOfBurnouts() {
        return numberOfBurnouts;
    }

    public void reset() {
        progress.set(0);
        burntOut.set(false);
        burnoutRemaining.set(0);
        speedBoost.set(0);
        speedBoostRemaining = 0;
        accuracyBoost.set(0);
        accuracyBoostRemaining = 0;
        accuracyModifer = 0;
        justMistyped.set(false);
        timeTaken = 0;
    }

    public void prepareForRace() {
        if (energyDrink) setAccuracyBoost(0.1, 75);
    }

    public boolean isFinished(int passageLength) {
        return getProgress() >= passageLength;
    }

    @Override
    public String toString() {
        return name;
    }
}