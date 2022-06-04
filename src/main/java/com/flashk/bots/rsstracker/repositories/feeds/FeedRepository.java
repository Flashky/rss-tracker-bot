package com.flashk.bots.rsstracker.repositories.feeds;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.flashk.bots.rsstracker.repositories.feeds.entities.FeedEntity;

public interface FeedRepository extends MongoRepository<FeedEntity, String> {

}
