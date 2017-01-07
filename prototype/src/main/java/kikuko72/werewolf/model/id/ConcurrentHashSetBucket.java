package kikuko72.werewolf.model.id;

import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * UniqueIdBucketのHashSet実装です。
 * このクラスはIdオブジェクトの登録を定数時間で行えますが、
 * 登録されたIdオブジェクトを取得する手段は提供しません。
 * Created by kikuko72 on 2016/11/14.
 */
@EqualsAndHashCode
@ToString
class ConcurrentHashSetBucket<E extends  Id> implements UniqueIdBucket<E> {
    private final Set<E> bucket = ConcurrentHashMap.newKeySet();

    @Override
    public boolean put(E element) {
        return bucket.add(element);
    }

    @Override
    public boolean isRegistered(E element) {
        return bucket.contains(element);
    }

    @Override
    public boolean remove(Set<E> Ids) {
        return bucket.removeAll(Ids);
    }

    @Override
    public int countRegisteredIds() {
        return bucket.size();
    }
}
