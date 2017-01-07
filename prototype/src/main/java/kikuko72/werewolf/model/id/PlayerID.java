package kikuko72.werewolf.model.id;

import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * プレイヤーID型を表現します。
 * Created by kikuko72 on 2016/11/12.
 */
@EqualsAndHashCode
@ToString
public final class PlayerId implements Id {
    private final int code;
    PlayerId(int code) {
        this.code = code;
    }
}
