package com.ttn.ecommerce.service;

import com.ttn.ecommerce.entity.User;
import com.ttn.ecommerce.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@Service
public class ImageService{

    Logger logger = LoggerFactory.getLogger(ImageService.class);

    @Value("${project.profile.image}")
    private String profileImageBucket;

    @Autowired
    UserRepository userRepository;

    public void saveImage(String email, MultipartFile image) throws IOException {
        logger.info("ImageService::saveImage execution started.");

        User user= userRepository.findUserByEmail(email);
        File dir = new File(profileImageBucket);
        if(!dir.exists()){
            dir.mkdir();
        }
        // setting filepath
        String filePath = profileImageBucket + File.separator + user.getEmail()+ ".png";
        // copying all bytes from an input stream to a file
        logger.debug("ImageService::saveImage saving image");
        Files.copy(image.getInputStream(), Paths.get(filePath),StandardCopyOption.REPLACE_EXISTING);
        logger.info("ImageService::saveImage execution ended.");

    }

    public byte[] getImage(String email) {
        logger.info("ImageService::getImage execution started.");
        final String imgName = email;
        String filename = profileImageBucket + File.separator + email + ".png";

        // check if dir contains associated file
        File dir = new File(profileImageBucket);
        File[] files = dir.listFiles(new FilenameFilter() {
            public boolean accept(File dir , String name) {
                return name.startsWith(imgName);
            }});

        // if no related image is found return empty byte array
        // else return byte array of the img
        if (files.length==0){
            logger.debug("ImageService::getImage no associated image found");
            logger.info("ImageService::getImage execution ended.");
            return new byte[0];
        }
        else {
            try {
                logger.debug("ImageService::getImage returning byte array of the image");
                // reads input bytes from a file in a file system
                InputStream inputStream = new FileInputStream(filename);
                logger.info("ImageService::getImage execution ended.");
                return inputStream.readAllBytes();
            } catch (FileNotFoundException e) {
                logger.error("ImageService::getImage An exception occurred while fetching the image");
                throw new RuntimeException(e);
            }
            catch (IOException e) {
                logger.error("ImageService::getImage An exception occurred while fetching the image");
                throw new RuntimeException(e);
            }
        }
    }
}
