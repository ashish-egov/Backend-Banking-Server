package com.example.BackendBankingService.dao;


import com.example.BackendBankingService.model.Client;

import java.io.IOException;
import java.util.List;

public interface ClientDao {

    List<Client> getAllClients();

    Client getClientById(Long id);

    String addClient(Client client) throws IOException;

    String updateClient(Client client);

    String deleteClient(Long id);
}
