package pointsystem;

import org.modelmapper.ModelMapper;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

@TestConfiguration
public  class Config {

    @Bean
    public ModelMapper modelMapper() {
        return new ModelMapper();
    }
}
