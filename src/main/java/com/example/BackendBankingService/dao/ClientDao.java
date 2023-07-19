package com.example.BackendBankingService.dao;


import com.example.BackendBankingService.model.Client;

import java.util.List;

public interface ClientDao {

    List<Client> getAllClients();

    Client getClientById(Long id);

    String addClient(Client client);

    String updateClient(Client client);

    String deleteClient(Long id);
}
