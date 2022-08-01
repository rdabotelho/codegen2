package com.example.demo.web.rest;

import com.example.demo.model.Client;
import com.example.demo.repository.ClientRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api")
@Transactional
public class ClientResource {

    private final Logger log = LoggerFactory.getLogger(ClientResource.class);

    private final ClientRepository ClientRepository;

    public ClientResource(ClientRepository ClientRepository) {
        this.ClientRepository = ClientRepository;
    }

    @PostMapping("/Clients")
    public ResponseEntity<Client> createClient(@RequestBody Client Client) throws Exception {
        log.debug("REST request to save Client : {}", Client);
        if (Client.getId() != null) {
            throw new RuntimeException("A new Client cannot already have an ID");
        }
        Client result = ClientRepository.save(Client);
        return ResponseEntity
                .created(new URI("/api/Clients/" + result.getId()))
                .body(result);
    }

    @PutMapping("/Clients")
    public ResponseEntity<Client> updateClient(@RequestBody Client Client) {
        log.debug("REST request to update Client : {}", Client);
        if (Client.getId() == null) {
            throw new RuntimeException("Invalid id");
        }
        return ClientRepository.findById(Client.getId())
                .map(it -> ResponseEntity.ok().body(it))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/Clients")
    public List<Client> getAllClients() {
        log.debug("REST request to get all Clients");
        return ClientRepository.findAll();
    }

    @GetMapping("/Clients/{id}")
    public ResponseEntity<Client> getClient(@PathVariable Long id) {
        log.debug("REST request to get Client : {}", id);
        return ClientRepository.findById(id)
                .map(it -> ResponseEntity.ok().body(it))
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/Clients/{id}")
    public ResponseEntity<Void> deleteClient(@PathVariable Long id) {
        log.debug("REST request to delete Client : {}", id);
        ClientRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
    
}
