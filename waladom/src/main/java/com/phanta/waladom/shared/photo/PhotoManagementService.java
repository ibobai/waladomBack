package com.phanta.waladom.shared.photo;

import com.phanta.waladom.base.BasePhoto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class PhotoManagementService {

    @Transactional
    public <T extends BasePhoto> T savePhoto(T photo, JpaRepository<T, String> repository) {
        return repository.save(photo);
    }

    @Transactional
    public <T extends BasePhoto> void deletePhoto(String id, JpaRepository<T, String> repository) {
        repository.deleteById(id);
    }

    public <T extends BasePhoto> List<T> findAllPhotos(JpaRepository<T, String> repository) {
        return repository.findAll();
    }

    public <T extends BasePhoto> T findPhotoById(String id, JpaRepository<T, String> repository) {
        return repository.findById(id).orElse(null);
    }
}
