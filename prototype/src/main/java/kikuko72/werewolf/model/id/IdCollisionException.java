package kikuko72.werewolf.model.id;

import lombok.Value;

/**
 * 生成済みのIDと衝突してIDの生成に失敗した事を意味する例外クラスです。
 * Created by kikuko72 on 2016/11/13.
 */
@Value
public class IdCollisionException extends Exception {
    private final int idBucketUsage;
    IdCollisionException(String message, Throwable t, int idBucketUsage) {
        super(message,t);
        this.idBucketUsage = idBucketUsage;
    }
}
