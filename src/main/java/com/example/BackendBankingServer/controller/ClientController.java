package com.example.BackendBankingServer.controller;

import com.example.BackendBankingServer.dao.ClientDao;
import com.example.BackendBankingServer.model.Client;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/clients")
public class ClientController {

    @Autowired
    private ClientDao clientDao;

    @GetMapping
    public List<Client> getAllClients() {
        return clientDao.getAllClients();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Client> getClientById(@PathVariable Long id) {
        Client client = clientDao.getClientById(id);
        if (client != null) {
            return ResponseEntity.ok(client);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    public ResponseEntity<String> addClient(@RequestBody Client client) {
        String result = clientDao.addClient(client);
        HttpStatus status = HttpStatus.CREATED;
        if (result.startsWith("Failed")) {
            status = HttpStatus.BAD_REQUEST;
        }
        return ResponseEntity.status(status).body(result);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<String> updateClient(@PathVariable Long id, @RequestBody Client client) {
        Client existingClient = clientDao.getClientById(id);
        if (existingClient != null) {
            client.setAccountId(id);
            String message = clientDao.updateClient(client);
            return ResponseEntity.ok(message);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteClient(@PathVariable Long id) {
        String result = clientDao.deleteClient(id);
        if (result.equals("Client deleted")) {
            return ResponseEntity.status(HttpStatus.OK).body(result);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(result);
        }
    }
}