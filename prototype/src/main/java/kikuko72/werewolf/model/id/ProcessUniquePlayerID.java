package kikuko72.werewolf.model.id;

import javaslang.control.Either;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.val;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * 実行しているプロセス内で一意のPlayerIdです。
 * Created by kikuko72 on 2016/11/12.
 */
@EqualsAndHashCode
@ToString
public final class ProcessUniquePlayerId implements PlayerId {
    private static final UniqueIdBucket<ProcessUniquePlayerId> used = new ConcurrentHashSetBucket<>();
    private final int code;

    private ProcessUniquePlayerId(int code) {
        this.code = code;
    }

    /**
     * プロセス内で一意のPlayerIdを生成します。
     * このクラスのインスタンスを得るにはこのメソッドを呼び出してください。
     * @return  Either.Left IDの生成の失敗を意味する例外
     * Either.Right 生成されたID
     */
    public static Either<Exception, ProcessUniquePlayerId> generate() {
        int count = 0;
        do {
            ProcessUniquePlayerId candidate =  new ProcessUniquePlayerId(getRandomCode());
            if(used.put(candidate)) {
                return Either.right(candidate);
            }
            count++;
        } while(count < 3);
        return Either.left(new IdCollisionException("プレイヤーIDの生成に失敗しました。 使用済みID数: " + used.countRegisteredIds(), used.countRegisteredIds()));
    }

    /**
     * 引数で指定されたPlayerIDを再度使用可能にします。
     * 使用可能になったIDはgenerate()メソッドで再び生成されることがあります。
     * この操作はスレッドセーフに行われます。
     * @param Ids 再使用可能にするIDセット
     * @return この操作で使用可能になったIDが存在する場合true
     */
    public static boolean forget(Set<ProcessUniquePlayerId> Ids) {
        synchronized (used) {
            return used.remove(Ids);
        }
    }

    /**
     * このメソッドは#forget(Set Ids)のシンタックスシュガーです。
     * @see #forget(Set)
     */
    public static boolean forget(ProcessUniquePlayerId... Ids) {
        Set<ProcessUniquePlayerId> idSet = new HashSet<>();
        Collections.addAll(idSet, Ids);
        return forget(idSet);
    }

    private static int getRandomCode() {
        return (int)(Math.random() * Integer.MAX_VALUE);
    }
}
