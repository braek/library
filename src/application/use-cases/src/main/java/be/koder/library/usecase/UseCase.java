package be.koder.library.usecase;

public interface UseCase<COMMAND extends Command, PRESENTER> {
    void execute(COMMAND command, PRESENTER presenter);
}