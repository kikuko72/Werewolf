package kikuko72.werewolf.model.id;

import javaslang.Tuple2;
import javaslang.control.Either;
import org.junit.Assert;
import org.junit.Test;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by User on 2017/01/07.
 */
public class ProcessUniquePlayerIdFactoryTest extends IdFactoryTest{

    @Override
    protected IdFactory getTarget() {
        return new TestTarget();
    }

    // リトライ回数が多いと実行時間が長くなるためリトライ回数を1回に
    private static class TestTarget  extends ProcessUniquePlayerIdFactory {
        @Override
        int getRetryCount() {
            return 1;
        }
    }

    @Test
    public void testGenerateFail() {
        final IdFactory target = getTarget();
        final int taskQuantity = 10;
        ExecutorService pool = Executors.newFixedThreadPool(taskQuantity);
        Collection<Callable<Tuple2<Integer, IdCollisionException>>> tasks
                = Stream.generate(() -> generateTask(target)).limit(taskQuantity).collect(Collectors.toList());
        try {
            List<Future<Tuple2<Integer, IdCollisionException>>> results = pool.invokeAll(tasks);
            Optional<Tuple2<Integer, IdCollisionException>> result = results.stream().map(f ->  {
                try {
                    return f.get();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }).reduce((t1, t2) -> {
                int successCount = t1._1() + t2._1();
                IdCollisionException later = t1._2().getIdBucketUsage() > t2._2().getIdBucketUsage() ? t1._2() : t2._2();
                return new Tuple2<>(successCount, later);
            });
            if(result.isPresent()) {
                int totalSuccessCount = result.get()._1();
                Assert.assertTrue("IDは少なくとも一回は生成に成功するはず",totalSuccessCount > 1);
                Assert.assertEquals("成功回数と生成済みIDの数は同じはず", totalSuccessCount, result.get()._2().getIdBucketUsage());
            } else {
                Assert.fail("no result");
            }
        } catch (Exception e) {
            Assert.fail();
        }
    }

    private Callable<Tuple2<Integer, IdCollisionException>> generateTask(final IdFactory target) {
        return () -> {
            int tryCount = 0;
            Either<Exception, Id> tried;
            do {
                tried = target.generate();
                tryCount++;
            } while (tried.isRight());
            IdCollisionException e = (IdCollisionException) tried.getLeft();
            return  new Tuple2<>(tryCount - 1, e); // 成功した回数, IdCollisionException
        };
    }
}