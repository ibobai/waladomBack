package com.phanta.waladom.shared.user;

import com.phanta.waladom.base.BaseUser;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserManagementService {

    @Transactional
    public <T extends BaseUser> T save(T entity, JpaRepository<T, String> repository) {
        // Shared save logic
        return repository.save(entity);
    }

    @Transactional
    public <T extends BaseUser> void delete(String id, JpaRepository<T, String> repository) {
        // Shared delete logic
        repository.deleteById(id);
    }

    public <T extends BaseUser> List<T> findAll(JpaRepository<T, String> repository) {
        // Shared retrieval logic
        return repository.findAll();
    }
}