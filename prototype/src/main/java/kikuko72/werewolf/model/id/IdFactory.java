package kikuko72.werewolf.model.id;

import javaslang.control.Either;

/**
 * Created by User on 2017/01/07.
 */
public interface IdFactory<T extends Id> {
    <T> Either<Exception, T> generate();
}
