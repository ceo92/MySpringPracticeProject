package hello.itemservice.config;

import hello.itemservice.repository.ItemRepository;
import hello.itemservice.repository.jpa.JpaItemRepository;
import hello.itemservice.service.ItemService;
import hello.itemservice.service.ItemServiceV1;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.persistence.EntityManager;

@Configuration
@RequiredArgsConstructor
public class JpaConfig {

    private final EntityManager entityManager;


    @Bean
    public ItemService itemService(){
        return new ItemServiceV1(jpaItemRepository());
    }
    @Bean
    public ItemRepository jpaItemRepository(){
        return new JpaItemRepository(entityManager);
    }
}
