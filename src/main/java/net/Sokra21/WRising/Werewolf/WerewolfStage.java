package net.Sokra21.WRising.Werewolf;

public enum WerewolfStage {
    HUMAN,
    CURSED,
    EARLY,
    MID,
    ELDER;

    public boolean isWerewolf() {
        return this == EARLY || this == MID || this == ELDER;
    }
}
