//package com.himedia.luckydokiapi.config;
//
//import com.mongodb.MongoClientSettings;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.data.mongodb.config.AbstractMongoClientConfiguration;
//import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
//
//@Configuration
//@EnableMongoRepositories(basePackages = "com.himedia.luckydokiapi.domain.chat.repository")
//
//public class MongoConfig extends AbstractMongoClientConfiguration {
//    @Override
//    protected String getDatabaseName() {
//        return "chatdb"; // 사용할 mongodb 이름
//    }
//}
////