package br.com.projeto.spring.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import br.com.projeto.spring.service.BulkInsertService;

@RestController
public class BulkInsertController {

    private final BulkInsertService bulkInsertService;

    public BulkInsertController(BulkInsertService bulkInsertService) {
        this.bulkInsertService = bulkInsertService;
    }

    @PostMapping("/bulk-insert-usuarios")
    @PreAuthorize("hasAuthority('usuario:create')")
    public String bulkInsert(

            @RequestParam(defaultValue = "100000")
            int records) {

        new Thread(() -> {
            try {
                bulkInsertService.bulkInsertUsuarios(records);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();

        return "Bulk insert started for " + records + " records";
    }
}
