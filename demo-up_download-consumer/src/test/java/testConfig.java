import com.wuwei.dubboconsumer.ConsumerApplication;
import com.wuwei.dubboconsumer.config.ContentTypeConfig;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Map;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = ConsumerApplication.class)
public class testConfig {

    @Autowired
    private ContentTypeConfig contentTypeConfig;

    @Test
    public void test(){
        Map<String, String> contentType = contentTypeConfig.getTypes();
        System.out.println(contentType);

    }
}
