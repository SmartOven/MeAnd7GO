package ru.itmo.highload.client;

import org.springframework.stereotype.Repository;
import org.springframework.data.mongodb.repository.MongoRepository;

@Repository
public interface ClientRepository extends MongoRepository<Client, String>{
    ClientDto get(String key);
    void set(String key, String value); // TODO check
}
