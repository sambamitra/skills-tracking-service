package uk.gov.nhs.sts;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.support.GenericApplicationContext;

@SpringBootTest
class ApplicationTest {

  @Autowired
  private GenericApplicationContext ctx;

  @Test
  void contextLoads() {
    assertThat(ctx.isRunning(), is(true));
  }

}
