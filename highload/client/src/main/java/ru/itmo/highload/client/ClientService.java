package ru.itmo.highload.client;

import org.springframework.stereotype.Service;

@Service
public class ClientService {
    private final ClientRepository clientRepository;
    private final ClientMapper clientMapper;
    public ClientService(
        ClientRepository clientRepository,
        ClientMapper clientMapper
    ) {
        this.clientRepository = clientRepository;
        this.clientMapper = clientMapper;
    }

    public Client get(String key) {
        return clientMapper.toClient(clientRepository.get(key));
    }

    public void set(String key, String value) {
        clientRepository.set(key, value);
    }

}
