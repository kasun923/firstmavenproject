import org.testng.Assert;
import org.testng.annotations.Test;

public class testClass {
    @Test
    public void thisIsAFailingTest(){
        Assert.assertFalse(false,"This test should pass");
    }

    @Test
    public void thisIsAPassingTest(){
        Assert.assertFalse(false,"This test should fail");
    }
}
