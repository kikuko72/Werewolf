package kikuko72.werewolf.model.id;

import java.util.Set;

/**
 * IDを一意にするために仕様済みのIDを格納します。
 * このインターフェースのメソッドはスレッドセーフに実装される必要があります。
 * Created by kikuko72 on 2016/11/14.
 */
interface UniqueIdBucket<E extends Id> {
    /**
     * 引数に渡されたIdオブジェクトを"使用済みID"として登録します。
     * 引数のIdオブジェクトがすでに登録済みである場合、
     * すでに登録されているものは上書きせずにfalseを返します。
     * @param element 使用済みとして登録するIdオブジェクト
     * @return 登録された場合はtrue, 登録されなかった場合はfalse
     */
    boolean put(E element);

    /**
     * 引数に渡されたIdオブジェクトが登録されているかどうかを返します。
     * @param element Idオブジェクト
     * @return すでに登録されていればtrue、登録されていなければfalse
     */
    boolean isRegistered(E element);

    /**
     * 引数に渡されたIdオブジェクトの集合を登録されていない状態に戻します。
     * @param Ids Idオブジェクトのセット
     * @return この操作により使用を取り消されたIDがある場合はtrue
     */
    boolean remove(Set<E> Ids);

    /**
     * 登録されているIdオブジェクトの個数を返します。
     * @return 登録されているIdオブジェクトの個数
     */
    int countRegisteredIds();
}
