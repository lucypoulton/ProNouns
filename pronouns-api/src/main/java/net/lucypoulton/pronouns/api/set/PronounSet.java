package net.lucypoulton.pronouns.api.set;

import net.lucypoulton.pronouns.api.StringUtils;

import java.util.List;

public abstract class PronounSet {
    public abstract String subjective();

    public abstract String objective();

    public abstract String progressive();

    public abstract String possessiveAdjective();

    public abstract String possessivePronoun();

    public abstract String reflexive();

    public String formatted() {
        return StringUtils.capitalise(subjective()) + "/" + StringUtils.capitalise(objective());
    }

    @Override
    public String toString() {
        return subjective() + "/" +
            objective() + "/" +
            progressive() + "/" +
            possessiveAdjective() + "/" +
            possessivePronoun() + "/" +
            reflexive();
    }

    public static PronounSet parse(String input) {
        List<String> split = StringUtils.splitSet(input);
        if (split.size() != 6) {
            throw new IllegalArgumentException("Invalid number of pronouns in set");
        }
        return new ParsedPronounSet(split.get(0), split.get(1), split.get(2), split.get(3), split.get(4), split.get(5));
    }
}
