package sk.peter.tenis.util;

public final class Printer {
    private Printer() {
    } // zabrani vytvaraniu inštancií

    // jednoduchy wrapper  - neskor sa rozsiri o farby / logovanie
    public static void println(String msg) {
        System.out.println(msg);
    }
}
