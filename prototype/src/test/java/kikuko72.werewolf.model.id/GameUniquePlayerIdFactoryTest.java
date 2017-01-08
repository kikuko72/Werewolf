package kikuko72.werewolf.model.id;

import javaslang.Tuple3;
import javaslang.control.Either;
import org.junit.Assert;
import org.junit.Test;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.Callable;

/**
 * GameUniquePlayerIdFactoryのテストです。
 * IdFactoryTestのインターフェース仕様を満たしているか　＋　独自例外の仕様確認を行います。
 * Created by kikuko72 on 2017/01/07.
 */
public class GameUniquePlayerIdFactoryTest extends IdFactoryTest<PlayerId> {

    @Override
    protected IdFactory<PlayerId> getTarget() {
        return new GameUniquePlayerIdFactory(1); // リトライ回数が多いと実行時間が長くなるためリトライ回数を1回に
    }

    /**
     * Idの生成に失敗時の例外からId使用量の情報が取得できるかの確認
     */
    @Test
    public void testGenerateFailException() {
        try {
            final IdFactory<PlayerId> target = getTarget();
            Optional<Tuple3<List<Exception>, Integer, Set<Id>>> summary = generateAsyncAndGetSummary(() -> generateTask(target));
            if(!summary.isPresent()) {
                Assert.fail("no result");
            }

            int totalSuccessCount = summary.get()._2();
            Optional<IdCollisionException> latestException = summary.get()._1().stream()
                    .map(e -> (IdCollisionException)e)
                    .reduce((e1, e2) -> e1.getIdBucketUsage() > e2.getIdBucketUsage() ? e1 : e2);
            if(!latestException.isPresent()) {
                Assert.fail("something wrong");
            }
            Assert.assertEquals("成功回数とIdBucketの使用量は同じはず", totalSuccessCount, latestException.get().getIdBucketUsage());
            Assert.assertEquals("生成したIdの数とIdBucketの使用量は同じはず", summary.get()._3().size(), latestException.get().getIdBucketUsage());
        } catch (Exception e) {
            Assert.fail();
        }
    }

    /**
     * Id生成タスク生成メソッド
     * @param target テスト対象となるIdFactoryオブジェクト
     * @return Id生成タスク
     */
    private Callable<Tuple3<Optional<Exception>, Integer, List<Id>>> generateTask(final IdFactory<PlayerId> target) {
        return () -> {
            int tryCount = 0;
            List<Id> generatedIds = new LinkedList<>(); // 各タスク中の挿入回数が多くなることが見込まれるため
            Either<Exception, PlayerId> tried;
            do {
                tried = target.generate();
                if (tried.isRight()) {
                    generatedIds.add(tried.get());
                }
                tryCount++;
            } while (tried.isRight()); // ID衝突例外が発生するまで
            return  new Tuple3<>(Optional.of(tried.getLeft()), tryCount - 1, generatedIds); // 発生した例外, 成功した回数, 生成したIdリスト
        };
    }
}