package kikuko72.werewolf.model.id;

import javaslang.control.Either;
import org.junit.Assert;
import org.junit.Test;

/**
 * Created by User on 2016/11/13.
 */

public abstract class IdFactoryTest {

    abstract protected IdFactory getTarget();

    @Test
    public void testGenerate() {
        Either<Exception, Id> tried = getTarget().generate();
        Assert.assertTrue("初回のID生成には必ず成功するはず", tried.isRight());
    }
}