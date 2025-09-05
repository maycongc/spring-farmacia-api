package br.com.projeto.spring.service.impl;

import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

import org.springframework.scheduling.annotation.Async;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.github.javafaker.Faker;

import br.com.projeto.spring.domain.model.Usuario;
import br.com.projeto.spring.service.BulkInsertService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BulkInsertServiceImpl implements BulkInsertService {

    @PersistenceContext
    private EntityManager entityManager;

    private final PasswordEncoder passwordEncoder;

    @Async
    @Transactional
    public void bulkInsertUsuarios(int totalRecords) {
        int batchSize = 1000;
        Faker faker = new Faker();

        for (int i = 0; i < totalRecords; i += batchSize) {
            List<Usuario> usuarios = new ArrayList<>();

            for (int j = 0; j < batchSize && (i + j) < totalRecords; j++) {
                usuarios.add(createFakeUsuario(faker, i + j));
            }

            // Insert batch
            for (int j = 0; j < usuarios.size(); j++) {
                entityManager.persist(usuarios.get(j));
                if (j % batchSize == 0 && j > 0) {
                    entityManager.flush();
                    entityManager.clear();
                }
            }
            entityManager.flush();
            entityManager.clear();

            System.out.println("Inserted " + Math.min(i + batchSize, totalRecords) + " records");
        }
    }

    private Usuario createFakeUsuario(Faker faker, Integer indexAleatorio) {
        Usuario usuario = new Usuario();
        usuario.setNome(faker.name().fullName());
        usuario.setUsername(faker.name().username() + indexAleatorio.toString());
        usuario.setSenha(passwordEncoder.encode(faker.internet().password()));
        usuario.setEmail(faker.internet().emailAddress());
        usuario.setCpf(faker.numerify("###########"));
        usuario.setDataNascimento(
                faker.date().birthday().toInstant().atZone(ZoneId.systemDefault()).toLocalDate().toString());
        return usuario;
    }
}
