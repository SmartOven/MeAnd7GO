package ru.itmo.highload.client;

import lombok.NonNull;
import org.springframework.stereotype.Service;

@Service
public class ClientMapper {
    public Client toClient(@NonNull ClientDto clientDto) {
        return new Client(
            clientDto.getKey(),
            clientDto.getValue()
        );
    }
    public ClientDto toClientDto(@NonNull Client client) {
        return new ClientDto(
            client.getKey(),
            client.getValue()
        );
    }
}
