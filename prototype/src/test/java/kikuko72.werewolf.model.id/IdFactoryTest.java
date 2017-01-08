package kikuko72.werewolf.model.id;

import javaslang.Tuple3;
import javaslang.control.Either;
import org.junit.Assert;
import org.junit.Test;

import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * IdFactoryのインターフェース仕様のテストクラスです。
 * IdFactoryの実装クラスはすべてこのテストクラスのテストにパスする必要があります。
 * このクラス継承し、IdFactoryTest#getTargetをオーバーライドしてください。
 * Created by kikuko72 on 2016/11/13.
 */

public abstract class IdFactoryTest<T extends Id> {

    abstract protected IdFactory<T> getTarget();

    @Test
    public void testGenerate() {
        Either<Exception, T> tried = getTarget().generate();
        Assert.assertTrue("初回のID生成には必ず成功するはず", tried.isRight());
    }

    /**
     * 生成に成功したIdが一意であることの確認
     */
    @Test
    public void testGenerateUniqueness() {
        try {
            final IdFactory<T> target = getTarget();
            Optional<Tuple3<List<Exception>, Integer, Set<Id>>> summary = generateAsyncAndGetSummary(() -> generateTask(target));
            if(!summary.isPresent()) {
                Assert.fail("no result");
            }
            int totalSuccessCount = summary.get()._2();
            Assert.assertTrue("IDは少なくとも一回は生成に成功するはず",totalSuccessCount > 1);
            Assert.assertEquals("成功回数と一意な生成済みIDの数は同じはず", totalSuccessCount, summary.get()._3().size());
        } catch (Exception e) {
            Assert.fail();
        }
    }

    /**
     * 非同期にIdの生成を行ってその結果を集約したものを返します。
     * @return Optional<Tuple3<List<Exception>, Integer, Set<Id>>> summary : 非同期Id生成結果のサマリ。
     * 結果の組の中身は、(Id生成に失敗した時の理由,生成に成功した回数,生成されたIdのセット)です。
     * @throws Exception
     */
    protected Optional<Tuple3<List<Exception>, Integer, Set<Id>>> generateAsyncAndGetSummary(Supplier<Callable<Tuple3<Optional<Exception>, Integer, List<Id>>>> taskGenerator) throws  Exception {
        final int taskQuantity = 10;
        ExecutorService pool = Executors.newFixedThreadPool(taskQuantity);
        Collection<Callable<Tuple3<Optional<Exception>, Integer, List<Id>>>> tasks
                = Stream.generate(taskGenerator::get).limit(taskQuantity).collect(Collectors.toList());
        List<Future<Tuple3<Optional<Exception>, Integer, List<Id>>>> results = pool.invokeAll(tasks);
        return results.stream().map(f ->  {
            try {
                Tuple3<Optional<Exception>, Integer, List<Id>> result = f.get();
                // 3引数のreduceは冗長なためここで型をそろえておく
                List<Exception> exceptions = new ArrayList<>();
                if(result._1().isPresent()) {
                  exceptions.add(result._1().get());
                }
                Set<Id> ids = new HashSet<>();
                ids.addAll(result._3());
                return  new Tuple3<>(exceptions, result._2(), ids);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }).reduce((t1, t2) -> {
            List<Exception> exceptions = new ArrayList<>();
            exceptions.addAll(t1._1());
            exceptions.addAll(t2._1());
            int successCount = t1._2() + t2._2();
            // 一意性の確認のためSetにする
            Set<Id> ids = new HashSet<>();
            ids.addAll(t1._3());
            ids.addAll(t2._3());
            return new Tuple3<>(exceptions, successCount, ids);
        });
    }

    /**
     * Id生成タスク生成メソッド
     * @param target テスト対象となるIdFactoryオブジェクト
     * @return Id生成タスク
     */
    private Callable<Tuple3<Optional<Exception>, Integer, List<Id>>> generateTask(final IdFactory<T> target) {
        return () -> {
            int tryCount = 0;
            List<Id> generatedIds = new LinkedList<>(); // 各タスク中の挿入回数が多くなることが見込まれるため
            Either<Exception, T> tried;
            do {
                tried = target.generate();
                if (tried.isRight()) {
                    generatedIds.add(tried.get());
                }
                tryCount++;
            } while (tryCount < 50000 && tried.isRight());
            Optional<Exception> e;
            int successCount;
            if (tried.isLeft()) {
                e = Optional.of(tried.getLeft());
                successCount = tryCount - 1;
            } else {
                e = Optional.empty();
                successCount = tryCount;
            }
            return  new Tuple3<>(e, successCount, generatedIds); // 発生した例外, 成功した回数, 生成したIdリスト
        };
    }
}