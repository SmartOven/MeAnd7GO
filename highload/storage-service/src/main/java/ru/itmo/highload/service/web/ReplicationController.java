package ru.itmo.highload.service.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.itmo.highload.service.kv.KeyValueService;
import ru.itmo.highload.service.kv.lsm.storage.MemTableVersioned;

@Profile({"master", "replication-lsm"})
@RestController
@RequestMapping("/api/replication")
public class ReplicationController {
    @Autowired
    KeyValueService keyValueService;

    @GetMapping("/wal")
    public MemTableVersioned getWal() {
        return keyValueService.getVersionedMemTableWal();
    }
}
