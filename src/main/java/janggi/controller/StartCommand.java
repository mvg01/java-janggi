package janggi.controller;

import java.util.Arrays;

public enum StartCommand {
    CREATE_NEW_GAME(1),
    LOAD_PREVIOUS_GAME(2);

    private static final int MIN_SETUP_COMMAND = 1;
    private static final int MAX_SETUP_COMMAND = 2;
    private final int commandNumber;

    StartCommand(final int commandNumber) {
        this.commandNumber = commandNumber;
    }

    public static StartCommand from(final int inputCommand) {
        return Arrays.stream(StartCommand.values())
                .filter(command -> command.commandNumber == inputCommand)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(
                        "입력은 " + MIN_SETUP_COMMAND + "에서 " + MAX_SETUP_COMMAND + "까지의 정수 값이어야 합니다."));
    }
}
