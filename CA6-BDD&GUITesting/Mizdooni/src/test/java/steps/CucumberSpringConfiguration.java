package steps;

import io.cucumber.spring.CucumberContextConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import mizdooni.MizdooniApplication;

@CucumberContextConfiguration
@SpringBootTest(classes = MizdooniApplication.class)
public class CucumberSpringConfiguration { }
