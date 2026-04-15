public class Testing {

    public static void main(String[] args) {

        TypingRace race = new TypingRace(40);
        race.addTypist(new Typist('①', "TURBOFINGERS", 0.85), 1);
        race.addTypist(new Typist('②', "QWERTY_QUEEN", 0.60), 2);
        race.addTypist(new Typist('③', "HUNT_N_PECK", 0.40), 3);
        race.startRace();
        return;
    }

    // 1.0 Testing Typist Class

    // 1.1 Testing slideBack() does not go below 0
    // Tests:
    // Normal - testSlideBack(5, 3)
    // Extreme - testSlideBack(3, 4)
    // Extreme - testSlideBack(0, 2)
    // Boundary - testSlideBack(2, 2)
    public static void testSlideBack(int initalProgress, int slideBackAmount) {
        Typist typist = new Typist('①', "Eesa", 0);

        for (int i = 1; i <= initalProgress; i++) {
            typist.typeCharacter();
        }

        System.out.println("Progress before: " + typist.getProgress());

        typist.slideBack(slideBackAmount);

        System.out.println("Progress after: " + typist.getProgress());
    }

    // 1.2 Testing burnout logic
    // Tests:
    // testBurnoutLogic(2)
    public static void testBurnoutLogic(int duration) {
        Typist typist = new Typist('①', "Eesa", 0);

        typist.burnOut(duration);
        System.out.println("Burnt out: " + typist.isBurntOut());
        System.out.println("Burnout remaining: " + typist.getBurnoutTurnsRemaining());

        for (int i = 0; i < duration; i++) {
            typist.recoverFromBurnout();

            System.out.println("Burnt out: " + typist.isBurntOut());
            System.out.println("Burnout remaining: " + typist.getBurnoutTurnsRemaining());
        }
    }

    // 1.3 Testing resetToStart()
    // Tests:
    // testReset()
    public static void testReset() {
        Typist typist = new Typist('①', "Eesa", 0);

        typist.typeCharacter();
        typist.burnOut(4);

        System.out.println("Progress before: " + typist.getProgress());
        System.out.println("Burnt out before: " + typist.isBurntOut());
        System.out.println("Burnout remaining before: " + typist.getBurnoutTurnsRemaining());

        typist.resetToStart();

        System.out.println("Progress after: " + typist.getProgress());
        System.out.println("Burnt out after: " + typist.isBurntOut());
        System.out.println("Burnout remaining after: " + typist.getBurnoutTurnsRemaining());
    }

    // 1.4 Testing setAccuracy()
    // Tests: 
    // Extreme - testSetAccuracy(2)
    // Extreme - testSetAccuracy(-1)
    // Boundary - testSetAccuracy(1)
    // Boundary - testSetAccuracy(0)
    // Normal - testSetAccuracy(0.5)
    public static void testSetAccuracy(double accuracy) {
        Typist typist = new Typist('①', "Eesa", 0);

        typist.setAccuracy(accuracy);
        System.out.println("Accuracy: " + typist.getAccuracy());
    }

    // 1.5 Testing typeCharacter()
    // Tests:
    // testTypeCharacter()
    public static void testTypeCharacter() {
        Typist typist = new Typist('①', "Eesa", 0);

        System.out.println("Progress before: " + typist.getProgress());

        typist.typeCharacter();

        System.out.println("Progress after: " + typist.getProgress());
    }
}