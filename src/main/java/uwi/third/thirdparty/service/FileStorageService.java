package uwi.third.thirdparty.service;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import org.apache.commons.io.FilenameUtils;
import org.apache.tomcat.util.http.fileupload.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import uwi.third.thirdparty.exceptions.FileStorageException;
import uwi.third.thirdparty.exceptions.InvalidExtensionException;
import uwi.third.thirdparty.exceptions.MyFileNotFoundException;

@Service
public class FileStorageService {

    private final Path fileStorageLocation;
    
    private final FileStorageProperties fileStorageProperties;
    private Path targetLocation;
    private Path newFileLocation;
    
    @Autowired
    public FileStorageService(FileStorageProperties fileStorageProperties) {
        this.fileStorageLocation = Paths.get(fileStorageProperties.getUploadDir())
                .toAbsolutePath().normalize();
        
        this.fileStorageProperties = fileStorageProperties;
        try {
            Files.createDirectories(this.fileStorageLocation);
        } catch (Exception ex) {
            throw new FileStorageException("Could not create the directory where the uploaded files will be stored.", ex);
        }
    }

    public String storeFile(MultipartFile file) throws InvalidExtensionException {
        // Normalize file name
    	
        String fileName = StringUtils.cleanPath(file.getOriginalFilename());
        String ext = FilenameUtils.getExtension(fileName); 
        if (!ext.equals("csv"))
        	throw new InvalidExtensionException("Invalid File Extension");
        try {
            // Check if the file's name contains invalid characters
            if(fileName.contains("..")) {
                throw new FileStorageException("Sorry! Filename contains invalid path sequence " + fileName);
            }

            // Copy file to the target location (Replacing existing file with the same name)
            targetLocation = this.fileStorageLocation.resolve(fileName);
            newFileLocation =this.fileStorageLocation.resolve(fileStorageProperties.getDownloadFile());
            
            File fl = new File(targetLocation.toString());
            File newfl = new File(newFileLocation.toString());
            
            if (fl.exists()) 
               FileUtils.forceDelete(new File(targetLocation.toString()));
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
            fl.renameTo(newfl);
            
            return newfl.getName();
        } catch (IOException ex) {
            throw new FileStorageException("Could not store file " + fileName + ". Please try again!", ex);
        }
    }

    public Resource loadFileAsResource(String fileName) {
        try {
            Path filePath = this.fileStorageLocation.resolve(fileName).normalize();
            Resource resource = new UrlResource(filePath.toUri());
            if(resource.exists()) {
                return resource;
            } else {
                throw new MyFileNotFoundException("File not found " + fileName);
            }
        } catch (MalformedURLException ex) {
            throw new MyFileNotFoundException("File not found " + fileName, ex);
        }
    }
}
