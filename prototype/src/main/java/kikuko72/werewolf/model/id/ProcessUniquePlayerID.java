package kikuko72.werewolf.model.id;

import javaslang.control.Either;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 実行しているプロセス内で一意のPlayerIdです。
 * Created by kikuko72 on 2016/11/12.
 */
@EqualsAndHashCode
@ToString
public final class ProcessUniquePlayerId implements PlayerId {
    private static final Set<ProcessUniquePlayerId> used = ConcurrentHashMap.newKeySet();
    private final int code;

    private ProcessUniquePlayerId(int code) {
        this.code = code;
    }

    /**
     * プロセス内で一意のPlayerIdを生成します。
     * このクラスのインスタンスを得るにはこのメソッドを呼び出してください。
     * @return  Either.Left IDの生成の失敗を意味する例外
     * @return  Either.Right 生成されたID
     */
    public static Either<Exception, ProcessUniquePlayerId> generate() {
        int count = 0;
        Either<Exception, ProcessUniquePlayerId> tried;
        do {
            tried = UniqueIDGenerator.generateOnce(ProcessUniquePlayerId::new, ProcessUniquePlayerId::getRandomCode, used);
            count++;
        } while(tried.isLeft() && count < 3);
        return tried.left().map(e -> (Exception)new IdCollisionException("プレイヤーIDの生成に失敗しました。 使用済みID数: " + used.size(), e, used.size())).toEither();
    }

    private static Integer getRandomCode() {
        return (int)(Math.random() * Integer.MAX_VALUE);
    }
}
