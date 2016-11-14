package kikuko72.werewolf.model.id;

import javaslang.control.Either;

import java.util.function.Function;
import java.util.function.Supplier;

/**
 * 一意なIDの生成ロジックを提供するクラスです。
 * Created by kikuko72 on 2016/11/13.
 */
final class UniqueIDGenerator {

    /**
     * 引数に渡されたSet内で一意のIDの生成をマルチスレッド安全に1度試みます。
     * このメソッドは一意性の判定にSetを使い、生成したIDを使用済みIDとしてSetに登録します。
     * @param constructor 生成するIdの実装クラスのコンストラクタ
     * @param valueGenerator コンストラクタの引数を生成するメソッド。
     * @param uniqueIdBucket 一意性を保障するための使用済みIDセット
     * @param <T> コンストラクタの引数。Idの内部実装用の値を意図しています
     * @param <E> Idの実装型
     * @return  Either.Left IDの生成の失敗を意味する例外
     * Either.Right 生成されたID
     */
    static <T, E extends Id> Either<Exception, E> generateOnce(final Function<T, E> constructor, final Supplier<T> valueGenerator, final UniqueIdBucket<E> uniqueIdBucket) {
        E candidate = constructor.apply(valueGenerator.get());
        if(!uniqueIdBucket.put(candidate)) {
            return Either.left(new Exception("IDを生成できませんでした"));
        }
        return Either.right(candidate);

    }
}
