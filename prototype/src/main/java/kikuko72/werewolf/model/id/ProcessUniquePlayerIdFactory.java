package kikuko72.werewolf.model.id;

import javaslang.control.Either;

/**
 * 実行しているプロセス内で一意のPlayerIdです。
 * Created by kikuko72 on 2016/11/12.
 */
class ProcessUniquePlayerIdFactory implements IdFactory<PlayerId> {
    private final UniqueIdBucket<PlayerId> used = new ConcurrentHashSetBucket<>();

    /**
     * プロセス内で一意のPlayerIdを生成します。
     * このクラスのインスタンスを得るにはこのメソッドを呼び出してください。
     * @return  Either.Left IDの生成の失敗を意味する例外
     * Either.Right 生成されたID
     */
    public Either<Exception, PlayerId> generate() {
        int count = 0;
        do {
            PlayerId candidate =  new PlayerId(getRandomCode());
            if(used.put(candidate)) {
                return Either.right(candidate);
            }
            count++;
        } while(count < getRetryCount());
        return Either.left(new IdCollisionException("プレイヤーIDの生成に失敗しました。 使用済みID数: " + used.countRegisteredIds(), used.countRegisteredIds()));
    }

    private static int getRandomCode() {
        return (int)(Math.random() * Integer.MAX_VALUE);
    }
    int getRetryCount() {
         return 3;
    }
}
