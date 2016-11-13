package kikuko72.werewolf.model.id;

import javaslang.control.Either;
import org.junit.Assert;
import org.junit.Test;

/**
 * Created by User on 2016/11/13.
 */
public class ProcessUniquePlayerIdTest {
    @Test
    public void testGenerate() {
        Either<Exception, ProcessUniquePlayerId> tried = ProcessUniquePlayerId.generate();
        Assert.assertTrue("初回のID生成には必ず成功するはず", tried.isRight());
    }

    @Test
    public void testGenerateFail() {
        int tryCount = 0;
        Either<Exception, ProcessUniquePlayerId> tried;
        do {
            tried = ProcessUniquePlayerId.generate();
            tryCount++;
        } while (tried.isRight());
        Assert.assertTrue("IDは少なくとも一回は生成に成功するはず", tryCount > 1);
        IdCollisionException e = (IdCollisionException) tried.getLeft();
        Assert.assertEquals("最後に一度生成に失敗しているため試行回数より1少ないはず", tryCount - 1, e.getIdBucketUsage());
    }

}