package kikuko72.werewolf.model.id;

import javaslang.control.Either;

/**
 * Id実装クラスの生成用インターフェースです。
 * Created by User on 2017/01/07.
 */
public interface IdFactory<T extends Id> {
    Either<Exception, T> generate();
}
