package com.phanta.waladom.shared.photo;

import org.springframework.stereotype.Service;

@Service
public class PhotosService {

    private final PhotoManagementService photoManagementService;

    public PhotosService(PhotoManagementService photoManagementService) {
        this.photoManagementService = photoManagementService;
    }

}
